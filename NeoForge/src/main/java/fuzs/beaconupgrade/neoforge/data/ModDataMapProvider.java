package fuzs.beaconupgrade.neoforge.data;

import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.beaconupgrade.world.level.block.BeaconBaseBlock;
import fuzs.beaconupgrade.world.level.block.BeaconLevelEffect;
import fuzs.beaconupgrade.world.level.block.BeaconPaymentItem;
import fuzs.neoforgedatapackextensions.neoforge.api.v1.NeoForgeDataMapToken;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {

    public ModDataMapProvider(DataProviderContext context) {
        this(context.getPackOutput(), context.getRegistries());
    }

    public ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider registries) {
        this.addBeaconBaseBlocks();
        this.addBeaconPaymentItems();
        this.addBeaconLevelEffects();
    }

    private void addBeaconBaseBlocks() {
        Builder<BeaconBaseBlock, Block> builder = this.builder(NeoForgeDataMapToken.unwrap(ModRegistry.BEACON_BASE_BLOCKS_DATA_MAP_TYPE));
        builder.add(BlockTags.COPPER, new BeaconBaseBlock(0), false);
        builder.add(Blocks.IRON_BLOCK.builtInRegistryHolder(), new BeaconBaseBlock(1), false);
        builder.add(Blocks.GOLD_BLOCK.builtInRegistryHolder(), new BeaconBaseBlock(2), false);
        builder.add(Blocks.EMERALD_BLOCK.builtInRegistryHolder(), new BeaconBaseBlock(3), false);
        builder.add(Blocks.DIAMOND_BLOCK.builtInRegistryHolder(), new BeaconBaseBlock(4), false);
        builder.add(Blocks.NETHERITE_BLOCK.builtInRegistryHolder(), new BeaconBaseBlock(5), false);
    }

    private void addBeaconPaymentItems() {
        Builder<BeaconPaymentItem, Item> builder = this.builder(NeoForgeDataMapToken.unwrap(ModRegistry.BEACON_PAYMENT_ITEMS_DATA_MAP_TYPE));
        builder.add(Items.COPPER_INGOT.builtInRegistryHolder(), new BeaconPaymentItem(11, 2), false);
        builder.add(Items.IRON_INGOT.builtInRegistryHolder(), new BeaconPaymentItem(15, 3), false);
        builder.add(Items.GOLD_INGOT.builtInRegistryHolder(), new BeaconPaymentItem(20, 5), false);
        builder.add(Items.EMERALD.builtInRegistryHolder(), new BeaconPaymentItem(30, 8), false);
        builder.add(Items.DIAMOND.builtInRegistryHolder(), new BeaconPaymentItem(40, 10), false);
        builder.add(Items.NETHERITE_INGOT.builtInRegistryHolder(), new BeaconPaymentItem(60, 15), false);
    }

    private void addBeaconLevelEffects() {
        Builder<BeaconLevelEffect, MobEffect> builder = this.builder(NeoForgeDataMapToken.unwrap(ModRegistry.BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE));
        builder.add(MobEffects.SPEED, new BeaconLevelEffect(1), false);
        builder.add(MobEffects.HASTE, new BeaconLevelEffect(1), false);
        builder.add(ModRegistry.LONG_REACH_MOB_EFFECT, new BeaconLevelEffect(1), false);
        builder.add(MobEffects.RESISTANCE, new BeaconLevelEffect(2), false);
        builder.add(MobEffects.JUMP_BOOST, new BeaconLevelEffect(2), false);
        builder.add(MobEffects.REGENERATION, new BeaconLevelEffect(2), false);
        builder.add(MobEffects.STRENGTH, new BeaconLevelEffect(3), false);
        builder.add(MobEffects.FIRE_RESISTANCE, new BeaconLevelEffect(3), false);
        builder.add(ModRegistry.NUTRITION_MOB_EFFECT, new BeaconLevelEffect(3), false);
    }
}
