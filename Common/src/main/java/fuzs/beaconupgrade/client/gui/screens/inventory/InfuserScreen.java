package fuzs.beaconupgrade.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.beaconupgrade.BeaconUpgrade;
import fuzs.beaconupgrade.client.gui.components.AbstractMenuSelectionList;
import fuzs.beaconupgrade.client.gui.components.ItemStackDisplayWidget;
import fuzs.beaconupgrade.client.gui.components.LevelBasedOperationButton;
import fuzs.beaconupgrade.client.util.MobEffectTooltipHelper;
import fuzs.beaconupgrade.network.client.ServerboundBeaconEffectsMessage;
import fuzs.beaconupgrade.world.inventory.UpgradedBeaconMenu;
import fuzs.beaconupgrade.world.level.block.BeaconLevelEffect;
import fuzs.beaconupgrade.world.level.block.entity.UpgradedBeaconBlockEntity;
import fuzs.puzzleslib.api.client.gui.v2.tooltip.ClientComponentSplitter;
import fuzs.puzzleslib.api.client.gui.v2.tooltip.TooltipBuilder;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.network.v4.MessageSender;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.*;

public class InfuserScreen extends AbstractContainerScreen<UpgradedBeaconMenu> implements ContainerListener {
    public static final Identifier TEXTURE_LOCATION = BeaconUpgrade.id("textures/gui/container/beacon.png");
    public static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");
    public static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(BeaconUpgrade.id("container/infuser/button"),
            BeaconUpgrade.id("container/infuser/button_disabled"),
            BeaconUpgrade.id("container/infuser/button_highlighted"));
    public static final WidgetSprites CONFIRM_BUTTON_SPRITES = new WidgetSprites(BeaconUpgrade.id(
            "container/infuser/confirm_button"),
            BeaconUpgrade.id("container/infuser/confirm_button_disabled"),
            BeaconUpgrade.id("container/infuser/confirm_button_highlighted"));
    public static final WidgetSprites REMOVE_BUTTON_SPRITES = new WidgetSprites(BeaconUpgrade.id(
            "container/infuser/remove_button"),
            BeaconUpgrade.id("container/infuser/remove_button_disabled"),
            BeaconUpgrade.id("container/infuser/remove_button_highlighted"));
    public static final WidgetSprites ADD_BUTTON_SPRITES = new WidgetSprites(BeaconUpgrade.id(
            "container/infuser/add_button"),
            BeaconUpgrade.id("container/infuser/add_button_disabled"),
            BeaconUpgrade.id("container/infuser/add_button_highlighted"));
    public static final List<Identifier> EMPTY_SLOT_ICONS = List.of(SmithingTemplateItem.EMPTY_SLOT_INGOT,
            SmithingTemplateItem.EMPTY_SLOT_DIAMOND,
            SmithingTemplateItem.EMPTY_SLOT_EMERALD);
    private static final int CONFIRM_BUTTON_OFFSET_X = 7;
    private static final int CONFIRM_BUTTON_OFFSET_Y = 55;

    private static boolean isPowerTooLow;
    private final CyclingSlotBackground slotBackground = new CyclingSlotBackground(0);
    private final int enchantmentSeed = new Random().nextInt();
    private EditBox searchBox;
    private EnchantmentSelectionList scrollingList;
    private boolean ignoreTextInput;
    private AbstractWidget powerWidget;
    private ImageButton confirmButton;

    public InfuserScreen(UpgradedBeaconMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageWidth = 220;
        this.imageHeight = 185;
        this.inventoryLabelX = 30;
        this.inventoryLabelY = this.imageHeight - 94;
        this.getMenu().addSlotListener(this);
    }

    public static void setIsPowerTooLow(boolean isPowerTooLow) {
        InfuserScreen.isPowerTooLow = isPowerTooLow;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.slotBackground.tick(EMPTY_SLOT_ICONS);
    }

