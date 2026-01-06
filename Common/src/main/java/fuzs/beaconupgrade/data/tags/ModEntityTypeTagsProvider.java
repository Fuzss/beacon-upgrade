package fuzs.beaconupgrade.data.tags;

import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;

public class ModEntityTypeTagsProvider extends AbstractTagProvider<EntityType<?>> {

    public ModEntityTypeTagsProvider(DataProviderContext context) {
        super(Registries.ENTITY_TYPE, context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.PLAYER_BEACON_TARGETS_ENTITY_TAG);
        this.tag(ModRegistry.PET_BEACON_TARGETS_ENTITY_TAG);
        this.tag(ModRegistry.GOLEM_BEACON_TARGETS_ENTITY_TAG).add(EntityType.ALLAY);
    }
}
