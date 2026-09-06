package fuzs.beaconupgrade.common.mixin;

import fuzs.beaconupgrade.common.init.ModRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WanderingTraderSpawner.class)
abstract class WanderingTraderSpawnerMixin {

    @ModifyVariable(method = "spawn", at = @At("STORE"))
    private @Nullable Player spawn(@Nullable Player player, ServerLevel level) {
        return player != null && !player.hasEffect(ModRegistry.BANE_OF_TRADERS_MOB_EFFECT) ? player : null;
    }
}
