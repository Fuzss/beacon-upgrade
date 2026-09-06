package fuzs.beaconupgrade.neoforge.init;

import fuzs.beaconupgrade.common.BeaconUpgrade;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.NeoForgeMod;

public class NeoForgeModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(BeaconUpgrade.MOD_ID);
    public static final Holder.Reference<MobEffect> FLIGHT_MOB_EFFECT = REGISTRIES.registerMobEffect("flight",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL,
                    0xB8F2FF).addAttributeModifier(NeoForgeMod.CREATIVE_FLIGHT,
                    BeaconUpgrade.id("effect.flight"),
                    1.0,
                    AttributeModifier.Operation.ADD_VALUE));

    public static void bootstrap() {
        // NO-OP
    }
}
