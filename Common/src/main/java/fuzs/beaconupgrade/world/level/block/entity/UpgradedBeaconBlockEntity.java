package fuzs.beaconupgrade.world.level.block.entity;

import com.mojang.serialization.Codec;
import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.beaconupgrade.world.inventory.UpgradedBeaconMenu;
import fuzs.beaconupgrade.world.level.block.BeaconBaseBlock;
import fuzs.beaconupgrade.world.level.block.BeaconLevelEffect;
import fuzs.neoforgedatapackextensions.api.v2.DataMapLookup;
import fuzs.puzzleslib.api.block.v1.entity.TickingBlockEntity;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public class UpgradedBeaconBlockEntity extends BeaconBlockEntity implements TickingBlockEntity {
    /**
     * Always require a level of at least one, as the beacon structure itself will not activate with less.
     */
    public static final int MIN_BEACON_LEVELS = 1;
    /**
     * Pick some reasonable value, as the number of blocks required for checking on the pyramid gets out of hand quickly
     * otherwise.
     */
    public static final int MAX_BEACON_LEVELS = 5;
    public static final int POWER_LEVELS_DATA_SLOTS = MAX_BEACON_LEVELS + 1;
    public static final Codec<Object2IntMap<Holder<MobEffect>>> MOB_EFFECTS_CODEC = Codec.unboundedMap(MobEffect.CODEC,
                    Codec.intRange(MobEffectInstance.MIN_AMPLIFIER, MobEffectInstance.MAX_AMPLIFIER))
            .xmap(Object2IntOpenHashMap::new, Function.identity());
    public static final StreamCodec<RegistryFriendlyByteBuf, Object2IntMap<Holder<MobEffect>>> MOB_EFFECTS_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2IntOpenHashMap::new, MobEffect.STREAM_CODEC, ByteBufCodecs.VAR_INT),
            Object2IntOpenHashMap::new,
            Function.identity());
    public static final String TAG_LEVEL_STRENGTHS = "power_levels";
    public static final String TAG_MOB_EFFECTS = "mob_effects";

    private final Object2IntMap<Holder<MobEffect>> mobEffects = new Object2IntOpenHashMap<>();
    private final ContainerData mobEffectsData = new ContainerData() {
        @Override
        public int get(int index) {
            MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.byId(index);
            if (mobEffect != null) {
                return UpgradedBeaconBlockEntity.this.mobEffects.getInt(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                        mobEffect));
            } else {
                return -1;
            }
        }

        @Override
        public void set(int index, int value) {
            MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.byId(index);
            if (mobEffect != null) {
                Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(mobEffect);
                int maxAmplifier = BeaconLevelEffect.getMaxAmplifier(holder,
                        UpgradedBeaconBlockEntity.this.powerLevels.length);
                int newAmplifier = Math.min(maxAmplifier, value);
                if (newAmplifier >= 0) {
                    UpgradedBeaconBlockEntity.this.mobEffects.put(holder, newAmplifier);
                } else {
                    UpgradedBeaconBlockEntity.this.mobEffects.removeInt(holder);
                }
            }
        }

        @Override
        public int getCount() {
            return BuiltInRegistries.MOB_EFFECT.size();
        }
    };
    private final ContainerData powerLevelsData = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == DATA_LEVELS) {
                return UpgradedBeaconBlockEntity.this.powerLevels.length;
            } else if (index > DATA_LEVELS && index - 1 < UpgradedBeaconBlockEntity.this.powerLevels.length) {
                return UpgradedBeaconBlockEntity.this.powerLevels[index - 1];
            } else {
                return -1;
            }
        }

        @Override
        public void set(int index, int value) {
            if (index > DATA_LEVELS && index - 1 < UpgradedBeaconBlockEntity.this.powerLevels.length) {
                UpgradedBeaconBlockEntity.this.powerLevels[index - 1] = value;
            }
        }

        @Override
        public int getCount() {
            return POWER_LEVELS_DATA_SLOTS;
        }
    };
    private int[] powerLevels = new int[0];

    public UpgradedBeaconBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
        this.type = ModRegistry.BEACON_BLOCK_ENTITY_TYPE.value();
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModRegistry.BEACON_BLOCK_ENTITY_TYPE.value();
    }

    @Override
    public void clientTick(Level level, BlockPos blockPos, BlockState blockState) {
        tick(level, blockPos, blockState, this);
    }

    @Override
    public void serverTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        tick(serverLevel, blockPos, blockState, this);
    }

    public int[] updatePowerLevels(Level level, BlockPos blockPos) {
        return this.powerLevels = this.getPowerLevels(level, blockPos);
    }

    /**
     * @see BeaconBlockEntity#updateBase(Level, int, int, int)
     */
    private int[] getPowerLevels(Level level, BlockPos blockPos) {
        int maxLevel = this.getMaxLevel();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        IntList powerLevels = new IntArrayList();
        for (int levels = MIN_BEACON_LEVELS; levels <= maxLevel; levels++) {
            int posY = blockPos.getY() - levels;
            if (posY < level.getMinY()) {
                break;
            }

            powerLevels.add(MAX_BEACON_LEVELS);
            for (int posX = blockPos.getX() - levels; posX <= blockPos.getX() + levels; ++posX) {
                for (int posZ = blockPos.getZ() - levels; posZ <= blockPos.getZ() + levels; ++posZ) {
                    BlockState blockState = level.getBlockState(mutableBlockPos.set(posX, posY, posZ));
                    BeaconBaseBlock beaconBaseBlock = BeaconBaseBlock.get(blockState);
                    if (beaconBaseBlock != null) {
                        int powerLevel = Math.round(beaconBaseBlock.power().calculate(levels));
                        if (powerLevel < powerLevels.getInt(levels - 1)) {
                            powerLevels.set(levels - 1, powerLevel);
                        }
                    } else {
                        powerLevels.removeInt(levels - 1);
                        return powerLevels.toIntArray();
                    }
                }
            }
        }

        return powerLevels.toIntArray();
    }

    private int getMaxLevel() {
        return DataMapLookup.getDataMap(BuiltInRegistries.MOB_EFFECT, ModRegistry.BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE)
                .values()
                .stream()
                .mapToInt(BeaconLevelEffect::getMinLevels)
                .max()
                .orElse(MIN_BEACON_LEVELS);
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.powerLevels = valueInput.getIntArray(TAG_LEVEL_STRENGTHS).orElseGet(() -> new int[0]);
        this.levels = this.powerLevels.length;
        this.mobEffects.clear();
        valueInput.read(TAG_MOB_EFFECTS, MOB_EFFECTS_CODEC).ifPresent((Object2IntMap<Holder<MobEffect>> mobEffects) -> {
            for (Object2IntMap.Entry<Holder<MobEffect>> entry : mobEffects.object2IntEntrySet()) {
                int maxAmplifier = BeaconLevelEffect.getMaxAmplifier(entry.getKey(), this.powerLevels.length);
                int newAmplifier = Math.min(maxAmplifier, entry.getIntValue());
                if (newAmplifier >= 0) {
                    this.mobEffects.put(entry.getKey(), newAmplifier);
                }
            }
        });
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        if (this.powerLevels.length > 0) {
            valueOutput.putIntArray(TAG_LEVEL_STRENGTHS, this.powerLevels);
        }

        if (!this.mobEffects.isEmpty()) {
            valueOutput.store(TAG_MOB_EFFECTS, MOB_EFFECTS_CODEC, this.mobEffects);
        }
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        if (this.lockKey.canUnlock(player)) {
            return new UpgradedBeaconMenu(containerId,
                    inventory,
                    this.powerLevelsData,
                    this.mobEffectsData,
                    ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()));
        } else {
            return super.createMenu(containerId, inventory, player);
        }
    }
}
