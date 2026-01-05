package fuzs.beaconupgrade.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.beaconupgrade.world.item.enchantment.ClampedLevelBasedValue;
import fuzs.beaconupgrade.world.level.block.entity.UpgradedBeaconBlockEntity;
import fuzs.neoforgedatapackextensions.api.v2.DataMapLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jspecify.annotations.Nullable;

public record BeaconBaseBlock(LevelBasedValue pyramidLevelBonus) {
    public static final Codec<BeaconBaseBlock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    LevelBasedValue.CODEC.fieldOf("power_level").forGetter(BeaconBaseBlock::pyramidLevelBonus))
            .apply(instance, BeaconBaseBlock::new));
    public static final BeaconBaseBlock DEFAULT = new BeaconBaseBlock(1);

    public BeaconBaseBlock(int power) {
        this(new ClampedLevelBasedValue(LevelBasedValue.constant(power),
                LevelBasedValue.constant(0.0F),
                LevelBasedValue.perLevel(1.0F)));
    }

    public int getMaxPowerLevel() {
        return this.getPowerLevel(UpgradedBeaconBlockEntity.MAX_PYRAMID_LEVELS);
    }

    public int getPowerLevel(int pyramidLevels) {
        return Math.round(this.pyramidLevelBonus.calculate(pyramidLevels));
    }

    public static boolean is(BlockState blockState) {
        return get(blockState) != null;
    }

    public static @Nullable BeaconBaseBlock get(BlockState blockState) {
        BeaconBaseBlock beaconBaseBlock = DataMapLookup.getData(ModRegistry.BEACON_BASE_BLOCKS_DATA_MAP_TYPE,
                blockState.getBlockHolder());
        if (beaconBaseBlock != null) {
            return beaconBaseBlock;
        } else if (blockState.is(BlockTags.BEACON_BASE_BLOCKS)) {
            return BeaconBaseBlock.DEFAULT;
        } else {
            return null;
        }
    }

    public static int getMaxPowerLevels() {
        int maxPowerLevels = 0;
        for (MutableInt levels = new MutableInt(UpgradedBeaconBlockEntity.MIN_PYRAMID_LEVELS);
             levels.intValue() <= UpgradedBeaconBlockEntity.MAX_PYRAMID_LEVELS; levels.increment()) {
            maxPowerLevels += DataMapLookup.getDataMap(BuiltInRegistries.BLOCK,
                            ModRegistry.BEACON_BASE_BLOCKS_DATA_MAP_TYPE)
                    .values()
                    .stream()
                    .mapToInt((BeaconBaseBlock beaconBaseBlock) -> {
                        return beaconBaseBlock.getPowerLevel(levels.intValue());
                    })
                    .max()
                    .orElse(0);
        }

        return maxPowerLevels;
    }
}
