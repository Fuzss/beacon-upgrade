package fuzs.beaconupgrade.common.data.client;

import fuzs.beaconupgrade.common.client.gui.components.LevelBasedOperationButton;
import fuzs.beaconupgrade.common.client.gui.screens.inventory.MobEffectAmplifierEntry;
import fuzs.beaconupgrade.common.client.gui.screens.inventory.UpgradedBeaconScreen;
import fuzs.beaconupgrade.common.init.ModRegistry;
import fuzs.beaconupgrade.common.world.level.block.entity.BeaconBaseBlock;
import fuzs.beaconupgrade.common.world.level.block.entity.BeaconEffectTargets;
import fuzs.beaconupgrade.common.world.level.block.entity.BeaconPaymentItem;
import fuzs.puzzleslib.common.api.client.data.v3.language.AbstractLanguageProvider;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations() {
        this.add(ModRegistry.REACH_MOB_EFFECT.value(), "Reach");
        this.add(ModRegistry.REACH_MOB_EFFECT.value(), "desc", "Increases block interaction range.");
        this.add(ModRegistry.NUTRITION_MOB_EFFECT.value(), "Nutrition");
        this.add(ModRegistry.NUTRITION_MOB_EFFECT.value(), "desc", "Restores food levels gradually.");
        this.add(ModRegistry.BANE_OF_PHANTOMS_MOB_EFFECT.value(), "Bane of Phantoms");
        this.add(ModRegistry.BANE_OF_PHANTOMS_MOB_EFFECT.value(), "desc", "Prevents phantom spawning.");
        this.add(ModRegistry.BANE_OF_RAIDERS_MOB_EFFECT.value(), "Bane of Raiders");
        this.add(ModRegistry.BANE_OF_RAIDERS_MOB_EFFECT.value(),
                "desc",
                "Prevents pillager patrol spawning.");
        this.add(ModRegistry.BANE_OF_TRADERS_MOB_EFFECT.value(), "Bane of Traders");
        this.add(ModRegistry.BANE_OF_TRADERS_MOB_EFFECT.value(),
                "desc",
                "Prevents wandering trader spawning.");
        this.add(ModRegistry.FLIGHT_MOB_EFFECT.value(), "Flight");
        this.add(ModRegistry.FLIGHT_MOB_EFFECT.value(), "desc", "Grants the ability to fly.");
        this.add(MobEffectAmplifierEntry.PYRAMID_LEVELS_KEY, "Pyramid Levels: %s");
        this.add(MobEffectAmplifierEntry.PYRAMID_LEVELS_FRACTION_KEY, "Pyramid Levels: %s / %s");
        this.add(MobEffectAmplifierEntry.UNLOCK_EFFECT_COMPONENT,
                "You need to build a larger pyramid with more layers to unlock this effect.");
        this.add(LevelBasedOperationButton.AMPLIFY_EFFECT_COMPONENT,
                "You need to build a larger pyramid with more layers to further amplify this effect.");
        this.add(LevelBasedOperationButton.MODIFY_EFFECT_COMPONENT,
                "You need to build a larger pyramid with more layers to modify this effect.");
        this.add(UpgradedBeaconScreen.PYRAMID_LEVEL_BONUS_KEY, "%s %s");
        this.add(BeaconPaymentItem.EFFECT_DURATION_COMPONENT, "Effect Duration (Seconds)");
        this.add(BeaconBaseBlock.PYRAMID_STRENGTH_COMPONENT, "Pyramid Strength");
        this.add(BeaconBaseBlock.PYRAMID_STRENGTH_POTENTIAL_KEY, "%s (%s/%s)");
        this.add(BeaconBaseBlock.PYRAMID_STRENGTH_REQUIREMENT_KEY, "Required Pyramid Strength: %s / %s");
        this.add(BeaconBaseBlock.PYRAMID_STRENGTH_DESCRIPTION_COMPONENT,
                "You need to build a pyramid from more valuable materials to amplify strength.");
        this.add(BeaconBaseBlock.EFFECTIVE_RADIUS_COMPONENT, "Effective Radius (Blocks)");
        this.add(BeaconEffectTargets.PLAYERS.component, "Players");
        this.add(BeaconEffectTargets.PETS.component, "Pets");
        this.add(BeaconEffectTargets.FRIENDS.component, "Friends");
        this.add(BeaconEffectTargets.ANIMALS.component, "Animals");
    }

    @Override
    protected boolean mustHaveTranslationKey(Holder.Reference<?> holder, String translationKey) {
        return !(holder.value() instanceof Block) && super.mustHaveTranslationKey(holder, translationKey);
    }
}
