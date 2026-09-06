package fuzs.beaconupgrade.common.services;

import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public interface ClientAbstractions {
    ClientAbstractions INSTANCE = ServiceProviderHelper.load(ClientAbstractions.class);

    void onGatherEffectScreenTooltip(EffectRenderingInventoryScreen<?> screen, MobEffectInstance mobEffect, List<Component> tooltipLines);
}
