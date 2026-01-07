package fuzs.beaconupgrade.world.level.block.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;

import java.util.function.Consumer;

/**
 * @see net.minecraft.world.item.component.TooltipProvider
 */
@FunctionalInterface
public interface BeaconTooltipProvider {
    void addToTooltip(int pyramidLevels, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag);

    /**
     * @see net.minecraft.world.item.component.ItemAttributeModifiers.Display.Default#apply(Consumer, Player, Holder,
     *         AttributeModifier)
     */
    static Component getAttributeModifierComponent(int value, Component component) {
        return Component.translatable("attribute.modifier.plus." + AttributeModifier.Operation.ADD_VALUE.id(),
                value,
                component).withStyle(ChatFormatting.BLUE);
    }
}
