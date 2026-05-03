package fuzs.beaconupgrade.common.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.beaconupgrade.common.BeaconUpgrade;
import fuzs.beaconupgrade.common.init.ModRegistry;
import fuzs.multiloaderdataextensions.common.api.v2.DataMapLookup;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record BeaconPaymentItem(LevelBasedValue durationInSeconds) implements BeaconTooltipProvider {
    public static final Codec<BeaconPaymentItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    LevelBasedValue.CODEC.fieldOf("duration_in_seconds").forGetter(BeaconPaymentItem::durationInSeconds))
            .apply(instance, BeaconPaymentItem::new));
    public static final BeaconPaymentItem DEFAULT = new BeaconPaymentItem(11, 2);
    public static final Component EFFECT_DURATION_COMPONENT = Component.translatable(Util.makeDescriptionId("gui",
            BeaconUpgrade.id("beacon.tooltip.effect_duration")));

    public BeaconPaymentItem(int base, int perLevelAfterFirst) {
        this(LevelBasedValue.perLevel(base, perLevelAfterFirst));
    }

    public int getDurationInSeconds(int pyramidLevels) {
        return Math.round(this.durationInSeconds.calculate(pyramidLevels));
    }

    public int getDuration(int pyramidLevels) {
        return this.getDurationInSeconds(pyramidLevels) * SharedConstants.TICKS_PER_SECOND;
    }

    @Override
    public void addToTooltip(int pyramidLevels, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        int durationInSeconds = this.getDurationInSeconds(pyramidLevels);
        if (durationInSeconds > 0) {
            tooltipAdder.accept(BeaconTooltipProvider.getAttributeModifierComponent(durationInSeconds,
                    BeaconPaymentItem.EFFECT_DURATION_COMPONENT));
        }
    }

    public static @Nullable BeaconPaymentItem get(Holder<Item> holder) {
        BeaconPaymentItem beaconBaseBlock = DataMapLookup.getData(ModRegistry.BEACON_PAYMENT_ITEMS_DATA_MAP_TYPE,
                holder);
        if (beaconBaseBlock != null) {
            return beaconBaseBlock;
        } else if (holder.is(ItemTags.BEACON_PAYMENT_ITEMS)) {
            return BeaconPaymentItem.DEFAULT;
        } else {
            return null;
        }
    }
}
