package fuzs.beaconupgrade.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.beaconupgrade.world.item.enchantment.ClampedLevelBasedValue;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record BeaconBaseBlock(LevelBasedValue strength) {
    public static final Codec<BeaconBaseBlock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    LevelBasedValue.CODEC.fieldOf("strength").forGetter(BeaconBaseBlock::strength))
            .apply(instance, BeaconBaseBlock::new));
    public static final BeaconBaseBlock DEFAULT = new BeaconBaseBlock(1);

    public BeaconBaseBlock(int strength) {
        this(new ClampedLevelBasedValue(LevelBasedValue.constant(strength),
                LevelBasedValue.constant(0.0F),
                LevelBasedValue.perLevel(1.0F)));
    }
}
