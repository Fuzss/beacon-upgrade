package fuzs.beaconupgrade.neoforge;

import fuzs.beaconupgrade.BeaconUpgrade;
import fuzs.beaconupgrade.data.tags.ModBlockTagsProvider;
import fuzs.beaconupgrade.data.tags.ModItemTagsProvider;
import fuzs.beaconupgrade.neoforge.data.ModDataMapProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.fml.common.Mod;

@Mod(BeaconUpgrade.MOD_ID)
public class BeaconUpgradeNeoForge {

    public BeaconUpgradeNeoForge() {
        ModConstructor.construct(BeaconUpgrade.MOD_ID, BeaconUpgrade::new);
        DataProviderHelper.registerDataProviders(BeaconUpgrade.MOD_ID,
                ModItemTagsProvider::new,
                ModBlockTagsProvider::new,
                ModDataMapProvider::new);
    }
}
