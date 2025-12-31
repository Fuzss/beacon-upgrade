package fuzs.beaconupgrade.neoforge.client;

import fuzs.beaconupgrade.BeaconUpgrade;
import fuzs.beaconupgrade.client.BeaconUpgradeClient;
import fuzs.beaconupgrade.data.client.ModLanguageProvider;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = BeaconUpgrade.MOD_ID, dist = Dist.CLIENT)
public class BeaconUpgradeNeoForgeClient {

    public BeaconUpgradeNeoForgeClient() {
        ClientModConstructor.construct(BeaconUpgrade.MOD_ID, BeaconUpgradeClient::new);
        DataProviderHelper.registerDataProviders(BeaconUpgrade.MOD_ID, ModLanguageProvider::new);
    }
}
