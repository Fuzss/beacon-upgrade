package fuzs.beaconupgrade.common.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * @see net.minecraft.world.effect.SaturationMobEffect
 */
public class NutritionMobEffect extends MobEffect {

    public NutritionMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof Player player) {
            player.getFoodData().eat(amplifier + 1, 0.0F);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int divisor = 80 >> amplifier;
        return divisor == 0 || duration % divisor == 0;
    }
}