    @Override
    protected void init() {
        super.init();
        this.searchBox = new EditBox(this.font,
                this.leftPos + 67,
                this.topPos + 6,
                116,
                9,
                Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setTextColor(-1);
        this.searchBox.setInvertHighlightedTextColor(false);
        this.addRenderableWidget(this.searchBox);
        this.scrollingList = new EnchantmentSelectionList(this.leftPos + 30, this.topPos + 18);
        this.addRenderableWidget(this.scrollingList);
        this.powerWidget = this.addRenderableOnly(new ItemStackDisplayWidget(this.leftPos + 196,
                this.topPos + 161,
                this.font,
                new ItemStack(Items.BEACON)) {
            private Component maximumEnchantmentPowerMessage = CommonComponents.EMPTY;
            private Component powerIsTooLowMessage = CommonComponents.EMPTY;

            @Override
            public Component getMessage() {
                if (InfuserScreen.this.getMenu().getLevels() >= UpgradedBeaconBlockEntity.MAX_BEACON_LEVELS) {
                    return this.maximumEnchantmentPowerMessage;
                } else if (isPowerTooLow) {
                    return this.powerIsTooLowMessage;
                } else {
                    return super.getMessage();
                }
            }

            @Override
            public void setMessage(Component message) {
                super.setMessage(message);
                this.maximumEnchantmentPowerMessage = ComponentUtils.mergeStyles(message,
                        Style.EMPTY.withColor(ChatFormatting.YELLOW));
                this.powerIsTooLowMessage = ComponentUtils.mergeStyles(message,
                        Style.EMPTY.withColor(ChatFormatting.RED));
            }
        });
        this.confirmButton = this.addRenderableWidget(new ImageButton(this.leftPos + CONFIRM_BUTTON_OFFSET_X,
                this.topPos + CONFIRM_BUTTON_OFFSET_Y,
                18,
                18,
                CONFIRM_BUTTON_SPRITES,
                (Button button) -> {
                    MessageSender.broadcast(new ServerboundBeaconEffectsMessage(this.getMenu().containerId,
                            this.getMenu().pack()));
                    this.searchBox.setValue("");
                }));
        this.refreshButton(InfuserMenu.ENCHANTMENT_POWER_DATA_SLOT);
        this.refreshButton(InfuserMenu.ENCHANTING_COST_DATA_SLOT);
    }

    private void refreshButton(int dataSlot) {
        switch (dataSlot) {
            case InfuserMenu.ENCHANTMENT_POWER_DATA_SLOT ->
                    this.refreshPowerLevels(this.getMenu().getEnchantmentPower());
            case InfuserMenu.ENCHANTING_COST_DATA_SLOT -> {
                this.refreshButton(this.confirmButton,
                        this.getMenu().getEnchantingCost(),
                        this.getMenu().canEnchant(this.minecraft.player));
            }
        }
    }

    private void refreshButton(InfuserMenuButton button, int value, boolean mayApply) {
        button.refreshMessage(value, mayApply);
        button.refreshTooltip(this.getMenu().getEnchantableStack(),
                this.getMenu().getItemEnchantments(),
                value,
                mayApply);
        button.active = mayApply;
    }

    private void refreshPowerLevels(int levels) {
        this.powerWidget.setMessage(Component.literal(String.valueOf(levels)));
        int enchantmentPowerLimit = this.getMenu().getEnchantmentPowerLimit();
        TooltipBuilder builder = TooltipBuilder.create()
                .splitLines(200)
                .addLines(Component.translatable(MobEffectTooltipHelper.KEY_CURRENT_ENCHANTING_POWER,
                        levels,
                        enchantmentPowerLimit).withStyle(ChatFormatting.YELLOW));
        if (levels < enchantmentPowerLimit) {
            builder.addLines(Component.translatable(InfuserMenuButton.KEY_TOOLTIP_HINT).withStyle(ChatFormatting.GRAY));
        }

        builder.build(this.powerWidget);
    }

    @Override
    public void removed() {
        super.removed();
        this.getMenu().removeSlotListener(this);
    }

    @Override
    public void resize(int width, int height) {
        String string = this.searchBox.getValue();
        super.resize(width, height);
        this.searchBox.setValue(string);
        this.refreshSearchResults();
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        if (this.ignoreTextInput) {
            return false;
        } else {
            String s = this.searchBox.getValue();
            if (this.searchBox.charTyped(characterEvent)) {
                if (!Objects.equals(s, this.searchBox.getValue())) {
                    this.refreshSearchResults();
                }
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        this.ignoreTextInput = false;
        if (!this.searchBox.isFocused()) {
            if (this.minecraft.options.keyChat.matches(keyEvent)) {
                this.ignoreTextInput = true;
                this.searchBox.setFocused(true);
                return true;
            } else {
                return super.keyPressed(keyEvent);
            }
        } else {
            boolean flag = this.hoveredSlot != null && this.hoveredSlot.hasItem();
            boolean flag1 = InputConstants.getKey(keyEvent).getNumericKeyValue().isPresent();
            if (flag && flag1 && this.checkHotbarKeyPressed(keyEvent)) {
                this.ignoreTextInput = true;
                return true;
            } else {
                String s = this.searchBox.getValue();
                if (this.searchBox.keyPressed(keyEvent)) {
                    if (!Objects.equals(s, this.searchBox.getValue())) {
                        this.refreshSearchResults();
                    }
                    return true;
                } else {
                    return this.searchBox.isFocused() && this.searchBox.isVisible() && !keyEvent.isEscape()
                            || super.keyPressed(keyEvent);
                }
            }
        }
    }

    @Override
    public boolean keyReleased(KeyEvent keyEvent) {
        this.ignoreTextInput = false;
        return super.keyReleased(keyEvent);
    }

    public void refreshSearchResults() {
        int size = this.scrollingList.children().size();
        this.scrollingList.clearEntries();
        Set<? extends Holder<MobEffect>> mobEffects = BeaconLevelEffect.getValidMobEffects();
        for (Holder<MobEffect> holder : mobEffects) {
            int amplifier = this.getMenu().getMobEffectAmplifier(holder);
            if (amplifier >= 0 && this.matchesSearch(holder)) {
                LevelBasedEntry<MobEffect> levelBasedEntry = MobEffectAmplifierEntry.create(holder,
                        amplifier,
                        this.getMenu().getLevels());
                this.scrollingList.addEntry(holder, levelBasedEntry);
            }
        }

        if (size != this.scrollingList.children().size()) {
            this.scrollingList.setScrollAmount(0.0);
        }
    }

    private boolean matchesSearch(Holder<MobEffect> holder) {
        String searchQuery = this.searchBox.getValue().toLowerCase(Locale.ROOT).trim();
        if (searchQuery.isEmpty()) {
            return true;
        } else {
            return holder.value().getDisplayName().getString().toLowerCase(Locale.ROOT).contains(searchQuery);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        isPowerTooLow = false;
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                TEXTURE_LOCATION,
                this.leftPos,
                this.topPos,
                0,
                0,
                this.imageWidth,
                this.imageHeight,
                256,
                256);
        Slot slot = this.getMenu().getSlot(0);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                SLOT_SPRITE,
                this.leftPos + slot.x - 1,
                this.topPos + slot.y - 1,
                18,
                18);
        this.slotBackground.render(this.getMenu(), guiGraphics, partialTick, this.leftPos, this.topPos);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        // AbstractContainerMenu::mouseScrolled does not call super, so this is copied from ContainerEventHandler::mouseScrolled
        if (this.getChildAt(mouseX, mouseY)
                .filter(listener -> listener.mouseScrolled(mouseX, mouseY, scrollX, scrollY))
                .isPresent()) {
            return true;
        } else {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double dragX, double dragY) {
        // AbstractContainerMenu::mouseDragged does not call super, so this is copied from ContainerEventHandler::mouseDragged
        // Fabric Api patches that in though so we only need it for NeoForge
        if (!ModLoaderEnvironment.INSTANCE.getModLoader().isFabricLike()) {
            if (this.getFocused() != null && this.isDragging()
                    && mouseButtonEvent.button() == InputConstants.MOUSE_BUTTON_LEFT && this.getFocused()
                    .mouseDragged(mouseButtonEvent, dragX, dragY)) {
                return true;
            }
        }

        return super.mouseDragged(mouseButtonEvent, dragX, dragY);
    }

    @Override
    public void slotChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, ItemStack itemStack) {
        if (dataSlotIndex == UpgradedBeaconMenu.PAYMENT_SLOT) {
            this.refreshButton(InfuserMenu.ENCHANTING_COST_DATA_SLOT);
        }
    }

    @Override
    public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
        if (dataSlotIndex < UpgradedBeaconBlockEntity.POWER_LEVELS_DATA_SLOTS) {
            this.refreshButton(dataSlotIndex);
        }
    }

    private class EnchantmentSelectionList extends AbstractMenuSelectionList<EnchantmentSelectionList.Entry> {

        public EnchantmentSelectionList(int x, int y) {
            super(InfuserScreen.this.minecraft, x, y, 160, 70, 18, 8);
        }

        public void addEntry(Holder<MobEffect> holder, LevelBasedEntry<MobEffect> levelBasedEntry) {
            this.addEntry(new Entry(holder, levelBasedEntry));
        }

        @Override
        public void clearEntries() {
            super.clearEntries();
        }

        class Entry extends AbstractMenuSelectionList.Entry<Entry> {
            private final LevelBasedEntry<MobEffect> levelBasedEntry;
            private final Component component;
            private final List<FormattedCharSequence> tooltip;

            public Entry(Holder<MobEffect> holder, LevelBasedEntry<MobEffect> levelBasedEntry) {
                this.levelBasedEntry = levelBasedEntry;
                this.component = ComponentUtils.mergeStyles(levelBasedEntry.getDisplayName(holder,
                        EnchantmentSelectionList.this.getWidth() - 18 * 2,
                        InfuserScreen.this.enchantmentSeed), Style.EMPTY.withColor(ARGB.opaque(this.getFontColor())));
                this.tooltip = ClientComponentSplitter.splitTooltipLines(levelBasedEntry.getTooltip(holder)).toList();
                this.addRenderableWidget(new LevelBasedOperationButton.Remove(levelBasedEntry,
                        EnchantmentSelectionList.this.getX(),
                        EnchantmentSelectionList.this.getY(),
                        (Button button) -> {
                            int amplifier = InfuserScreen.this.getMenu().getMobEffectAmplifier(holder);
                            if (InfuserScreen.this.getMenu().setMobEffectAmplifier(holder, amplifier + 1)
                                    != amplifier) {
                                InfuserScreen.this.refreshSearchResults();
                            }
                        }));
                this.addRenderableWidget(new LevelBasedOperationButton.Add(levelBasedEntry,
                        EnchantmentSelectionList.this.getX() + EnchantmentSelectionList.this.getWidth() - 18,
                        EnchantmentSelectionList.this.getY(),
                        (Button button) -> {
                            int amplifier = InfuserScreen.this.getMenu().getMobEffectAmplifier(holder);
                            if (InfuserScreen.this.getMenu().setMobEffectAmplifier(holder, amplifier - 1)
                                    != amplifier) {
                                InfuserScreen.this.refreshSearchResults();
                            }
                        }));
            }

            @Override
            public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
                Identifier buttonSprite = BUTTON_SPRITES.get(!this.levelBasedEntry.isInactive(),
                        this.levelBasedEntry.isPresent());
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                        buttonSprite,
                        this.getContentX(),
                        this.getContentY(),
                        this.getContentWidth(),
                        this.getContentHeight());
                guiGraphics.textRenderer(GuiGraphics.HoveredTextEffects.NONE)
                        .acceptScrollingWithDefaultCenter(this.component,
                                this.getContentX() + 18 + 2,
                                this.getContentRight() - 18 - 2,
                                this.getContentY(),
                                this.getContentBottom());
                super.renderContent(guiGraphics, mouseX, mouseY, hovering, partialTick);
                if (hovering && (this.levelBasedEntry.isInactive()
                        || mouseX >= this.getContentX() + 18 && mouseX < this.getContentRight() - 18)) {
                    guiGraphics.setTooltipForNextFrame(this.tooltip, mouseX, mouseY);
                    if (this.levelBasedEntry.isNotAvailable()) {
                        InfuserScreen.setIsPowerTooLow(true);
                    }
                }
            }

            private int getFontColor() {
                return this.levelBasedEntry.isInactive() ? 0x685E4A :
                        this.levelBasedEntry.isPresent() ? ChatFormatting.YELLOW.getColor() : -1;
            }
        }
    }
}
