package fuzs.beaconupgrade.client.gui.screens.inventory;

import fuzs.beaconupgrade.BeaconUpgrade;
import fuzs.beaconupgrade.client.util.MobEffectTooltipHelper;
import fuzs.beaconupgrade.world.level.block.BeaconLevelEffect;
import fuzs.beaconupgrade.world.level.block.entity.UpgradedBeaconBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;

import java.util.List;

public record MobEffectAmplifierEntry(int amplifier,
                                      int maxAmplifier,
                                      int availableAmplifier,
                                      int levels,
                                      int minLevels) implements LevelBasedEntry<MobEffect> {
    public static final Component UNKNOWN_ENCHANT_COMPONENT = Component.translatable(Util.makeDescriptionId("gui",
            BeaconUpgrade.id("mob_effect.tooltip.unknown_enchantment"))).withStyle(ChatFormatting.GRAY);

    public static MobEffectAmplifierEntry create(Holder<MobEffect> holder, int amplifier, int levels) {
        int maxLevel = BeaconLevelEffect.getMaxAmplifier(holder, UpgradedBeaconBlockEntity.MAX_BEACON_LEVELS);
        int availableLevel = BeaconLevelEffect.getMaxAmplifier(holder, levels);
        int minLevels = BeaconLevelEffect.get(holder).getMinLevels();
        return new MobEffectAmplifierEntry(amplifier, maxLevel, availableLevel, levels, minLevels);
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
    public Component getDisplayName(Holder<MobEffect> holder, int maxWidth, int seed) {
        if (this.isPresent()) {
            return MobEffectTooltipHelper.getDisplayNameWithLevel(holder, this.amplifier, this.levels);
        } else {
            return MobEffectTooltipHelper.getDisplayName(holder);
        }
    }

    @Override
    public List<Component> getTooltip(Holder<MobEffect> holder) {
        if (this.isNotAvailable()) {
            return this.getWeakPowerTooltip(UNKNOWN_ENCHANT_COMPONENT);
        } else {
            return MobEffectTooltipHelper.getMobEffectTooltip(holder, this.levels);
        }
    }

    @Override
    public List<Component> getWeakPowerTooltip(Component component) {
        return MobEffectTooltipHelper.getWeakPowerTooltip(this.levels, this.minLevels, component);
    }
}
