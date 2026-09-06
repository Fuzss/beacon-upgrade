package fuzs.beaconupgrade.common.data.client;

import fuzs.beaconupgrade.common.client.gui.components.LevelBasedOperationButton;
import fuzs.beaconupgrade.common.client.gui.screens.inventory.MobEffectAmplifierEntry;
import fuzs.beaconupgrade.common.client.gui.screens.inventory.UpgradedBeaconScreen;
import fuzs.beaconupgrade.common.init.ModRegistry;
import fuzs.beaconupgrade.common.world.level.block.entity.BeaconBaseBlock;
import fuzs.beaconupgrade.common.world.level.block.entity.BeaconEffectTargets;
import fuzs.beaconupgrade.common.world.level.block.entity.BeaconPaymentItem;
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
        translationBuilder.add(ModRegistry.REACH_MOB_EFFECT.value(), "Reach");
        translationBuilder.add(ModRegistry.REACH_MOB_EFFECT.value(), "desc", "Increases block interaction range.");
        translationBuilder.add(ModRegistry.NUTRITION_MOB_EFFECT.value(), "Nutrition");
        translationBuilder.add(ModRegistry.NUTRITION_MOB_EFFECT.value(), "desc", "Restores food levels gradually.");
        translationBuilder.add(ModRegistry.BANE_OF_PHANTOMS_MOB_EFFECT.value(), "Bane of Phantoms");
        translationBuilder.add(ModRegistry.BANE_OF_PHANTOMS_MOB_EFFECT.value(), "desc", "Prevents phantom spawning.");
        translationBuilder.add(ModRegistry.BANE_OF_RAIDERS_MOB_EFFECT.value(), "Bane of Raiders");
        translationBuilder.add(ModRegistry.BANE_OF_RAIDERS_MOB_EFFECT.value(),
                "desc",
                "Prevents pillager patrol spawning.");
        translationBuilder.add(ModRegistry.BANE_OF_TRADERS_MOB_EFFECT.value(), "Bane of Traders");
        translationBuilder.add(ModRegistry.BANE_OF_TRADERS_MOB_EFFECT.value(),
                "desc",
                "Prevents wandering trader spawning.");
        translationBuilder.add(ModRegistry.FLIGHT_MOB_EFFECT.value(), "Flight");
        translationBuilder.add(ModRegistry.FLIGHT_MOB_EFFECT.value(), "desc", "Grants the ability to fly.");
        translationBuilder.add(MobEffectAmplifierEntry.PYRAMID_LEVELS_KEY, "Pyramid Levels: %s");
        translationBuilder.add(MobEffectAmplifierEntry.PYRAMID_LEVELS_FRACTION_KEY, "Pyramid Levels: %s / %s");
        translationBuilder.add(MobEffectAmplifierEntry.UNLOCK_EFFECT_COMPONENT,
                "You need to build a larger pyramid with more layers to unlock this effect.");
        translationBuilder.add(LevelBasedOperationButton.AMPLIFY_EFFECT_COMPONENT,
                "You need to build a larger pyramid with more layers to further amplify this effect.");
        translationBuilder.add(LevelBasedOperationButton.MODIFY_EFFECT_COMPONENT,
                "You need to build a larger pyramid with more layers to modify this effect.");
        translationBuilder.add(UpgradedBeaconScreen.PYRAMID_LEVEL_BONUS_KEY, "%s %s");
        translationBuilder.add(BeaconPaymentItem.EFFECT_DURATION_COMPONENT, "Effect Duration (Seconds)");
        translationBuilder.add(BeaconBaseBlock.PYRAMID_STRENGTH_COMPONENT, "Pyramid Strength");
        translationBuilder.add(BeaconBaseBlock.PYRAMID_STRENGTH_POTENTIAL_KEY, "%s (%s/%s)");
        translationBuilder.add(BeaconBaseBlock.PYRAMID_STRENGTH_REQUIREMENT_KEY, "Required Pyramid Strength: %s / %s");
        translationBuilder.add(BeaconBaseBlock.PYRAMID_STRENGTH_DESCRIPTION_COMPONENT,
                "You need to build a pyramid from more valuable materials to amplify strength.");
        translationBuilder.add(BeaconBaseBlock.EFFECTIVE_RADIUS_COMPONENT, "Effective Radius (Blocks)");
        translationBuilder.add(BeaconEffectTargets.PLAYERS.component, "Players");
        translationBuilder.add(BeaconEffectTargets.PETS.component, "Pets");
        translationBuilder.add(BeaconEffectTargets.FRIENDS.component, "Friends");
        translationBuilder.add(BeaconEffectTargets.ANIMALS.component, "Animals");
    }

    @Override
    protected boolean mustHaveTranslationKey(Holder.Reference<?> holder, String translationKey) {
        return !(holder.value() instanceof Block) && super.mustHaveTranslationKey(holder, translationKey);
    }
}
