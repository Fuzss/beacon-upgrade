package fuzs.beaconupgrade.common.world.level.block.entity;

import com.mojang.serialization.Codec;
import fuzs.beaconupgrade.common.BeaconUpgrade;
import fuzs.beaconupgrade.common.init.ModRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;

import java.util.Locale;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public enum BeaconEffectTargets implements Predicate<LivingEntity>, StringRepresentable {
    PLAYERS(ModRegistry.PLAYER_BEACON_TARGETS_ENTITY_TAG) {
        @Override
        public boolean isEffectTarget(LivingEntity livingEntity) {
            return livingEntity instanceof Player;
        }
    },
    PETS(ModRegistry.PET_BEACON_TARGETS_ENTITY_TAG) {
        @Override
        public boolean isEffectTarget(LivingEntity livingEntity) {
            return livingEntity instanceof OwnableEntity ownableEntity && ownableEntity.getOwnerReference() != null;
        }
    },
    FRIENDS(ModRegistry.FRIEND_BEACON_TARGETS_ENTITY_TAG) {
        @Override
        public boolean isEffectTarget(LivingEntity livingEntity) {
            return livingEntity instanceof AbstractGolem || livingEntity instanceof AbstractVillager;
        }
    },
    ANIMALS(ModRegistry.ANIMAL_BEACON_TARGETS_ENTITY_TAG) {
        @Override
        public boolean isEffectTarget(LivingEntity livingEntity) {
            return livingEntity instanceof Animal;
        }
    };

    public static final Codec<BeaconEffectTargets> CODEC = StringRepresentable.fromEnum(BeaconEffectTargets::values);
    public static final IntFunction<BeaconEffectTargets> BY_ID = ByIdMap.continuous(BeaconEffectTargets::ordinal,
            values(),
            ByIdMap.OutOfBoundsStrategy.WRAP);

    private final TagKey<EntityType<?>> tagKey;
    public final Component component;

    BeaconEffectTargets(TagKey<EntityType<?>> tagKey) {
        this.tagKey = tagKey;
        this.component = Component.translatable(Util.makeDescriptionId("gui",
                BeaconUpgrade.id("beacon.tooltip." + this.getSerializedName())));
    }

    @Override
    public boolean test(LivingEntity livingEntity) {
        if (livingEntity.is(this.tagKey)) {
            return true;
        } else {
            return this.isEffectTarget(livingEntity) && !this.isNeverEffectTarget(livingEntity);
        }
    }

    abstract boolean isEffectTarget(LivingEntity livingEntity);

    private boolean isNeverEffectTarget(LivingEntity livingEntity) {
        if (livingEntity instanceof Enemy) {
            return true;
        } else if (livingEntity instanceof NeutralMob neutralMob) {
            return neutralMob.getTarget() instanceof Player || neutralMob.getLastHurtByMob() instanceof Player;
        } else {
            return false;
        }
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
