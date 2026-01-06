package fuzs.beaconupgrade.world.inventory;

import com.mojang.datafixers.util.Pair;
import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.beaconupgrade.world.level.block.BeaconBaseBlock;
import fuzs.beaconupgrade.world.level.block.BeaconLevelEffect;
import fuzs.beaconupgrade.world.level.block.BeaconPaymentItem;
import fuzs.beaconupgrade.world.level.block.entity.UpgradedBeaconBlockEntity;
import fuzs.puzzleslib.api.container.v1.QuickMoveRuleSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class UpgradedBeaconMenu extends AbstractContainerMenu {
    public static final int PAYMENT_SLOT = 0;

    private final Container container = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            UpgradedBeaconMenu.this.slotsChanged(this);
        }
    };
    private final ContainerData pyramidLevelsData;
    private final ContainerData mobEffectsData;
    private final ContainerLevelAccess levelAccess;

    public UpgradedBeaconMenu(int containerId, Inventory inventory) {
        this(containerId,
                inventory,
                new SimpleContainerData(UpgradedBeaconBlockEntity.LEVELS_DATA_SLOTS),
                new SimpleContainerData(BuiltInRegistries.MOB_EFFECT.size()),
                ContainerLevelAccess.NULL);
    }

    public UpgradedBeaconMenu(int containerId, Inventory inventory, ContainerData pyramidLevelsData, ContainerData mobEffectsData, ContainerLevelAccess levelAccess) {
        super(ModRegistry.BEACON_MENU_TYPE.value(), containerId);
        this.pyramidLevelsData = pyramidLevelsData;
        this.mobEffectsData = mobEffectsData;
        this.levelAccess = levelAccess;
        this.addSlot(new Slot(this.container, PAYMENT_SLOT, 8, 34) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return BeaconPaymentItem.is(itemStack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        for (int k = 0; k < 4; ++k) {
            EquipmentSlot equipmentSlot = InventoryMenu.SLOT_IDS[k];
            Identifier identifier = InventoryMenu.TEXTURE_EMPTY_SLOTS.get(equipmentSlot);
            this.addSlot(new ArmorSlot(inventory,
                    inventory.player,
                    equipmentSlot,
                    39 - k,
                    8 + 188 * (k / 2),
                    103 + (k % 2) * 18,
                    identifier));
        }

        this.addStandardInventorySlots(inventory, 30, 103);
        this.addSlot(new Slot(inventory, Inventory.SLOT_OFFHAND, 8, 161) {
            @Override
            public void setByPlayer(ItemStack newItemStack, ItemStack oldItemStack) {
                inventory.player.onEquipItem(EquipmentSlot.OFFHAND, oldItemStack, newItemStack);
                super.setByPlayer(newItemStack, oldItemStack);
            }

            @Override
            public Identifier getNoItemIcon() {
                return InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD;
            }
        });
        this.addDataSlots(pyramidLevelsData);
        this.addDataSlots(mobEffectsData);
    }

    @Override
    public MenuType<?> getType() {
        return ModRegistry.BEACON_MENU_TYPE.value();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return QuickMoveRuleSet.of(this, this::moveItemStackTo)
                .addContainerSlotRule(0)
                .addInventoryRules()
                .addInventoryCompartmentRules()
                .quickMoveStack(player, index);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.levelAccess, player, ModRegistry.BEACON_BLOCK_ENTITY_TYPE.value());
    }

    /**
     * @see AbstractContainerMenu#stillValid(ContainerLevelAccess, Player, Block)
     */
    protected static boolean stillValid(ContainerLevelAccess levelAccess, Player player, BlockEntityType<?> blockEntityType) {
        return levelAccess.evaluate((Level level, BlockPos blockPos) ->
                blockEntityType.isValid(level.getBlockState(blockPos)) && player.isWithinBlockInteractionRange(blockPos,
                        4.0F), true);
    }

    @Override
    public void setItem(int slotId, int stateId, ItemStack itemStack) {
        super.setItem(slotId, stateId, itemStack);
        this.broadcastChanges();
    }

    @Override
    public void setData(int slotId, int data) {
        super.setData(slotId, data);
        this.broadcastChanges();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.levelAccess.execute((Level level, BlockPos blockPos) -> {
            this.clearContainer(player, this.container);
        });
    }

    public void updateEffects(Object2IntMap<Holder<MobEffect>> mobEffects) {
        this.levelAccess.execute((Level level, BlockPos blockPos) -> {
            this.updateEffects(level, blockPos, mobEffects);
        });
    }

    /**
     * @see BeaconMenu#updateEffects(Optional, Optional)
     */
    private void updateEffects(Level level, BlockPos blockPos, Object2IntMap<Holder<MobEffect>> mobEffects) {
        if (!this.container.getItem(PAYMENT_SLOT).isEmpty() || mobEffects.isEmpty()) {
            if (!mobEffects.isEmpty()) {
                this.container.removeItem(PAYMENT_SLOT, 1);
            }

            for (Holder<MobEffect> mobEffect : BeaconLevelEffect.getValidMobEffects()) {
                this.setMobEffectAmplifier(mobEffect,
                        mobEffects.getOrDefault(mobEffect, UpgradedBeaconBlockEntity.DEFAULT_AMPLIFIER));
            }

            level.blockEntityChanged(blockPos);
            level.getBlockEntity(blockPos, ModRegistry.BEACON_BLOCK_ENTITY_TYPE.value())
                    .ifPresent((UpgradedBeaconBlockEntity blockEntity) -> {
                        if (!blockEntity.getBeamSections().isEmpty()) {
                            BeaconBlockEntity.playSound(level, blockPos, SoundEvents.BEACON_POWER_SELECT);
                        }
                    });
        }
    }

    public Object2IntMap<Holder<MobEffect>> packMobEffects() {
        Object2IntMap<Holder<MobEffect>> mobEffects = new Object2IntOpenHashMap<>();
        mobEffects.defaultReturnValue(UpgradedBeaconBlockEntity.DEFAULT_AMPLIFIER);
        for (Holder<MobEffect> mobEffect : BeaconLevelEffect.getValidMobEffects()) {
            int amplifier = this.getMobEffectAmplifier(mobEffect);
            if (amplifier >= MobEffectInstance.MIN_AMPLIFIER) {
                mobEffects.put(mobEffect, amplifier);
            }
        }

        return mobEffects;
    }

    private int getMobEffectAmplifier(Holder<MobEffect> mobEffect) {
        return this.mobEffectsData.get(BuiltInRegistries.MOB_EFFECT.getIdOrThrow(mobEffect.value()));
    }

    private void setMobEffectAmplifier(Holder<MobEffect> mobEffect, int amplifier) {
        this.mobEffectsData.set(BuiltInRegistries.MOB_EFFECT.getIdOrThrow(mobEffect.value()), amplifier);
    }

    public int getPyramidLevels() {
        return this.pyramidLevelsData.get(UpgradedBeaconBlockEntity.PYRAMID_LEVELS_DATA_SLOT);
    }

    public int getAllPowerLevels() {
        return this.pyramidLevelsData.get(UpgradedBeaconBlockEntity.ALL_POWER_LEVELS_DATA_SLOT);
    }

    public @Nullable Pair<Block, BeaconBaseBlock> getPyramidLevelPower(int pyramidLevel) {
        int blockId = this.pyramidLevelsData.get(UpgradedBeaconBlockEntity.EXTRA_LEVELS_DATA_SLOTS + pyramidLevel - 1);
        Block block = BuiltInRegistries.BLOCK.byId(blockId);
        BeaconBaseBlock beaconBaseBlock = BeaconBaseBlock.get(block.defaultBlockState());
        return beaconBaseBlock != null ? Pair.of(block, beaconBaseBlock) : null;
    }
}
