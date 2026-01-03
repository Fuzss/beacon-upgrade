package fuzs.beaconupgrade.handler;

import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableDouble;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class FlightEffectHandler {

    public static EventResult onLivingFall(LivingEntity livingEntity, MutableDouble fallDistance, MutableFloat damageMultiplier) {
        if (ModRegistry.FALL_DAMAGE_IMMUNITY_ATTACHMENT_TYPE.has(livingEntity)) {
            ModRegistry.FALL_DAMAGE_IMMUNITY_ATTACHMENT_TYPE.set(livingEntity, null);
            return EventResult.INTERRUPT;
        }
        return EventResult.PASS;
    }

    public static EventResult onMobEffectExpire(LivingEntity livingEntity, MobEffectInstance mobEffect) {
        // Prevent any fall damage after the effect expires when the player was still flying.
        if (mobEffect.is(ModRegistry.FLIGHT_MOB_EFFECT) && livingEntity instanceof Player player) {
            if (!player.isCreative() && !player.isSpectator() && player.getAbilities().flying) {
                ModRegistry.FALL_DAMAGE_IMMUNITY_ATTACHMENT_TYPE.set(player, Unit.INSTANCE);
            }
        }

        return EventResult.PASS;
    }

    public static EventResult onMobEffectRemove(LivingEntity livingEntity, MobEffectInstance mobEffect) {
        if (mobEffect.is(ModRegistry.FLIGHT_MOB_EFFECT) && livingEntity instanceof Player player) {
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().mayfly = player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }

        return EventResult.PASS;
    }
}
