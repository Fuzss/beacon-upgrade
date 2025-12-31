package fuzs.beaconupgrade.world.item.enchantment;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record ClampedLevelBasedValue(LevelBasedValue value,
                                     LevelBasedValue min,
                                     LevelBasedValue max) implements LevelBasedValue {
    public static final MapCodec<ClampedLevelBasedValue> CODEC = RecordCodecBuilder.mapCodec((RecordCodecBuilder.Instance<ClampedLevelBasedValue> instance) -> {
        return instance.group(LevelBasedValue.CODEC.fieldOf("value").forGetter(ClampedLevelBasedValue::value),
                        LevelBasedValue.CODEC.fieldOf("min").forGetter(ClampedLevelBasedValue::min),
                        LevelBasedValue.CODEC.fieldOf("max").forGetter(ClampedLevelBasedValue::max))
                .apply(instance, ClampedLevelBasedValue::new);
    }).validate((ClampedLevelBasedValue value) -> {
        float max = value.max.calculate(1);
        float min = value.min.calculate(1);
        return max <= min ? DataResult.error(() -> {
            return "Max must be larger than min, min: " + min + ", max: " + max;
        }) : DataResult.success(value);
    });

    @Override
    public float calculate(int level) {
        return Mth.clamp(this.value.calculate(level), this.min.calculate(level), this.max.calculate(level));
    }

    @Override
    public MapCodec<ClampedLevelBasedValue> codec() {
        return CODEC;
    }
}
