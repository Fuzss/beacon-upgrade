package fuzs.beaconupgrade.common.client.util;

import fuzs.beaconupgrade.common.services.ClientAbstractions;
import fuzs.beaconupgrade.common.world.level.block.entity.BeaconLevelEffect;
import fuzs.beaconupgrade.common.world.level.block.entity.UpgradedBeaconBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
                    new MobEffectInstance(holder, 0, maxAmplifier),
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

    public static Component getSpriteDisplayName(Holder<MobEffect> holder, int amplifier) {
        Component displayName = holder.value().getDisplayName();
        if (amplifier > UpgradedBeaconBlockEntity.DEFAULT_AMPLIFIER
                && BeaconLevelEffect.get(holder).getMaxAmplifier() != MobEffectInstance.MIN_AMPLIFIER) {
            return Component.translatable("potion.withAmplifier", displayName, getEnchantmentLevel(amplifier + 1));
        } else {
            return displayName;
        }
    }
}
