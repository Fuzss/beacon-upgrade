package fuzs.beaconupgrade.data.client;

import fuzs.beaconupgrade.client.gui.components.LevelBasedOperationButton;
import fuzs.beaconupgrade.client.gui.screens.inventory.InfuserScreen;
import fuzs.beaconupgrade.client.gui.screens.inventory.MobEffectAmplifierEntry;
import fuzs.beaconupgrade.init.ModRegistry;
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
        translationBuilder.add(MobEffectAmplifierEntry.KEY_CURRENT_ENCHANTING_POWER, "Pyramid Levels: %s");
        translationBuilder.add(InfuserScreen.KEY_TOOLTIP_HINT,
                "Place more bookshelves in a square around the infuser on up to two layers.");
        translationBuilder.add(MobEffectAmplifierEntry.UNKNOWN_ENCHANT_COMPONENT,
                "You need to build a larger pyramid with more layers to use this effect.");
        translationBuilder.add(LevelBasedOperationButton.INCREASE_LEVEL_COMPONENT,
                "Further increasing the level for this enchantment requires an infuser with more enchanting power.");
        translationBuilder.add(LevelBasedOperationButton.MODIFY_LEVEL_COMPONENT,
                "Modifying the level for this enchantment requires an infuser with more enchanting power.");
    }

    @Override
    protected boolean mustHaveTranslationKey(Holder.Reference<?> holder, String translationKey) {
        return !(holder.value() instanceof Block) && super.mustHaveTranslationKey(holder, translationKey);
    }
}
