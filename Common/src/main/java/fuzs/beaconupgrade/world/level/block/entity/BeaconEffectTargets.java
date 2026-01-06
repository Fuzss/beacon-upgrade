package fuzs.beaconupgrade.world.level.block.entity;

import com.mojang.serialization.Codec;
import fuzs.beaconupgrade.BeaconUpgrade;
import fuzs.beaconupgrade.init.ModRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.player.Player;

import java.util.Locale;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public enum BeaconEffectTargets implements Predicate<LivingEntity>, StringRepresentable {
    PLAYERS {
        @Override
        public boolean test(LivingEntity livingEntity) {
            return livingEntity instanceof Player || livingEntity.getType()
                    .is(ModRegistry.PLAYER_BEACON_TARGETS_ENTITY_TAG);
        }
    },
    PETS {
        @Override
        public boolean test(LivingEntity livingEntity) {
            return livingEntity instanceof OwnableEntity ownableEntity && ownableEntity.getOwnerReference() != null
                    || livingEntity.getType().is(ModRegistry.PET_BEACON_TARGETS_ENTITY_TAG);
        }
    },
    GOLEMS {
        @Override
        public boolean test(LivingEntity livingEntity) {
            return livingEntity instanceof AbstractGolem || livingEntity.getType()
                    .is(ModRegistry.GOLEM_BEACON_TARGETS_ENTITY_TAG);
        }
    };

    public static final Codec<BeaconEffectTargets> CODEC = StringRepresentable.fromEnum(BeaconEffectTargets::values);
    public static final IntFunction<BeaconEffectTargets> BY_ID = ByIdMap.continuous(BeaconEffectTargets::ordinal,
            values(),
            ByIdMap.OutOfBoundsStrategy.WRAP);

    public final Component component;

    BeaconEffectTargets() {
        this.component = Component.translatable(Util.makeDescriptionId("gui",
                BeaconUpgrade.id("beacon.tooltip." + this.getSerializedName())));
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
