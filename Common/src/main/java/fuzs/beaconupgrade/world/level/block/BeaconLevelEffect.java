package fuzs.beaconupgrade.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.beaconupgrade.world.item.enchantment.ClampedLevelBasedValue;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record BeaconLevelEffect(int minLevel, LevelBasedValue amplifier) {
    /**
     * Always require a level of at least one, as the beacon structure itself will not activate with less.
     */
    public static final int MIN_BEACON_LEVEL = 1;
    public static final int MAX_BEACON_LEVEL = 8;
    public static final Codec<BeaconLevelEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.intRange(
                            MIN_BEACON_LEVEL,
                            MAX_BEACON_LEVEL).fieldOf("min_level").forGetter(BeaconLevelEffect::minLevel),
                    LevelBasedValue.CODEC.fieldOf("amplifier").forGetter(BeaconLevelEffect::amplifier))
            .apply(instance, BeaconLevelEffect::new));

    public BeaconLevelEffect(int minLevel) {
        this(minLevel, LevelBasedValue.perLevel(0.0F, 1.0F));
    }

    public BeaconLevelEffect(int minLevel, int maxAmplifier) {
        this(minLevel,
                new ClampedLevelBasedValue(LevelBasedValue.perLevel(0.0F, 1.0F),
                        LevelBasedValue.constant(0.0F),
                        LevelBasedValue.constant(maxAmplifier)));
    }
}
