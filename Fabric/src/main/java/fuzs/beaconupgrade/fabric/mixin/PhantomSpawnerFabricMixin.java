package fuzs.beaconupgrade.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import fuzs.beaconupgrade.common.init.ModRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PhantomSpawner.class)
abstract class PhantomSpawnerFabricMixin {

    @ModifyExpressionValue(method = "tick",
                           at = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z"))
    public boolean tick(boolean isSpectator, @Local ServerPlayer serverPlayer) {
        return isSpectator || serverPlayer.hasEffect(ModRegistry.BANE_OF_PHANTOMS_MOB_EFFECT);
    }
}
