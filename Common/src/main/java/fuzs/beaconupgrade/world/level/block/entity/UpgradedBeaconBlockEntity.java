package fuzs.beaconupgrade.world.level.block.entity;

import com.mojang.serialization.Codec;
import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.beaconupgrade.world.inventory.UpgradedBeaconMenu;
import fuzs.beaconupgrade.world.level.block.BeaconBaseBlock;
import fuzs.beaconupgrade.world.level.block.BeaconLevelEffect;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public class UpgradedBeaconBlockEntity extends BeaconBlockEntity implements TickingBlockEntity {
    /**
     * The default {@link MobEffectInstance} amplifier for mob effects that are not present.
     *
     * @see MobEffectInstance#MIN_AMPLIFIER
     * @see MobEffectInstance#MAX_AMPLIFIER
     */
    public static final int DEFAULT_AMPLIFIER = -1;
    /**
     * Always require a level of at least one, as the beacon structure itself will not activate with less.
     */
    public static final int MIN_PYRAMID_LEVELS = 1;
    /**
     * Pick some reasonable value, as the number of blocks required for checking on the pyramid gets out of hand quickly
     * otherwise.
     */
    public static final int MAX_PYRAMID_LEVELS = 5;
    public static final int PYRAMID_LEVELS_DATA_SLOT = DATA_LEVELS;
    public static final int ALL_POWER_LEVELS_DATA_SLOT = 1;
    public static final int EXTRA_LEVELS_DATA_SLOTS = 2;
    public static final int LEVELS_DATA_SLOTS = MAX_PYRAMID_LEVELS + EXTRA_LEVELS_DATA_SLOTS;
    public static final Codec<Object2IntMap<Holder<MobEffect>>> MOB_EFFECTS_CODEC = Codec.unboundedMap(MobEffect.CODEC,
                    Codec.intRange(MobEffectInstance.MIN_AMPLIFIER, MobEffectInstance.MAX_AMPLIFIER))
            .xmap(Object2IntOpenHashMap::new, Function.identity());
    public static final StreamCodec<RegistryFriendlyByteBuf, Object2IntMap<Holder<MobEffect>>> MOB_EFFECTS_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2IntOpenHashMap::new, MobEffect.STREAM_CODEC, ByteBufCodecs.VAR_INT),
            Object2IntOpenHashMap::new,
            Function.identity());
    public static final String TAG_LEVEL_STRENGTHS = "power_levels";
    public static final String TAG_MOB_EFFECTS = "mob_effects";
    /**
     * @see BeaconBlockEntity#TAG_PRIMARY
     */
    private static final String TAG_PRIMARY = "primary_effect";
    /**
     * @see BeaconBlockEntity#TAG_SECONDARY
     */
    private static final String TAG_SECONDARY = "secondary_effect";

    private final Object2IntMap<Holder<MobEffect>> mobEffects;
    private final ContainerData mobEffectsData = new ContainerData() {
        @Override
        public int get(int index) {
            MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.byId(index);
            if (mobEffect != null) {
                Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(mobEffect);
                return UpgradedBeaconBlockEntity.this.mobEffects.getInt(holder);
            } else {
                return DEFAULT_AMPLIFIER;
            }
        }

        @Override
        public void set(int index, int value) {
            MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.byId(index);
            if (mobEffect != null) {
                Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(mobEffect);
                UpgradedBeaconBlockEntity.this.setMobEffectAmplifier(holder, value);
            }
        }

        @Override
        public int getCount() {
            return BuiltInRegistries.MOB_EFFECT.size();
        }
    };
    private final ContainerData pyramidLevelsData = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == PYRAMID_LEVELS_DATA_SLOT) {
                return UpgradedBeaconBlockEntity.this.pyramidLevels.length;
            } else if (index == ALL_POWER_LEVELS_DATA_SLOT) {
                return UpgradedBeaconBlockEntity.this.getAllPowerLevels();
            } else if (index - EXTRA_LEVELS_DATA_SLOTS >= 0
                    && index - EXTRA_LEVELS_DATA_SLOTS < UpgradedBeaconBlockEntity.this.pyramidLevels.length) {
                return UpgradedBeaconBlockEntity.this.pyramidLevels[index - EXTRA_LEVELS_DATA_SLOTS];
            } else {
                return -1;
            }
        }

        @Override
        public void set(int index, int value) {
            if (index - EXTRA_LEVELS_DATA_SLOTS >= 0
                    && index - EXTRA_LEVELS_DATA_SLOTS < UpgradedBeaconBlockEntity.this.pyramidLevels.length) {
                UpgradedBeaconBlockEntity.this.pyramidLevels[index - EXTRA_LEVELS_DATA_SLOTS] = value;
            }
        }

        @Override
        public int getCount() {
            return LEVELS_DATA_SLOTS;
        }
    };
    /**
     * The stored integers represent the numeric block ids (via {@link net.minecraft.core.IdMap#getId(Object)}) for the
     * block on that layer with the lowest value returned from {@link BeaconBaseBlock#getMaxPyramidLevelBonus()}.
     *
     * @see #getPyramidLevels(Level, BlockPos)
     */
    private int[] pyramidLevels = new int[0];

    public UpgradedBeaconBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
        this.type = ModRegistry.BEACON_BLOCK_ENTITY_TYPE.value();
        this.mobEffects = new Object2IntOpenHashMap<>();
        this.mobEffects.defaultReturnValue(DEFAULT_AMPLIFIER);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModRegistry.BEACON_BLOCK_ENTITY_TYPE.value();
    }

    public int getPyramidLevels() {
        return this.pyramidLevels.length;
    }

    public int getAllPowerLevels() {
        int allPowerLevel = 0;
        for (int powerLevel : this.pyramidLevels) {
            allPowerLevel += powerLevel;
        }

        return allPowerLevel;
    }

    private void setMobEffectAmplifier(Holder<MobEffect> mobEffect, int amplifier) {
        setMobEffectAmplifier(this.mobEffects, this.getPyramidLevels(), mobEffect, amplifier);
    }

    public static void setMobEffectAmplifier(Object2IntMap<Holder<MobEffect>> mobEffects, int pyramidLevels, Holder<MobEffect> mobEffect, int amplifier) {
        int maxAmplifier = BeaconLevelEffect.get(mobEffect).getMaxAmplifier(pyramidLevels);
        amplifier = Math.clamp(amplifier, DEFAULT_AMPLIFIER, maxAmplifier);
        if (amplifier >= MobEffectInstance.MIN_AMPLIFIER) {
            mobEffects.put(mobEffect, amplifier);
        } else {
            mobEffects.removeInt(mobEffect);
        }
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
        return this.pyramidLevels = this.getPyramidLevels(level, blockPos);
    }

    /**
     * @see BeaconBlockEntity#updateBase(Level, int, int, int)
     */
    private int[] getPyramidLevels(Level level, BlockPos blockPos) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        IntList powerBlocks = new IntArrayList();
        for (int pyramidLevel = MIN_PYRAMID_LEVELS; pyramidLevel <= MAX_PYRAMID_LEVELS; pyramidLevel++) {
            int posY = blockPos.getY() - pyramidLevel;
            if (posY < level.getMinY()) {
                break;
            }

            int minPowerLevel = Integer.MAX_VALUE;
            Block minPowerBlock = null;
            for (int posX = blockPos.getX() - pyramidLevel; posX <= blockPos.getX() + pyramidLevel; ++posX) {
                for (int posZ = blockPos.getZ() - pyramidLevel; posZ <= blockPos.getZ() + pyramidLevel; ++posZ) {
                    BlockState blockState = level.getBlockState(mutableBlockPos.set(posX, posY, posZ));
                    if (minPowerBlock == null || !blockState.is(minPowerBlock)) {
                        BeaconBaseBlock beaconBaseBlock = BeaconBaseBlock.get(blockState);
                        if (beaconBaseBlock != null) {
                            int powerLevel = beaconBaseBlock.getMaxPyramidLevelBonus();
                            if (powerLevel < minPowerLevel) {
                                minPowerLevel = powerLevel;
                                minPowerBlock = blockState.getBlock();
                            }
                        } else {
                            return powerBlocks.toIntArray();
                        }
                    }
                }
            }

            if (minPowerBlock != null) {
                powerBlocks.add(BuiltInRegistries.BLOCK.getIdOrThrow(minPowerBlock));
            } else {
                return powerBlocks.toIntArray();
            }
        }

        return powerBlocks.toIntArray();
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.pyramidLevels = valueInput.getIntArray(TAG_LEVEL_STRENGTHS).orElseGet(() -> new int[0]);
        this.levels = this.pyramidLevels.length;
        this.mobEffects.clear();
        valueInput.read(TAG_MOB_EFFECTS, MOB_EFFECTS_CODEC).ifPresent((Object2IntMap<Holder<MobEffect>> mobEffects) -> {
            mobEffects.forEach(this::setMobEffectAmplifier);
        });
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        valueOutput.discard(TAG_PRIMARY);
        valueOutput.discard(TAG_SECONDARY);
        if (this.pyramidLevels.length > 0) {
            valueOutput.putIntArray(TAG_LEVEL_STRENGTHS, this.pyramidLevels);
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
                    this.pyramidLevelsData,
                    this.mobEffectsData,
                    ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()));
        } else {
            return super.createMenu(containerId, inventory, player);
        }
    }
}
