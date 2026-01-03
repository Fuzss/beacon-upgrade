package fuzs.beaconupgrade.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fuzs.beaconupgrade.init.ModRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(PatrolSpawner.class)
abstract class PatrolSpawnerMixin {

    @ModifyExpressionValue(method = "tick",
                           at = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/server/level/ServerLevel;players()Ljava/util/List;"))
    public List<ServerPlayer> tick(List<ServerPlayer> players) {
        return players.stream().filter((ServerPlayer serverPlayer) -> {
            return !serverPlayer.hasEffect(ModRegistry.BANE_OF_RAIDERS_MOB_EFFECT);
        }).toList();
    }
}
