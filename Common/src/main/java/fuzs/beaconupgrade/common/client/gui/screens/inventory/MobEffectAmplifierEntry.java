package fuzs.beaconupgrade.common.client.gui.screens.inventory;

import fuzs.beaconupgrade.common.BeaconUpgrade;
import fuzs.beaconupgrade.common.client.util.MobEffectTooltipHelper;
import fuzs.beaconupgrade.common.world.level.block.entity.BeaconLevelEffect;
import fuzs.beaconupgrade.common.world.level.block.entity.UpgradedBeaconBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;

import java.util.ArrayList;
import java.util.List;

public record MobEffectAmplifierEntry(int amplifier,
                                      int maxAmplifier,
                                      int availableAmplifier,
                                      int pyramidLevels,
                                      int minPyramidLevels) implements LevelBasedEntry<MobEffect> {
    public static final String PYRAMID_LEVELS_KEY = Util.makeDescriptionId("gui",
            BeaconUpgrade.id("beacon.tooltip.pyramid_levels"));
    public static final String PYRAMID_LEVELS_FRACTION_KEY = Util.makeDescriptionId("gui",
            BeaconUpgrade.id("beacon.tooltip.pyramid_levels_fraction"));
    public static final Component UNLOCK_EFFECT_COMPONENT = Component.translatable(Util.makeDescriptionId("gui",
            BeaconUpgrade.id("beacon.tooltip.unlock_effect"))).withStyle(ChatFormatting.GRAY);

    public static MobEffectAmplifierEntry create(Holder<MobEffect> holder, int amplifier, int pyramidLevels) {
        int maxAmplifier = BeaconLevelEffect.get(holder).getMaxAmplifier();
        int availableAmplifier = BeaconLevelEffect.get(holder).getMaxAmplifier(pyramidLevels);
        int minPyramidLevels = BeaconLevelEffect.get(holder)
                .getRequiredPyramidLevels(Math.min(availableAmplifier + 1, maxAmplifier));
        return new MobEffectAmplifierEntry(amplifier,
                maxAmplifier,
                availableAmplifier,
                pyramidLevels,
                minPyramidLevels);
    }

    @Override
    public int level() {
        return this.amplifier;
    }

    @Override
    public int maxLevel() {
        return this.maxAmplifier;
    }

    @Override
    public int availableLevel() {
        return this.availableAmplifier;
    }

    @Override
    public int getZeroIndex() {
        return UpgradedBeaconBlockEntity.DEFAULT_AMPLIFIER;
    }

    @Override
    public Component getDisplayName(Holder<MobEffect> holder, int maxWidth, int seed) {
        return MobEffectTooltipHelper.getSpriteDisplayName(holder, this.amplifier);
    }

    @Override
    public List<Component> getTooltip(Holder<MobEffect> holder) {
        if (this.isNotAvailable()) {
            return this.getWeakPowerTooltip(UNLOCK_EFFECT_COMPONENT);
        } else {
            return MobEffectTooltipHelper.getMobEffectTooltip(holder);
        }
    }

    @Override
    public List<Component> getWeakPowerTooltip(Component component) {
        List<Component> tooltipLines = new ArrayList<>();
        if (this.pyramidLevels > 0 && this.minPyramidLevels > 0) {
            Component pyramidLevelsComponent = MobEffectTooltipHelper.getEnchantmentLevel(this.pyramidLevels);
            Component minPyramidLevelsComponent = MobEffectTooltipHelper.getEnchantmentLevel(this.minPyramidLevels);
            tooltipLines.add(Component.translatable(PYRAMID_LEVELS_FRACTION_KEY,
                    pyramidLevelsComponent,
                    minPyramidLevelsComponent).withStyle(ChatFormatting.RED));
        }

        tooltipLines.add(component);
        return tooltipLines;
    }
}
