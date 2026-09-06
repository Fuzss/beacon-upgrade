package fuzs.beaconupgrade.fabric;

import fuzs.beaconupgrade.common.BeaconUpgrade;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class BeaconUpgradeFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(BeaconUpgrade.MOD_ID, BeaconUpgrade::new);
    }
}
