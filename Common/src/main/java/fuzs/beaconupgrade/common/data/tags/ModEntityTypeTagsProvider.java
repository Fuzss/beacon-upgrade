package fuzs.beaconupgrade.common.data.tags;

import fuzs.beaconupgrade.common.init.ModRegistry;
import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.common.api.data.v2.tags.AbstractTagProvider;
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
        this.tag(ModRegistry.FRIEND_BEACON_TARGETS_ENTITY_TAG).add(EntityType.ALLAY);
        this.tag(ModRegistry.ANIMAL_BEACON_TARGETS_ENTITY_TAG);
    }
}
