package fuzs.beaconupgrade.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fuzs.beaconupgrade.init.ModRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodData.class)
abstract class FoodDataMixin {

    @ModifyExpressionValue(method = "tick",
                           at = @At(value = "FIELD",
                                    target = "Lnet/minecraft/world/food/FoodData;saturationLevel:F",
                                    opcode = Opcodes.GETFIELD,
                                    ordinal = 0))
    public float tick(float saturationLevel, ServerPlayer player) {
        // Prevent saturation from ticking down, use hunger directly instead, so players do not loose saturation while the effect is active.
        // The hunger points themselves are restored by the effect, but it does not affect saturation at all.
        if (player.hasEffect(ModRegistry.NUTRITION_MOB_EFFECT)) {
            return -1.0F;
        } else {
            return saturationLevel;
        }
    }
}
