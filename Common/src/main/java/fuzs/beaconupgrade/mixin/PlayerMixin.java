package fuzs.beaconupgrade.mixin;

import fuzs.beaconupgrade.init.ModRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "canEat", at = @At("HEAD"), cancellable = true)
    public void canEat(boolean canAlwaysEat, CallbackInfoReturnable<Boolean> callback) {
        if (this.hasEffect(ModRegistry.NUTRITION_MOB_EFFECT)) {
            callback.setReturnValue(true);
        }
    }
}
