package fuzs.beaconupgrade.common.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.beaconupgrade.common.BeaconUpgrade;
import fuzs.beaconupgrade.common.init.ModRegistry;
import fuzs.beaconupgrade.common.world.item.enchantment.ClampedLevelBasedValue;
import fuzs.neoforgedatapackextensions.api.v1.DataMapRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record BeaconBaseBlock(LevelBasedValue pyramidStrength,
                              LevelBasedValue effectiveRadius) implements BeaconTooltipProvider {
    public static final Codec<BeaconBaseBlock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    LevelBasedValue.CODEC.fieldOf("pyramid_strength").forGetter(BeaconBaseBlock::pyramidStrength),
                    LevelBasedValue.CODEC.fieldOf("effective_radius").forGetter(BeaconBaseBlock::effectiveRadius))
            .apply(instance, BeaconBaseBlock::new));
    public static final BeaconBaseBlock DEFAULT = new BeaconBaseBlock(1, 10);
    public static final Component PYRAMID_STRENGTH_COMPONENT = Component.translatable(Util.makeDescriptionId("gui",
            BeaconUpgrade.id("beacon.tooltip.pyramid_strength")));
    public static final String PYRAMID_STRENGTH_POTENTIAL_KEY = Util.makeDescriptionId("gui",
            BeaconUpgrade.id("beacon.tooltip.pyramid_strength_potential"));
    public static final String PYRAMID_STRENGTH_REQUIREMENT_KEY = Util.makeDescriptionId("gui",
            BeaconUpgrade.id("beacon.tooltip.pyramid_strength_requirement"));
    public static final Component PYRAMID_STRENGTH_DESCRIPTION_COMPONENT = Component.translatable(Util.makeDescriptionId(
            "gui",
            BeaconUpgrade.id("beacon.tooltip.pyramid_strength_description"))).withStyle(ChatFormatting.GRAY);
    public static final Component EFFECTIVE_RADIUS_COMPONENT = Component.translatable(Util.makeDescriptionId("gui",
            BeaconUpgrade.id("beacon.tooltip.effective_radius")));

    public BeaconBaseBlock(int pyramidStrength, int effectiveRadius) {
        this(new ClampedLevelBasedValue(LevelBasedValue.constant(pyramidStrength),
                LevelBasedValue.constant(0.0F),
                LevelBasedValue.perLevel(1.0F)), LevelBasedValue.constant(effectiveRadius));
    }

    public int getMaxPyramidStrength() {
        return this.getPyramidStrength(UpgradedBeaconBlockEntity.MAX_PYRAMID_LEVELS);
    }

    public int getPyramidStrength(int pyramidLevels) {
        return Math.round(this.pyramidStrength.calculate(pyramidLevels));
    }

    public int getEffectiveRadius(int pyramidLevels) {
        return Math.round(this.effectiveRadius.calculate(pyramidLevels));
    }

    @Override
    public void addToTooltip(int pyramidLevels, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        int pyramidStrength = this.getPyramidStrength(pyramidLevels);
        if (pyramidStrength > 0) {
            Component component = this.getPyramidStrengthComponent(pyramidStrength);
            tooltipAdder.accept(BeaconTooltipProvider.getAttributeModifierComponent(pyramidStrength, component));
        }

        int effectiveRadius = this.getEffectiveRadius(pyramidLevels);
        if (effectiveRadius > 0) {
            tooltipAdder.accept(BeaconTooltipProvider.getAttributeModifierComponent(effectiveRadius,
                    EFFECTIVE_RADIUS_COMPONENT));
        }
    }

    private Component getPyramidStrengthComponent(int pyramidStrength) {
        int maxPyramidStrength = this.getMaxPyramidStrength();
        return Component.translatable(PYRAMID_STRENGTH_POTENTIAL_KEY,
                PYRAMID_STRENGTH_COMPONENT,
                pyramidStrength,
                maxPyramidStrength);
    }

    public static @Nullable BeaconBaseBlock get(Holder<Block> holder) {
        BeaconBaseBlock beaconBaseBlock = DataMapRegistry.INSTANCE.getData(ModRegistry.BEACON_BASE_BLOCKS_DATA_MAP_TYPE,
                holder);
        if (beaconBaseBlock != null) {
            return beaconBaseBlock;
        } else if (holder.is(BlockTags.BEACON_BASE_BLOCKS)) {
            return BeaconBaseBlock.DEFAULT;
        } else {
            return null;
        }
    }
}
