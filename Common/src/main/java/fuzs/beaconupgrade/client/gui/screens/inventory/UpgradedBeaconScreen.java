package fuzs.beaconupgrade.client.gui.screens.inventory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Pair;
import fuzs.beaconupgrade.BeaconUpgrade;
import fuzs.beaconupgrade.client.gui.components.AbstractMenuSelectionList;
import fuzs.beaconupgrade.client.gui.components.ItemStackDisplayWidget;
import fuzs.beaconupgrade.client.gui.components.LevelBasedOperationButton;
import fuzs.beaconupgrade.client.util.MobEffectTooltipHelper;
import fuzs.beaconupgrade.network.client.ServerboundBeaconEffectsMessage;
import fuzs.beaconupgrade.world.inventory.UpgradedBeaconMenu;
import fuzs.beaconupgrade.world.level.block.entity.BeaconBaseBlock;
import fuzs.beaconupgrade.world.level.block.entity.BeaconEffectTargets;
import fuzs.beaconupgrade.world.level.block.entity.BeaconLevelEffect;
import fuzs.beaconupgrade.world.level.block.entity.BeaconPaymentItem;
import fuzs.beaconupgrade.world.level.block.entity.UpgradedBeaconBlockEntity;
import fuzs.puzzleslib.api.client.gui.v2.tooltip.ClientComponentSplitter;
import fuzs.puzzleslib.api.client.gui.v2.tooltip.TooltipBuilder;
import fuzs.puzzleslib.api.network.v4.MessageSender;
import fuzs.puzzleslib.api.util.v1.CommonHelper;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class UpgradedBeaconScreen extends AbstractWidgetsContainerScreen<UpgradedBeaconMenu> implements ContainerListener {
    public static final Identifier TEXTURE_LOCATION = BeaconUpgrade.id("textures/gui/container/beacon.png");
    public static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");
    public static final WidgetSprites LARGE_BUTTON_SPRITES = new WidgetSprites(BeaconUpgrade.id(
            "container/beacon/large_button"),
            BeaconUpgrade.id("container/beacon/large_button_disabled"),
            BeaconUpgrade.id("container/beacon/large_button_highlighted"));
    public static final WidgetSprites CONFIRM_BUTTON_SPRITES = new WidgetSprites(BeaconUpgrade.id(
            "container/beacon/confirm_button"),
            BeaconUpgrade.id("container/beacon/confirm_button_disabled"),
            BeaconUpgrade.id("container/beacon/confirm_button_highlighted"));
    public static final WidgetSprites CANCEL_BUTTON_SPRITES = new WidgetSprites(BeaconUpgrade.id(
            "container/beacon/cancel_button"),
            BeaconUpgrade.id("container/beacon/cancel_button_disabled"),
            BeaconUpgrade.id("container/beacon/cancel_button_highlighted"));
    public static final WidgetSprites PLAYER_BUTTON_SPRITES = new WidgetSprites(BeaconUpgrade.id(
            "container/beacon/player_button"),
            BeaconUpgrade.id("container/beacon/player_button_disabled"),
            BeaconUpgrade.id("container/beacon/player_button_highlighted"));
    public static final WidgetSprites PET_BUTTON_SPRITES = new WidgetSprites(BeaconUpgrade.id(
            "container/beacon/pet_button"),
            BeaconUpgrade.id("container/beacon/pet_button_disabled"),
            BeaconUpgrade.id("container/beacon/pet_button_highlighted"));
    public static final WidgetSprites GOLEM_BUTTON_SPRITES = new WidgetSprites(BeaconUpgrade.id(
            "container/beacon/golem_button"),
            BeaconUpgrade.id("container/beacon/golem_button_disabled"),
            BeaconUpgrade.id("container/beacon/golem_button_highlighted"));
    public static final WidgetSprites REMOVE_BUTTON_SPRITES = new WidgetSprites(BeaconUpgrade.id(
            "container/beacon/remove_button"),
            BeaconUpgrade.id("container/beacon/remove_button_disabled"),
            BeaconUpgrade.id("container/beacon/remove_button_highlighted"));
    public static final WidgetSprites ADD_BUTTON_SPRITES = new WidgetSprites(BeaconUpgrade.id(
            "container/beacon/add_button"),
            BeaconUpgrade.id("container/beacon/add_button_disabled"),
            BeaconUpgrade.id("container/beacon/add_button_highlighted"));
    public static final List<Identifier> PYRAMID_LEVEL_SPRITES = List.of(BeaconUpgrade.id("container/beacon/level_1"),
            BeaconUpgrade.id("container/beacon/level_2"),
            BeaconUpgrade.id("container/beacon/level_3"),
            BeaconUpgrade.id("container/beacon/level_4"),
            BeaconUpgrade.id("container/beacon/level_5"));
    public static final Map<BeaconEffectTargets, WidgetSprites> EFFECT_TARGET_SPRITES = Maps.immutableEnumMap(
            ImmutableMap.of(BeaconEffectTargets.PLAYERS,
                    PLAYER_BUTTON_SPRITES,
                    BeaconEffectTargets.PETS,
                    PET_BUTTON_SPRITES,
                    BeaconEffectTargets.GOLEMS,
                    GOLEM_BUTTON_SPRITES));
    public static final String PYRAMID_LEVEL_BONUS_KEY = Util.makeDescriptionId("gui",
            BeaconUpgrade.id("beacon.tooltip.pyramid_level_bonus"));
    public static final String PYRAMID_LEVEL_BONUS_STATS_KEY = Util.makeDescriptionId("gui",
            BeaconUpgrade.id("beacon.tooltip.pyramid_level_bonus_stats"));
    public static final List<Identifier> EMPTY_SLOT_ICONS = List.of(SmithingTemplateItem.EMPTY_SLOT_INGOT,
            SmithingTemplateItem.EMPTY_SLOT_DIAMOND,
            SmithingTemplateItem.EMPTY_SLOT_EMERALD);
    private static final int BUTTON_OFFSET_X = 7;
    private static final int CONFIRM_BUTTON_OFFSET_Y = 44;
    private static final int TARGETS_BUTTON_OFFSET_Y = 66;
    private static final int UPDATE_PYRAMID_LEVELS = 1 << 0;
    private static final int UPDATE_SEARCH_RESULTS = 1 << 1;
    private static final int UPDATE_CONFIRM_BUTTON = 1 << 2;
    private static final int UPDATE_MOB_EFFECTS = 1 << 3;

    private static boolean isPowerTooLow;
    private final CyclingSlotBackground slotBackground = new CyclingSlotBackground(0);
    private final int enchantmentSeed = new Random().nextInt();
    private EditBox searchBox;
    private EnchantmentSelectionList scrollingList;
    private boolean ignoreTextInput;
    private AbstractWidget pyramidLevelsWidget;
    private ImageButton confirmButton;
    private ImageButton cancelButton;
    private Object2IntMap<Holder<MobEffect>> mobEffects = Object2IntMaps.emptyMap();
    private int updateFlags;

    public UpgradedBeaconScreen(UpgradedBeaconMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageWidth = 220;
        this.imageHeight = 185;
        this.inventoryLabelX = 30;
        this.inventoryLabelY = this.imageHeight - 94;
        this.getMenu().addSlotListener(this);
    }

    public static void setIsPowerTooLow(boolean isPowerTooLow) {
        UpgradedBeaconScreen.isPowerTooLow = isPowerTooLow;
    }

    private static Identifier getPyramidLevelSprite(int pyramidLevel) {
        return PYRAMID_LEVEL_SPRITES.get(Math.clamp(pyramidLevel, 0, PYRAMID_LEVEL_SPRITES.size() - 1));
    }

    protected boolean hasChanged() {
        return !Objects.equals(this.mobEffects, this.getMenu().packMobEffects());
    }

    protected void setMobEffectAmplifier(Holder<MobEffect> mobEffect, int amplifier) {
        UpgradedBeaconBlockEntity.setMobEffectAmplifier(this.mobEffects,
                this.getMenu().getPyramidLevels(),
                mobEffect,
                amplifier);
        this.addUpdateFlag(UPDATE_SEARCH_RESULTS, UPDATE_CONFIRM_BUTTON);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.slotBackground.tick(EMPTY_SLOT_ICONS);
        if (this.hasUpdateFlag(UPDATE_MOB_EFFECTS)) {
            this.mobEffects = this.getMenu().packMobEffects();
            this.removeUpdateFlag(UPDATE_MOB_EFFECTS);
        }

        if (this.hasUpdateFlag(UPDATE_PYRAMID_LEVELS)) {
            this.refreshPyramidLevels();
        }

        if (this.hasUpdateFlag(UPDATE_SEARCH_RESULTS)) {
            this.refreshSearchResults();
        }

        if (this.hasUpdateFlag(UPDATE_CONFIRM_BUTTON)) {
            this.refreshConfirmButton();
        }
    }

    protected boolean hasUpdateFlag(int updateFlag) {
        return (this.updateFlags & updateFlag) != 0;
    }

    protected void addUpdateFlag(int... updateFlags) {
        for (int updateFlag : updateFlags) {
            this.updateFlags |= updateFlag;
        }
    }

    protected void removeUpdateFlag(int... updateFlags) {
        for (int updateFlag : updateFlags) {
            this.updateFlags &= ~updateFlag;
        }
    }

    @Override
    protected void init() {
        super.init();
        this.updateFlags = -1;
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
        this.pyramidLevelsWidget = this.addRenderableOnly(new ItemStackDisplayWidget(this.leftPos + 196,
                this.topPos + 161,
                this.font,
                new ItemStack(Items.BEACON)) {
            private Component maximumEnchantmentPowerMessage = CommonComponents.EMPTY;
            private Component powerIsTooLowMessage = CommonComponents.EMPTY;

            @Override
            public Component getMessage() {
                if (UpgradedBeaconScreen.this.getMenu().getPyramidLevels()
                        >= UpgradedBeaconBlockEntity.MAX_PYRAMID_LEVELS) {
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
        this.confirmButton = this.addRenderableWidget(new ImageButton(this.leftPos + BUTTON_OFFSET_X,
                this.topPos + CONFIRM_BUTTON_OFFSET_Y,
                18,
                18,
                CONFIRM_BUTTON_SPRITES,
                this::clickConfirmButton));
        this.cancelButton = this.addRenderableWidget(new ImageButton(this.leftPos + BUTTON_OFFSET_X,
                this.topPos + CONFIRM_BUTTON_OFFSET_Y,
                18,
                18,
                CANCEL_BUTTON_SPRITES,
                this::clickConfirmButton));
        this.confirmButton.visible = this.cancelButton.visible = false;
        AbstractWidget abstractWidget = this.addRenderableWidget(new ImageButton(this.leftPos + BUTTON_OFFSET_X,
                this.topPos + TARGETS_BUTTON_OFFSET_Y,
                18,
                18,
                PLAYER_BUTTON_SPRITES,
                (Button button) -> {
                    if (this.getMenu()
                            .clickMenuButton(this.minecraft.player, this.getMenu().getEffectTargets().ordinal() + 1)) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.getMenu().containerId,
                                this.getMenu().getEffectTargets().ordinal());
                    }
                }) {
            @Override
            public void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                BeaconEffectTargets effectTargets = UpgradedBeaconScreen.this.getMenu().getEffectTargets();
                this.sprites = EFFECT_TARGET_SPRITES.getOrDefault(effectTargets, PLAYER_BUTTON_SPRITES);
                super.renderContents(guiGraphics, mouseX, mouseY, partialTick);
            }
        });
        TooltipBuilder.create()
                .setLines(() -> Collections.singletonList(this.getMenu().getEffectTargets().component))
                .build(abstractWidget);
    }

    private void clickConfirmButton(Button button) {
        MessageSender.broadcast(new ServerboundBeaconEffectsMessage(this.getMenu().containerId, this.mobEffects));
        this.searchBox.setValue("");
    }

    public void refreshConfirmButton() {
        this.removeUpdateFlag(UPDATE_CONFIRM_BUTTON);
        ItemStack itemStack = this.getMenu().getSlot(UpgradedBeaconMenu.PAYMENT_SLOT).getItem();
        if (this.mobEffects.isEmpty() && this.hasChanged()) {
            this.cancelButton.visible = true;
            this.confirmButton.visible = false;
        } else {
            this.cancelButton.visible = false;
            this.confirmButton.visible = true;
            this.confirmButton.active = !itemStack.isEmpty() && this.hasChanged();
        }

        TooltipBuilder tooltipBuilder = TooltipBuilder.create(this.getTooltipLines(itemStack)).splitLines(200);
        tooltipBuilder.build(this.confirmButton);
        tooltipBuilder.build(this.cancelButton);
    }

    private List<Component> getTooltipLines(ItemStack itemStack) {
        List<Component> tooltipLines = new ArrayList<>();
        tooltipLines.add(Items.BEACON.getDefaultInstance().getStyledHoverName());
        BeaconPaymentItem beaconPaymentItem = BeaconPaymentItem.get(itemStack.getItemHolder());
        int duration =
                beaconPaymentItem != null ? beaconPaymentItem.getMobEffectDuration(this.getMenu().getPyramidLevels()) :
                        0;
        List<MobEffectInstance> mobEffects = this.mobEffects.object2IntEntrySet()
                .stream()
                .map((Object2IntMap.Entry<Holder<MobEffect>> entry) -> {
                    return new MobEffectInstance(entry.getKey(), duration, entry.getIntValue());
                })
                .sorted()
                .toList();
        PotionContents.addPotionTooltip(mobEffects,
                tooltipLines::add,
                1.0F,
                this.minecraft.level.tickRateManager().tickrate());
        return tooltipLines;
    }

    private void refreshPyramidLevels() {
        this.removeUpdateFlag(UPDATE_PYRAMID_LEVELS);
        int pyramidLevels = this.getMenu().getPyramidLevels();
        if (pyramidLevels <= 0) {
            this.pyramidLevelsWidget.setMessage(CommonComponents.EMPTY);
            this.pyramidLevelsWidget.setTooltip(null);
        } else {
            Component pyramidLevelComponent = MobEffectTooltipHelper.getEnchantmentLevel(pyramidLevels);
            this.pyramidLevelsWidget.setMessage(pyramidLevelComponent);
            TooltipBuilder tooltipBuilder = TooltipBuilder.create()
                    .splitLines(200)
                    .addLines(Component.translatable(MobEffectAmplifierEntry.PYRAMID_LEVELS_KEY,
                            pyramidLevelComponent));
            for (int pyramidLevel = UpgradedBeaconBlockEntity.MIN_PYRAMID_LEVELS;
                 pyramidLevel <= pyramidLevels; pyramidLevel++) {
                Component pyramidLevelBonusComponent = this.getPyramidLevelBonus(pyramidLevel);
                if (pyramidLevelBonusComponent != null) {
                    tooltipBuilder.addLines(pyramidLevelBonusComponent);
                } else {
                    break;
                }
            }

            tooltipBuilder.build(this.pyramidLevelsWidget);
        }
    }

    private @Nullable Component getPyramidLevelBonus(int pyramidLevel) {
        Pair<Block, BeaconBaseBlock> pyramidLevelPower = this.getMenu().getPyramidLevelBonus(pyramidLevel);
        if (pyramidLevelPower != null) {
            Block block = pyramidLevelPower.getFirst();
            Identifier identifier = getPyramidLevelSprite(pyramidLevel);
            Component guiAtlasComponent = MobEffectTooltipHelper.getGuiAtlasComponent(identifier);
            int pyramidLevelBonus = pyramidLevelPower.getSecond().getPyramidLevelBonus(pyramidLevel);
            int maxPyramidLevelBonus = pyramidLevelPower.getSecond().getMaxPyramidLevelBonus();
            Component component = Component.translatable(PYRAMID_LEVEL_BONUS_STATS_KEY,
                    pyramidLevelBonus,
                    maxPyramidLevelBonus).withColor(block.defaultMapColor().col);
            return Component.translatable(PYRAMID_LEVEL_BONUS_KEY, guiAtlasComponent, block.getName(), component)
                    .withStyle(ChatFormatting.GRAY);
        } else {
            return null;
        }
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
            boolean isHoveringFilledSlot = this.hoveredSlot != null && this.hoveredSlot.hasItem();
            boolean isNumericKey = InputConstants.getKey(keyEvent).getNumericKeyValue().isPresent();
            if (isHoveringFilledSlot && isNumericKey && this.checkHotbarKeyPressed(keyEvent)) {
                this.ignoreTextInput = true;
                return true;
            } else {
                String searchQuery = this.searchBox.getValue();
                if (this.searchBox.keyPressed(keyEvent)) {
                    if (!Objects.equals(searchQuery, this.searchBox.getValue())) {
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
        this.removeUpdateFlag(UPDATE_SEARCH_RESULTS);
        int size = this.scrollingList.children().size();
        this.scrollingList.clearEntries();
        for (Holder<MobEffect> holder : BeaconLevelEffect.getSortedValidMobEffects()) {
            if (this.matchesSearch(holder)) {
                LevelBasedEntry<MobEffect> levelBasedEntry = MobEffectAmplifierEntry.create(holder,
                        this.mobEffects.getInt(holder),
                        this.getMenu().getPyramidLevels());
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
                0.0F,
                0.0F,
                this.imageWidth,
                this.imageHeight,
                256,
                256);
        Slot slot = this.getMenu().getSlot(UpgradedBeaconMenu.PAYMENT_SLOT);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                SLOT_SPRITE,
                this.leftPos + slot.x - 1,
                this.topPos + slot.y - 1,
                18,
                18);
        this.slotBackground.render(this.getMenu(), guiGraphics, partialTick, this.leftPos, this.topPos);
    }

    @Override
    public void slotChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, ItemStack itemStack) {
        if (dataSlotIndex == UpgradedBeaconMenu.PAYMENT_SLOT) {
            this.addUpdateFlag(UPDATE_CONFIRM_BUTTON);
        }
    }

    @Override
    public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
        this.addUpdateFlag(UPDATE_SEARCH_RESULTS, UPDATE_CONFIRM_BUTTON);
        if (dataSlotIndex < UpgradedBeaconBlockEntity.CONTAINER_DATA_SLOTS
                + UpgradedBeaconBlockEntity.PYRAMID_LEVELS_DATA_SLOTS) {
            this.addUpdateFlag(UPDATE_PYRAMID_LEVELS);
        } else if (dataSlotIndex
                < UpgradedBeaconBlockEntity.CONTAINER_DATA_SLOTS + UpgradedBeaconBlockEntity.PYRAMID_LEVELS_DATA_SLOTS
                + BuiltInRegistries.MOB_EFFECT.size()) {
            this.addUpdateFlag(UPDATE_MOB_EFFECTS);
        }
    }

    private class EnchantmentSelectionList extends AbstractMenuSelectionList<EnchantmentSelectionList.Entry> {

        public EnchantmentSelectionList(int x, int y) {
            super(UpgradedBeaconScreen.this.minecraft, x, y, 160, 70, 18, 8);
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
                                UpgradedBeaconScreen.this.enchantmentSeed),
                        Style.EMPTY.withColor(ARGB.opaque(this.getFontColor())));
                this.tooltip = ClientComponentSplitter.splitTooltipLines(levelBasedEntry.getTooltip(holder)).toList();
                this.addRenderableWidget(new LevelBasedOperationButton.Remove(levelBasedEntry,
                        EnchantmentSelectionList.this.getX(),
                        EnchantmentSelectionList.this.getY(),
                        (Button button) -> {
                            int amplifier = UpgradedBeaconScreen.this.mobEffects.getInt(holder);
                            UpgradedBeaconScreen.this.setMobEffectAmplifier(holder,
                                    CommonHelper.hasShiftDown() ? UpgradedBeaconBlockEntity.DEFAULT_AMPLIFIER :
                                            amplifier - 1);
                            if (UpgradedBeaconScreen.this.mobEffects.getInt(holder) != amplifier) {
                                UpgradedBeaconScreen.this.refreshSearchResults();
                                UpgradedBeaconScreen.this.refreshConfirmButton();
                            }
                        }));
                this.addRenderableWidget(new LevelBasedOperationButton.Add(levelBasedEntry,
                        EnchantmentSelectionList.this.getX() + EnchantmentSelectionList.this.getWidth() - 18,
                        EnchantmentSelectionList.this.getY(),
                        (Button button) -> {
                            int amplifier = UpgradedBeaconScreen.this.mobEffects.getInt(holder);
                            UpgradedBeaconScreen.this.setMobEffectAmplifier(holder,
                                    CommonHelper.hasShiftDown() ? MobEffectInstance.MAX_AMPLIFIER : amplifier + 1);
                            if (UpgradedBeaconScreen.this.mobEffects.getInt(holder) != amplifier) {
                                UpgradedBeaconScreen.this.refreshSearchResults();
                                UpgradedBeaconScreen.this.refreshConfirmButton();
                            }
                        }));
            }

            @Override
            public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
                Identifier buttonSprite = LARGE_BUTTON_SPRITES.get(!this.levelBasedEntry.isInactive(),
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
                        UpgradedBeaconScreen.setIsPowerTooLow(true);
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
