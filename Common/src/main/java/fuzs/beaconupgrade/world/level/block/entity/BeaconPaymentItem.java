package fuzs.beaconupgrade.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.neoforgedatapackextensions.api.v2.DataMapLookup;
import net.minecraft.core.Holder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.jspecify.annotations.Nullable;

public record BeaconPaymentItem(LevelBasedValue durationInSeconds) {
    public static final Codec<BeaconPaymentItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    LevelBasedValue.CODEC.fieldOf("duration_in_seconds").forGetter(BeaconPaymentItem::durationInSeconds))
            .apply(instance, BeaconPaymentItem::new));
    public static final BeaconPaymentItem DEFAULT = new BeaconPaymentItem(11, 2);

    public BeaconPaymentItem(int base, int perLevelAfterFirst) {
        this(LevelBasedValue.perLevel(base, perLevelAfterFirst));
    }

    public int getMobEffectDuration(int pyramidLevels) {
        return Math.round(this.durationInSeconds.calculate(pyramidLevels)) * 20;
    }

    public static boolean is(Holder<Item> holder) {
        return get(holder) != null;
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
