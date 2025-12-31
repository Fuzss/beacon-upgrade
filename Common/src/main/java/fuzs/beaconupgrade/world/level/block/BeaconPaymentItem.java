package fuzs.beaconupgrade.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record BeaconPaymentItem(LevelBasedValue duration) {
    public static final Codec<BeaconPaymentItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    LevelBasedValue.CODEC.fieldOf("duration").forGetter(BeaconPaymentItem::duration))
            .apply(instance, BeaconPaymentItem::new));
    public static final BeaconPaymentItem DEFAULT = new BeaconPaymentItem(11, 2);

    public BeaconPaymentItem(int base, int perLevelAfterFirst) {
        this(LevelBasedValue.perLevel(base, perLevelAfterFirst));
    }
}
