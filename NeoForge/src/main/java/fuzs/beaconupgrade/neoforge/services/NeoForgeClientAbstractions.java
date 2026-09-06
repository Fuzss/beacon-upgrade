package fuzs.beaconupgrade.neoforge.services;

import fuzs.beaconupgrade.common.services.ClientAbstractions;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.event.GatherEffectScreenTooltipsEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;

public final class NeoForgeClientAbstractions implements ClientAbstractions {
    @Override
    public void onGatherEffectScreenTooltip(EffectRenderingInventoryScreen<?> screen, MobEffectInstance mobEffect, List<Component> tooltipLines) {
        NeoForge.EVENT_BUS.post(new GatherEffectScreenTooltipsEvent(screen, mobEffect, tooltipLines));
    }
}
