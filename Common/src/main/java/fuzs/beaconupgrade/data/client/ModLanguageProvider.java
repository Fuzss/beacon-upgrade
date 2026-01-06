package fuzs.beaconupgrade.data.client;

import fuzs.beaconupgrade.client.gui.components.LevelBasedOperationButton;
import fuzs.beaconupgrade.client.gui.screens.inventory.MobEffectAmplifierEntry;
import fuzs.beaconupgrade.client.gui.screens.inventory.UpgradedBeaconScreen;
import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.beaconupgrade.world.level.block.entity.BeaconEffectTargets;
import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.addMobEffect(ModRegistry.REACH_MOB_EFFECT, "Reach");
        translationBuilder.addMobEffect(ModRegistry.NUTRITION_MOB_EFFECT, "Nutrition");
        translationBuilder.addMobEffect(ModRegistry.BANE_OF_PHANTOMS_MOB_EFFECT, "Bane of Phantoms");
        translationBuilder.addMobEffect(ModRegistry.BANE_OF_RAIDERS_MOB_EFFECT, "Bane of Raiders");
        translationBuilder.addMobEffect(ModRegistry.BANE_OF_TRADERS_MOB_EFFECT, "Bane of Traders");
        translationBuilder.addMobEffect(ModRegistry.FLIGHT_MOB_EFFECT, "Flight");
        translationBuilder.add(MobEffectAmplifierEntry.PYRAMID_LEVELS_KEY, "Pyramid Levels: %s");
        translationBuilder.add(MobEffectAmplifierEntry.UNLOCK_EFFECT_COMPONENT,
                "You need to build a larger pyramid with more layers to unlock this effect.");
        translationBuilder.add(LevelBasedOperationButton.AMPLIFY_EFFECT_COMPONENT,
                "You need to build a larger pyramid with more layers to further amplify this effect.");
        translationBuilder.add(LevelBasedOperationButton.MODIFY_EFFECT_COMPONENT,
                "You need to build a larger pyramid with more layers to modify this effect.");
        translationBuilder.add(UpgradedBeaconScreen.PYRAMID_LEVEL_BONUS_KEY, "%s %s %s");
        translationBuilder.add(UpgradedBeaconScreen.PYRAMID_LEVEL_BONUS_STATS_KEY, "(+%s/%s)");
        translationBuilder.add(BeaconEffectTargets.PLAYERS.component, "Players");
        translationBuilder.add(BeaconEffectTargets.PETS.component, "Pets");
        translationBuilder.add(BeaconEffectTargets.GOLEMS.component, "Golems");
    }

    @Override
    protected boolean mustHaveTranslationKey(Holder.Reference<?> holder, String translationKey) {
        return !(holder.value() instanceof Block) && super.mustHaveTranslationKey(holder, translationKey);
    }
}
