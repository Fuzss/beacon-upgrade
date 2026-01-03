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
import net.minecraft.resources.ResourceKey;
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

import java.util.Map;
import java.util.function.Function;

public class UpgradedBeaconBlockEntity extends BeaconBlockEntity implements TickingBlockEntity {
    public static final Codec<Object2IntMap<Holder<MobEffect>>> MOB_EFFECTS_CODEC = Codec.unboundedMap(BuiltInRegistries.MOB_EFFECT.holderByNameCodec(),
                    Codec.intRange(MobEffectInstance.MIN_AMPLIFIER, MobEffectInstance.MAX_AMPLIFIER))
            .xmap(Object2IntOpenHashMap::new, Function.identity());
    public static final String TAG_MOB_EFFECTS = "mob_effects";
    public static final String TAG_MIN_LEVEL_STRENGTHS = "min_level_strengths";

    private final Object2IntMap<Holder<MobEffect>> mobEffects = new Object2IntOpenHashMap<>();
    private final ContainerData effectAccess = new ContainerData() {
        @Override
        public int get(int index) {
            Map<ResourceKey<MobEffect>, BeaconLevelEffect> dataMap = DataMapLookup.getDataMap(BuiltInRegistries.MOB_EFFECT,
                    ModRegistry.BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE);
            int mapIndex = 0;
            for (Map.Entry<ResourceKey<MobEffect>, BeaconLevelEffect> entry : dataMap.entrySet()) {
                if (mapIndex++ == index) {
                    Holder.Reference<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.getOrThrow(entry.getKey());
                    return UpgradedBeaconBlockEntity.this.mobEffects.getInt(holder);
                }
            }

            return 0;
        }

        @Override
        public void set(int index, int value) {
            Map<ResourceKey<MobEffect>, BeaconLevelEffect> dataMap = DataMapLookup.getDataMap(BuiltInRegistries.MOB_EFFECT,
                    ModRegistry.BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE);
            int mapIndex = 0;
            for (Map.Entry<ResourceKey<MobEffect>, BeaconLevelEffect> entry : dataMap.entrySet()) {
                if (mapIndex++ == index) {
                    Holder.Reference<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.getOrThrow(entry.getKey());
                    int maxAmplifier = Math.round(entry.getValue()
                            .amplifier()
                            .calculate(UpgradedBeaconBlockEntity.this.levels));
                    UpgradedBeaconBlockEntity.this.mobEffects.put(holder, Math.min(value, maxAmplifier));
                    break;
                }
            }
        }

        @Override
        public int getCount() {
            return DataMapLookup.getDataMap(BuiltInRegistries.MOB_EFFECT,
                    ModRegistry.BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE).size();
        }
    };
    private int[] minLevelStrengths = new int[0];

    public UpgradedBeaconBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
        this.type = ModRegistry.BEACON_BLOCK_ENTITY_TYPE.value();
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModRegistry.BEACON_BLOCK_ENTITY_TYPE.value();
    }

    @Override
    public void clientTick() {
        tick(this.getLevel(), this.getBlockPos(), this.getBlockState(), this);
    }

    @Override
    public void serverTick() {
        tick(this.getLevel(), this.getBlockPos(), this.getBlockState(), this);
    }

    /**
     * @see BeaconBlockEntity#updateBase(Level, int, int, int)
     */
    private int[] getMinLevelStrengths(Level level, BlockPos blockPos) {
        int maxLevel = this.getMaxLevel();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        IntList beaconLevelStrengths = new IntArrayList();
        for (int beaconLevel = BeaconLevelEffect.MIN_BEACON_LEVEL; beaconLevel <= maxLevel; beaconLevel++) {
            int posY = blockPos.getY() - beaconLevel;
            if (posY < level.getMinY()) {
                break;
            }

            beaconLevelStrengths.add(BeaconLevelEffect.MAX_BEACON_LEVEL);
            for (int posX = blockPos.getX() - beaconLevel; posX <= blockPos.getX() + beaconLevel; ++posX) {
                for (int posZ = blockPos.getZ() - beaconLevel; posZ <= blockPos.getZ() + beaconLevel; ++posZ) {
                    BlockState blockState = level.getBlockState(mutableBlockPos.set(posX, posY, posZ));
                    BeaconBaseBlock beaconBaseBlock = BeaconBaseBlock.get(blockState);
                    if (beaconBaseBlock != null) {
                        int beaconLevelStrength = Math.round(beaconBaseBlock.strength().calculate(beaconLevel));
                        if (beaconLevelStrength < beaconLevelStrengths.getInt(beaconLevel - 1)) {
                            beaconLevelStrengths.set(beaconLevel - 1, beaconLevelStrength);
                        }
                    } else {
                        beaconLevelStrengths.removeInt(beaconLevel - 1);
                        return beaconLevelStrengths.toIntArray();
                    }
                }
            }
        }

        return beaconLevelStrengths.toIntArray();
    }

    private int getMaxLevel() {
        return DataMapLookup.getDataMap(BuiltInRegistries.MOB_EFFECT, ModRegistry.BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE)
                .values()
                .stream()
                .mapToInt(BeaconLevelEffect::minLevel)
                .max()
                .orElse(BeaconLevelEffect.MIN_BEACON_LEVEL);
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.minLevelStrengths = valueInput.getIntArray(TAG_MIN_LEVEL_STRENGTHS).orElseGet(() -> new int[0]);
        this.levels = this.minLevelStrengths.length;
        this.mobEffects.clear();
        valueInput.read(TAG_MOB_EFFECTS, MOB_EFFECTS_CODEC).ifPresent(this.mobEffects::putAll);
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        if (this.minLevelStrengths.length > 0) {
            valueOutput.putIntArray(TAG_MIN_LEVEL_STRENGTHS, this.minLevelStrengths);
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
                    this.dataAccess,
                    ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()));
        } else {
            return super.createMenu(containerId, inventory, player);
        }
    }
}
