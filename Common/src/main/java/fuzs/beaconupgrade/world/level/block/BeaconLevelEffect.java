package fuzs.beaconupgrade.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.beaconupgrade.world.item.enchantment.ClampedLevelBasedValue;
import fuzs.beaconupgrade.world.level.block.entity.UpgradedBeaconBlockEntity;
import fuzs.neoforgedatapackextensions.api.v2.DataMapLookup;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

public record BeaconLevelEffect(LevelBasedValue maxAmplifier, LevelBasedValue costPerAmplifier) {
    public static final Codec<BeaconLevelEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    LevelBasedValue.CODEC.fieldOf("max_amplifier").forGetter(BeaconLevelEffect::maxAmplifier),
                    LevelBasedValue.CODEC.fieldOf("cost_per_amplifier").forGetter(BeaconLevelEffect::costPerAmplifier))
            .apply(instance, BeaconLevelEffect::new));
    public static final BeaconLevelEffect DEFAULT = new BeaconLevelEffect(LevelBasedValue.constant(-1.0F),
            LevelBasedValue.perLevel(0.0F));

    public BeaconLevelEffect(int minLevels) {
        this(LevelBasedValue.perLevel(1.0F - minLevels, 1.0F), LevelBasedValue.perLevel(minLevels));
    }

    public BeaconLevelEffect(int minLevels, int maxAmplifier) {
        this(new ClampedLevelBasedValue(LevelBasedValue.perLevel(1.0F - minLevels, 1.0F),
                LevelBasedValue.constant(-1.0F),
                LevelBasedValue.constant(maxAmplifier)), LevelBasedValue.perLevel(minLevels));
    }

    public int getMaxAmplifier(int pyramidLevels) {
        return Math.clamp(Math.round(this.maxAmplifier.calculate(pyramidLevels)), -1, MobEffectInstance.MAX_AMPLIFIER);
    }

    public int getMinPyramidLevels() {
        for (int levels = UpgradedBeaconBlockEntity.MIN_PYRAMID_LEVELS;
             levels <= UpgradedBeaconBlockEntity.MAX_PYRAMID_LEVELS; levels++) {
            if (this.getMaxAmplifier(levels) >= 0) {
                return levels;
            }
        }

        return 0;
    }

    public static BeaconLevelEffect get(Holder<MobEffect> mobEffect) {
        return DataMapLookup.getDataMap(BuiltInRegistries.MOB_EFFECT, ModRegistry.BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE)
                .getOrDefault(mobEffect.unwrapKey().orElseThrow(), DEFAULT);
    }

    public static int getMaxAmplifier(Holder<MobEffect> mobEffect) {
        return getMaxAmplifier(mobEffect, UpgradedBeaconBlockEntity.MAX_PYRAMID_LEVELS);
    }

    public static int getMaxAmplifier(Holder<MobEffect> mobEffect, int pyramidLevels) {
        return get(mobEffect).getMaxAmplifier(pyramidLevels);
    }

    public static Collection<? extends Holder<MobEffect>> getValidMobEffects() {
        return DataMapLookup.getDataMap(BuiltInRegistries.MOB_EFFECT, ModRegistry.BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE)
                .entrySet()
                .stream()
                .filter((Map.Entry<ResourceKey<MobEffect>, BeaconLevelEffect> entry) -> {
                    return entry.getValue().getMinPyramidLevels() >= UpgradedBeaconBlockEntity.MIN_PYRAMID_LEVELS;
                })
                .sorted(Comparator.<Map.Entry<ResourceKey<MobEffect>, BeaconLevelEffect>>comparingInt((Map.Entry<ResourceKey<MobEffect>, BeaconLevelEffect> entry) -> {
                    return entry.getValue().getMinPyramidLevels();
                }).thenComparing((Map.Entry<ResourceKey<MobEffect>, BeaconLevelEffect> entry) -> {
                    return entry.getKey().identifier();
                }))
                .map(Map.Entry::getKey)
                .map(BuiltInRegistries.MOB_EFFECT::getOrThrow)
                .toList();
    }
}
