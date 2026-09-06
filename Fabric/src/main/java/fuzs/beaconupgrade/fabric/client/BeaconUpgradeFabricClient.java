package fuzs.beaconupgrade.fabric.client;

import fuzs.beaconupgrade.common.BeaconUpgrade;
import fuzs.beaconupgrade.common.client.BeaconUpgradeClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class BeaconUpgradeFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(BeaconUpgrade.MOD_ID, BeaconUpgradeClient::new);
    }
}
