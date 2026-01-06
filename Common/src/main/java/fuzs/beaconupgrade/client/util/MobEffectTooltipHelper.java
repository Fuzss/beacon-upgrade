package fuzs.beaconupgrade.client.util;

import fuzs.beaconupgrade.services.ClientAbstractions;
import fuzs.beaconupgrade.world.level.block.BeaconLevelEffect;
import fuzs.beaconupgrade.world.level.block.entity.UpgradedBeaconBlockEntity;
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
        int maxAmplifier = BeaconLevelEffect.get(holder).getMaxAmplifier();
        List<Component> tooltipLines = new ArrayList<>();
        Component component = getLevelComponent(maxAmplifier);
        tooltipLines.add(Component.translatable("potion.withAmplifier", holder.value().getDisplayName(), component));
        if (Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen) {
            ClientAbstractions.INSTANCE.onGatherEffectScreenTooltip(screen,
                    new MobEffectInstance(holder, MobEffectInstance.INFINITE_DURATION, maxAmplifier),
                    tooltipLines);
        }

        return tooltipLines;
    }

    private static Component getLevelComponent(int maxAmplifier) {
        MutableComponent component = getEnchantmentLevel(MobEffectInstance.MIN_AMPLIFIER + 1);
        if (maxAmplifier > MobEffectInstance.MIN_AMPLIFIER) {
            component.append("-").append(getEnchantmentLevel(maxAmplifier + 1));
        }

        return wrapInRoundBrackets(component).withStyle(ChatFormatting.GRAY);
    }

    /**
     * @see net.minecraft.network.chat.ComponentUtils#wrapInSquareBrackets(Component)
     */
    public static MutableComponent wrapInRoundBrackets(Component component) {
        return Component.literal("(").append(component).append(")");
    }

    public static MutableComponent getEnchantmentLevel(int enchantmentLevel) {
        return Component.translatable("enchantment.level." + enchantmentLevel);
    }

    public static MutableComponent getSpriteDisplayName(Holder<MobEffect> holder, int amplifier) {
        MutableComponent displayName = getSpriteDisplayName(holder);
        if (amplifier > UpgradedBeaconBlockEntity.DEFAULT_AMPLIFIER
                && BeaconLevelEffect.get(holder).getMaxAmplifier() != MobEffectInstance.MIN_AMPLIFIER) {
            return Component.translatable("potion.withAmplifier", displayName, getEnchantmentLevel(amplifier + 1));
        } else {
            return displayName;
        }
    }

    private static MutableComponent getSpriteDisplayName(Holder<MobEffect> holder) {
        return Component.empty()
                .append(Component.object(new AtlasSprite(AtlasIds.GUI, Gui.getMobEffectSprite(holder)))
                        .withStyle(ChatFormatting.WHITE))
                .append(CommonComponents.SPACE)
                .append(holder.value().getDisplayName());
    }
}
