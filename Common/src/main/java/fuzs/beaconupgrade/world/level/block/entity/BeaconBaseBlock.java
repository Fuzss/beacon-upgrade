package fuzs.beaconupgrade.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.beaconupgrade.world.item.enchantment.ClampedLevelBasedValue;
import fuzs.neoforgedatapackextensions.api.v2.DataMapLookup;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jspecify.annotations.Nullable;

public record BeaconBaseBlock(LevelBasedValue pyramidLevelBonus, LevelBasedValue effectiveRadius) {
    public static final Codec<BeaconBaseBlock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    LevelBasedValue.CODEC.fieldOf("pyramid_level_bonus").forGetter(BeaconBaseBlock::pyramidLevelBonus),
                    LevelBasedValue.CODEC.fieldOf("effective_radius").forGetter(BeaconBaseBlock::effectiveRadius))
            .apply(instance, BeaconBaseBlock::new));
    public static final BeaconBaseBlock DEFAULT = new BeaconBaseBlock(1, 10);

    public BeaconBaseBlock(int pyramidLevelBonus, int effectiveRadius) {
        this(new ClampedLevelBasedValue(LevelBasedValue.constant(pyramidLevelBonus),
                LevelBasedValue.constant(0.0F),
                LevelBasedValue.perLevel(1.0F)), LevelBasedValue.perLevel(effectiveRadius * 2.0F, effectiveRadius));
    }

    public int getMaxPyramidLevelBonus() {
        return this.getPyramidLevelBonus(UpgradedBeaconBlockEntity.MAX_PYRAMID_LEVELS);
    }

    public int getPyramidLevelBonus(int pyramidLevels) {
        return Math.round(this.pyramidLevelBonus.calculate(pyramidLevels));
    }

    public int getEffectiveRadius(int pyramidLevels) {
        return Math.round(this.effectiveRadius.calculate(pyramidLevels));
    }

    public int getEffectiveRadiusAtLevel(int pyramidLevel) {
        // LevelBasedValue returns a sum including all lower levels; we only want a particular level.
        if (pyramidLevel > UpgradedBeaconBlockEntity.MIN_PYRAMID_LEVELS) {
            return this.getEffectiveRadius(pyramidLevel) - this.getEffectiveRadius(pyramidLevel - 1);
        } else {
            return this.getEffectiveRadius(pyramidLevel);
        }
    }

    public static @Nullable BeaconBaseBlock get(Holder<Block> holder) {
        BeaconBaseBlock beaconBaseBlock = DataMapLookup.getData(ModRegistry.BEACON_BASE_BLOCKS_DATA_MAP_TYPE, holder);
        if (beaconBaseBlock != null) {
            return beaconBaseBlock;
        } else if (holder.is(BlockTags.BEACON_BASE_BLOCKS)) {
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
                        return beaconBaseBlock.getPyramidLevelBonus(levels.intValue());
                    })
                    .max()
                    .orElse(0);
        }

        return maxPowerLevels;
    }
}
