package fuzs.beaconupgrade.common.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.beaconupgrade.common.init.ModRegistry;
import fuzs.beaconupgrade.common.world.item.enchantment.ClampedLevelBasedValue;
import fuzs.neoforgedatapackextensions.api.v1.DataMapRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.Collection;
import java.util.Comparator;

public record BeaconLevelEffect(LevelBasedValue maxAmplifier, LevelBasedValue strengthPerAmplifier) {
    public static final Codec<BeaconLevelEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    LevelBasedValue.CODEC.fieldOf("max_amplifier").forGetter(BeaconLevelEffect::maxAmplifier),
                    LevelBasedValue.CODEC.fieldOf("strength_per_amplifier").forGetter(BeaconLevelEffect::strengthPerAmplifier))
            .apply(instance, BeaconLevelEffect::new));
    public static final BeaconLevelEffect DEFAULT = new BeaconLevelEffect(LevelBasedValue.constant(
            UpgradedBeaconBlockEntity.DEFAULT_AMPLIFIER), LevelBasedValue.perLevel(0.0F));

    public BeaconLevelEffect(int minLevels) {
        this(minLevels, minLevels);
    }

    public BeaconLevelEffect(int minLevels, int costPerAmplifier) {
        this(LevelBasedValue.perLevel(1.0F - minLevels, 1.0F), LevelBasedValue.perLevel(costPerAmplifier));
    }

    public BeaconLevelEffect(int minLevels, int maxAmplifier, int costPerAmplifier) {
        this(new ClampedLevelBasedValue(LevelBasedValue.perLevel(1.0F - minLevels, 1.0F),
                LevelBasedValue.constant(-1.0F),
                LevelBasedValue.constant(maxAmplifier)), LevelBasedValue.perLevel(costPerAmplifier));
    }

    public int getMaxAmplifier() {
        return this.getMaxAmplifier(UpgradedBeaconBlockEntity.MAX_PYRAMID_LEVELS);
    }

    public int getMaxAmplifier(int pyramidLevels) {
        return Math.clamp(Math.round(this.maxAmplifier.calculate(pyramidLevels)),
                UpgradedBeaconBlockEntity.DEFAULT_AMPLIFIER,
                MobEffectInstance.MAX_AMPLIFIER);
    }

    public int getMinPyramidLevels() {
        return this.getRequiredPyramidLevels(MobEffectInstance.MIN_AMPLIFIER);
    }

    public int getRequiredPyramidLevels(int amplifier) {
        for (int levels = UpgradedBeaconBlockEntity.MIN_PYRAMID_LEVELS;
             levels <= UpgradedBeaconBlockEntity.MAX_PYRAMID_LEVELS; levels++) {
            if (this.getMaxAmplifier(levels) >= amplifier) {
                return levels;
            }
        }

        return -1;
    }

    public int getStrengthPerAmplifier(int amplifier) {
        return Math.round(this.strengthPerAmplifier.calculate(amplifier + 1));
    }

    public static BeaconLevelEffect get(Holder<MobEffect> mobEffect) {
        BeaconLevelEffect beaconLevelEffect = DataMapRegistry.INSTANCE.getData(ModRegistry.BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE,
                mobEffect);
        return beaconLevelEffect != null ? beaconLevelEffect : DEFAULT;
    }

    public static Collection<? extends Holder<MobEffect>> getValidMobEffects() {
        return BuiltInRegistries.MOB_EFFECT.holders().filter((Holder.Reference<MobEffect> holder) -> {
            BeaconLevelEffect beaconLevelEffect = DataMapRegistry.INSTANCE.getData(ModRegistry.BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE,
                    holder);
            return beaconLevelEffect != null
                    && beaconLevelEffect.getMinPyramidLevels() >= UpgradedBeaconBlockEntity.MIN_PYRAMID_LEVELS;
        }).toList();
    }

    public static Collection<? extends Holder<MobEffect>> getSortedValidMobEffects() {
        return BuiltInRegistries.MOB_EFFECT.holders().filter((Holder.Reference<MobEffect> holder) -> {
            BeaconLevelEffect beaconLevelEffect = DataMapRegistry.INSTANCE.getData(ModRegistry.BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE,
                    holder);
            return beaconLevelEffect != null
                    && beaconLevelEffect.getMinPyramidLevels() >= UpgradedBeaconBlockEntity.MIN_PYRAMID_LEVELS;
        }).sorted(Comparator.comparingInt((Holder.Reference<MobEffect> holder) -> {
            BeaconLevelEffect beaconLevelEffect = DataMapRegistry.INSTANCE.getData(ModRegistry.BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE,
                    holder);
            return beaconLevelEffect != null ? beaconLevelEffect.getMinPyramidLevels() : 0;
        }).thenComparing((Holder.Reference<MobEffect> holder) -> {
            return holder.key().location();
        })).toList();
    }
}
