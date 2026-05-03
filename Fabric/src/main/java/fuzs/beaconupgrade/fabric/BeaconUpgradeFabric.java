package fuzs.beaconupgrade.fabric;

import fuzs.beaconupgrade.BeaconUpgrade;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class BeaconUpgradeFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(BeaconUpgrade.MOD_ID, BeaconUpgrade::new);
    }
}
