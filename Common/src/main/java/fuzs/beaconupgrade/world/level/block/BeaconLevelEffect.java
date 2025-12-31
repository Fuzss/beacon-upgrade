package fuzs.beaconupgrade.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record BeaconLevelEffect(int minLevel, LevelBasedValue maxAmplifier) {
    public static final Codec<BeaconLevelEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("min_level").forGetter(BeaconLevelEffect::minLevel),
                    LevelBasedValue.CODEC.fieldOf("max_amplifier").forGetter(BeaconLevelEffect::maxAmplifier))
            .apply(instance, BeaconLevelEffect::new));

    public BeaconLevelEffect(int minLevel) {
        this(minLevel, LevelBasedValue.perLevel(1.0F));
    }
}
