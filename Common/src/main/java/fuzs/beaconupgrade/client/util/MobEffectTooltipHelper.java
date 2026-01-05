package fuzs.beaconupgrade.client.util;

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
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.List;

public class MobEffectTooltipHelper {

    public static List<Component> getMobEffectTooltip(Holder<MobEffect> holder) {
        List<Component> tooltipLines = new ArrayList<>();
        Component levelComponent = getLevelComponent(holder);
        tooltipLines.add(holder.value().getDisplayName().copy().append(CommonComponents.SPACE).append(levelComponent));
        if (Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen) {
            ClientAbstractions.INSTANCE.onGatherEffectScreenTooltip(screen,
                    new MobEffectInstance(holder),
                    tooltipLines);
        }

        return tooltipLines;
    }

    private static Component getLevelComponent(Holder<MobEffect> holder) {
        int minLevel = 0;
        int maxLevel = BeaconLevelEffect.getMaxAmplifier(holder);
        MutableComponent component = Component.translatable("enchantment.level." + (minLevel + 1));
        if (minLevel != maxLevel) {
            component.append("-").append(Component.translatable("enchantment.level." + (maxLevel + 1)));
        }

        return wrapInRoundBrackets(component).withStyle(ChatFormatting.GRAY);
    }

    public static MutableComponent wrapInRoundBrackets(Component component) {
        return Component.literal("(").append(component).append(")");
    }

    public static MutableComponent getDisplayName(Holder<MobEffect> holder) {
        return Component.empty()
                .append(Component.object(new AtlasSprite(AtlasIds.GUI, Gui.getMobEffectSprite(holder)))
                        .withStyle(ChatFormatting.WHITE))
                .append(CommonComponents.SPACE)
                .append(holder.value().getDisplayName());
    }

    public static MutableComponent getDisplayNameWithLevel(Holder<MobEffect> holder, int amplifier) {
        MutableComponent component = getDisplayName(holder);
        if (amplifier != 0 || BeaconLevelEffect.getMaxAmplifier(holder) != 0) {
            return component.append(CommonComponents.SPACE)
                    .append(Component.translatable("enchantment.level." + (amplifier + 1)));
        } else {
            return component;
        }
    }
}
