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

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record BeaconLevelEffect(LevelBasedValue maxAmplifier) {
    public static final Codec<BeaconLevelEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    LevelBasedValue.CODEC.fieldOf("max_amplifier").forGetter(BeaconLevelEffect::maxAmplifier))
            .apply(instance, BeaconLevelEffect::new));
    public static final BeaconLevelEffect DEFAULT = new BeaconLevelEffect(LevelBasedValue.constant(-1.0F));

    public BeaconLevelEffect(int minLevels) {
        this(LevelBasedValue.perLevel(1.0F - minLevels, 1.0F));
    }

    public BeaconLevelEffect(int minLevels, int maxAmplifier) {
        this(new ClampedLevelBasedValue(LevelBasedValue.perLevel(1.0F - minLevels, 1.0F),
                LevelBasedValue.constant(-1.0F),
                LevelBasedValue.constant(maxAmplifier)));
    }

    public int getMaxAmplifier(int levels) {
        return Math.clamp(Math.round(this.maxAmplifier.calculate(levels)), -1, MobEffectInstance.MAX_AMPLIFIER);
    }

    public int getMinLevels() {
        for (int levels = UpgradedBeaconBlockEntity.MIN_BEACON_LEVELS;
             levels <= UpgradedBeaconBlockEntity.MAX_BEACON_LEVELS; levels++) {
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

    public static int getMaxAmplifier(Holder<MobEffect> mobEffect, int levels) {
        return get(mobEffect).getMaxAmplifier(levels);
    }

    public static Set<? extends Holder<MobEffect>> getValidMobEffects() {
        return DataMapLookup.getDataMap(BuiltInRegistries.MOB_EFFECT, ModRegistry.BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE)
                .entrySet()
                .stream()
                .filter((Map.Entry<ResourceKey<MobEffect>, BeaconLevelEffect> entry) -> {
                    return entry.getValue().getMinLevels() >= UpgradedBeaconBlockEntity.MIN_BEACON_LEVELS;
                })
                .map(Map.Entry::getKey)
                .map(BuiltInRegistries.MOB_EFFECT::getOrThrow)
                .collect(Collectors.toSet());
    }
}
