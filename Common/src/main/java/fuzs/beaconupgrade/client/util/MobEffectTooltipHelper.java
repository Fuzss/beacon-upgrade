package fuzs.beaconupgrade.client.util;

import fuzs.beaconupgrade.BeaconUpgrade;
import fuzs.beaconupgrade.services.ClientAbstractions;
import fuzs.beaconupgrade.world.level.block.BeaconLevelEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.List;

public class MobEffectTooltipHelper {
    public static final String KEY_CURRENT_ENCHANTING_POWER = Util.makeDescriptionId("gui",
            BeaconUpgrade.id("mob_effect.tooltip.current_enchanting_power"));

    public static List<Component> getWeakPowerTooltip(int currentPower, int requiredPower, Component component) {
        List<Component> tooltipLines = new ArrayList<>();
        Component currentPowerComponent = Component.literal(String.valueOf(currentPower)).withStyle(ChatFormatting.RED);
        Component requiredPowerComponent = Component.literal(String.valueOf(requiredPower));
        tooltipLines.add(Component.translatable(KEY_CURRENT_ENCHANTING_POWER,
                currentPowerComponent,
                requiredPowerComponent));
        tooltipLines.add(component);
        return tooltipLines;
    }

    public static List<Component> getMobEffectTooltip(Holder<MobEffect> holder, int levels) {
        List<Component> tooltipLines = new ArrayList<>();
        Component levelComponent = getLevelComponent(holder, levels);
        tooltipLines.add(holder.value().getDisplayName().copy().append(CommonComponents.SPACE).append(levelComponent));
        if (Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen) {
            ClientAbstractions.INSTANCE.onGatherEffectScreenTooltip(screen,
                    new MobEffectInstance(holder),
                    tooltipLines);
        }

        return tooltipLines;
    }

    private static Component getLevelComponent(Holder<MobEffect> holder, int levels) {
        int minLevel = 0;
        int maxLevel = BeaconLevelEffect.getMaxAmplifier(holder, levels);
        MutableComponent component = Component.translatable("potion.potency." + minLevel);
        if (minLevel != maxLevel) {
            component.append("-").append(Component.translatable("potion.potency." + maxLevel));
        }

        return wrapInRoundBrackets(component).withStyle(ChatFormatting.GRAY);
    }

    private static MutableComponent wrapInRoundBrackets(Component component) {
        return Component.literal("(").append(component).append(")");
    }

    public static MutableComponent getDisplayName(Holder<MobEffect> holder) {
        return Component.object(new AtlasSprite(AtlasIds.GUI, Gui.getMobEffectSprite(holder)))
                .append(CommonComponents.SPACE)
                .append(holder.value().getDisplayName());
    }

    public static MutableComponent getDisplayNameWithLevel(Holder<MobEffect> holder, int amplifier, int levels) {
        MutableComponent component = getDisplayName(holder);
        if (amplifier != 1 || BeaconLevelEffect.getMaxAmplifier(holder, levels) != 1) {
            return component.append(CommonComponents.SPACE)
                    .append(Component.translatable("potion.potency." + amplifier));
        } else {
            return component;
        }
    }
}
