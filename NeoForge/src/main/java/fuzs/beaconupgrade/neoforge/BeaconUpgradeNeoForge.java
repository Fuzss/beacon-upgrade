package fuzs.beaconupgrade.neoforge;

import fuzs.beaconupgrade.common.BeaconUpgrade;
import fuzs.beaconupgrade.common.data.tags.ModBlockTagsProvider;
import fuzs.beaconupgrade.common.data.tags.ModEntityTypeTagsProvider;
import fuzs.beaconupgrade.common.data.tags.ModItemTagsProvider;
import fuzs.beaconupgrade.common.init.ModRegistry;
import fuzs.beaconupgrade.neoforge.data.ModDataMapProvider;
import fuzs.beaconupgrade.neoforge.init.NeoForgeModRegistry;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerSpawnPhantomsEvent;

@Mod(BeaconUpgrade.MOD_ID)
public class BeaconUpgradeNeoForge {

    public BeaconUpgradeNeoForge() {
        ModConstructor.construct(BeaconUpgrade.MOD_ID, BeaconUpgrade::new);
        NeoForgeModRegistry.bootstrap();
        registerEventHandlers();
        DataProviderHelper.registerDataProviders(BeaconUpgrade.MOD_ID,
                ModBlockTagsProvider::new,
                ModItemTagsProvider::new,
                ModEntityTypeTagsProvider::new,
                ModDataMapProvider::new);
    }

    private static void registerEventHandlers() {
        NeoForge.EVENT_BUS.addListener((final PlayerSpawnPhantomsEvent event) -> {
            if (event.getEntity().hasEffect(ModRegistry.BANE_OF_PHANTOMS_MOB_EFFECT)) {
                event.setResult(PlayerSpawnPhantomsEvent.Result.DENY);
            }
        });
    }
}
