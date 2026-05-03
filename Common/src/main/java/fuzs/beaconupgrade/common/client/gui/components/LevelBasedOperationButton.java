package fuzs.beaconupgrade.common.client.gui.components;

import fuzs.beaconupgrade.common.BeaconUpgrade;
import fuzs.beaconupgrade.common.client.gui.screens.inventory.LevelBasedEntry;
import fuzs.beaconupgrade.common.client.gui.screens.inventory.UpgradedBeaconScreen;
import fuzs.puzzleslib.common.api.client.gui.v2.tooltip.TooltipBuilder;
import fuzs.puzzleslib.common.api.util.v1.CommonHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public abstract class LevelBasedOperationButton extends ImageButton {
    public static final Component AMPLIFY_EFFECT_COMPONENT = Component.translatable(Util.makeDescriptionId("gui",
            BeaconUpgrade.id("beacon.tooltip.amplify_effect"))).withStyle(ChatFormatting.GRAY);
    public static final Component MODIFY_EFFECT_COMPONENT = Component.translatable(Util.makeDescriptionId("gui",
            BeaconUpgrade.id("beacon.tooltip.modify_effect"))).withStyle(ChatFormatting.GRAY);

    private final boolean isPowerTooLow;

    public LevelBasedOperationButton(LevelBasedEntry<?> levelBasedEntry, int x, int y, WidgetSprites widgetSprites, OnPress onPress, Component component) {
        super(x,
                y,
                UpgradedBeaconScreen.SQUARE_BUTTON_SIZE,
                UpgradedBeaconScreen.SQUARE_BUTTON_SIZE,
                widgetSprites,
                onPress);
        this.visible = !levelBasedEntry.isNotAvailable() && this.getVisibleValue(levelBasedEntry);
        this.active = this.getActiveValue(levelBasedEntry);
        if (this.isPowerLevelSufficient(levelBasedEntry)) {
            this.isPowerTooLow = true;
            TooltipBuilder.create(levelBasedEntry.getWeakPowerTooltip(component)).splitLines().build(this);
        } else {
            this.isPowerTooLow = false;
        }
    }

    protected abstract boolean getVisibleValue(LevelBasedEntry<?> levelBasedEntry);

    protected abstract boolean getActiveValue(LevelBasedEntry<?> levelBasedEntry);

    protected abstract boolean isPowerLevelSufficient(LevelBasedEntry<?> levelBasedEntry);

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isActive() && CommonHelper.hasShiftDown()) {
            Identifier identifier = this.sprites.get(true, this.isHoveredOrFocused());
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    identifier,
                    this.getX() - 3,
                    this.getY(),
                    this.width,
                    this.height);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    identifier,
                    this.getX() + 3,
                    this.getY(),
                    this.width,
                    this.height);
        } else {
            super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
        }

        if (this.isPowerTooLow && this.isHoveredOrFocused()) {
            UpgradedBeaconScreen.setIsPowerTooLow(true);
        }
    }

    public static class Add extends LevelBasedOperationButton {

        public Add(LevelBasedEntry<?> levelBasedEntry, int x, int y, OnPress onPress) {
            super(levelBasedEntry, x, y, UpgradedBeaconScreen.ADD_BUTTON_SPRITES, onPress, AMPLIFY_EFFECT_COMPONENT);
        }

        @Override
        protected boolean getVisibleValue(LevelBasedEntry<?> levelBasedEntry) {
            return levelBasedEntry.level() < levelBasedEntry.maxLevel();
        }

        @Override
        protected boolean getActiveValue(LevelBasedEntry<?> levelBasedEntry) {
            return !levelBasedEntry.isIncompatible() && levelBasedEntry.level() < levelBasedEntry.availableLevel();
        }

        @Override
        protected boolean isPowerLevelSufficient(LevelBasedEntry<?> levelBasedEntry) {
            return levelBasedEntry.level() >= levelBasedEntry.availableLevel() && !levelBasedEntry.isNotAvailable();
        }
    }

    public static class Remove extends LevelBasedOperationButton {

        public Remove(LevelBasedEntry<?> levelBasedEntry, int x, int y, OnPress onPress) {
            super(levelBasedEntry, x, y, UpgradedBeaconScreen.REMOVE_BUTTON_SPRITES, onPress, MODIFY_EFFECT_COMPONENT);
        }

        @Override
        protected boolean getVisibleValue(LevelBasedEntry<?> levelBasedEntry) {
            return levelBasedEntry.isPresent();
        }

        @Override
        protected boolean getActiveValue(LevelBasedEntry<?> levelBasedEntry) {
            return !levelBasedEntry.isIncompatible() && levelBasedEntry.level() - 1 < levelBasedEntry.availableLevel();
        }

        @Override
        protected boolean isPowerLevelSufficient(LevelBasedEntry<?> levelBasedEntry) {
            return levelBasedEntry.level() - 1 >= levelBasedEntry.availableLevel() && !levelBasedEntry.isNotAvailable();
        }
    }
}
