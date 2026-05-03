package fuzs.beaconupgrade.fabric.services;

import fuzs.beaconupgrade.common.services.ClientAbstractions;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public final class FabricClientAbstractions implements ClientAbstractions {
    @Override
    public void onGatherEffectScreenTooltip(AbstractContainerScreen<?> screen, MobEffectInstance mobEffect, List<Component> tooltipLines) {
        FabricGuiEvents.GATHER_EFFECT_SCREEN_TOOLTIP.invoker()
                .onGatherEffectScreenTooltip(screen, mobEffect, tooltipLines);
    }
}
