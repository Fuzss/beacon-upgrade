package fuzs.beaconupgrade.client.gui.screens.inventory;

import fuzs.beaconupgrade.BeaconUpgrade;
import fuzs.beaconupgrade.client.util.MobEffectTooltipHelper;
import fuzs.beaconupgrade.world.level.block.BeaconLevelEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;

import java.util.ArrayList;
import java.util.List;

public record MobEffectAmplifierEntry(int mobEffectAmplifier,
                                      int maxMobEffectAmplifier,
                                      int pyramidLevels,
                                      int minPyramidLevels) implements LevelBasedEntry<MobEffect> {
    public static final Component UNKNOWN_ENCHANT_COMPONENT = Component.translatable(Util.makeDescriptionId("gui",
            BeaconUpgrade.id("mob_effect.tooltip.unknown_enchantment"))).withStyle(ChatFormatting.GRAY);
    public static final String KEY_CURRENT_ENCHANTING_POWER = Util.makeDescriptionId("gui",
            BeaconUpgrade.id("mob_effect.tooltip.current_enchanting_power"));

    public static MobEffectAmplifierEntry create(Holder<MobEffect> holder, int amplifier, int pyramidLevels) {
        int maxAmplifier = BeaconLevelEffect.getMaxAmplifier(holder);
        int minLevels = BeaconLevelEffect.get(holder).getMinPyramidLevels();
        return new MobEffectAmplifierEntry(amplifier, maxAmplifier, pyramidLevels, minLevels);
    }

    @Override
    public int level() {
        return this.mobEffectAmplifier;
    }

    @Override
    public int maxLevel() {
        return this.maxMobEffectAmplifier;
    }

    @Override
    public int availableLevel() {
        return this.pyramidLevels >= this.minPyramidLevels ? this.maxMobEffectAmplifier : this.getZeroIndex();
    }

    @Override
    public int getZeroIndex() {
        return -1;
    }

    @Override
    public Component getDisplayName(Holder<MobEffect> holder, int maxWidth, int seed) {
        if (this.isPresent()) {
            return MobEffectTooltipHelper.getDisplayNameWithLevel(holder, this.mobEffectAmplifier);
        } else {
            return MobEffectTooltipHelper.getDisplayName(holder);
        }
    }

    @Override
    public List<Component> getTooltip(Holder<MobEffect> holder) {
        if (this.isNotAvailable()) {
            return this.getWeakPowerTooltip(UNKNOWN_ENCHANT_COMPONENT);
        } else {
            return MobEffectTooltipHelper.getMobEffectTooltip(holder);
        }
    }

    @Override
    public List<Component> getWeakPowerTooltip(Component component) {
        List<Component> tooltipLines = new ArrayList<>();
        Component currentPowerComponent = Component.literal(String.valueOf(this.pyramidLevels))
                .withStyle(ChatFormatting.RED);
        Component requiredPowerComponent = Component.literal(String.valueOf(this.minPyramidLevels));
        tooltipLines.add(Component.translatable(KEY_CURRENT_ENCHANTING_POWER,
                currentPowerComponent,
                requiredPowerComponent));
        tooltipLines.add(component);
        return tooltipLines;
    }
}
