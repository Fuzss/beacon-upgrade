package fuzs.beaconupgrade.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.neoforgedatapackextensions.api.v2.DataMapLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.jspecify.annotations.Nullable;

public record BeaconPaymentItem(LevelBasedValue duration) {
    public static final Codec<BeaconPaymentItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    LevelBasedValue.CODEC.fieldOf("duration").forGetter(BeaconPaymentItem::duration))
            .apply(instance, BeaconPaymentItem::new));
    public static final BeaconPaymentItem DEFAULT = new BeaconPaymentItem(11, 2);

    public BeaconPaymentItem(int base, int perLevelAfterFirst) {
        this(LevelBasedValue.perLevel(base, perLevelAfterFirst));
    }

    public static boolean is(ItemStack itemStack) {
        return get(itemStack) != null;
    }

    public static @Nullable BeaconPaymentItem get(ItemStack itemStack) {
        BeaconPaymentItem beaconBaseBlock = DataMapLookup.getData(ModRegistry.BEACON_PAYMENT_ITEMS_DATA_MAP_TYPE,
                itemStack.getItemHolder());
        if (beaconBaseBlock != null) {
            return beaconBaseBlock;
        } else if (itemStack.is(ItemTags.BEACON_PAYMENT_ITEMS)) {
            return BeaconPaymentItem.DEFAULT;
        } else {
            return null;
        }
    }
}
