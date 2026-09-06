package fuzs.beaconupgrade.neoforge.data;

import fuzs.beaconupgrade.common.init.ModRegistry;
import fuzs.beaconupgrade.common.world.level.block.entity.BeaconBaseBlock;
import fuzs.beaconupgrade.common.world.level.block.entity.BeaconLevelEffect;
import fuzs.beaconupgrade.common.world.level.block.entity.BeaconPaymentItem;
import fuzs.multiloaderdataextensions.neoforge.api.v2.NeoForgeDataMapToken;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
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
        this.builder(NeoForgeDataMapToken.unwrap(ModRegistry.BEACON_BASE_BLOCKS_DATA_MAP_TYPE))
                .add(BlockTags.COPPER, new BeaconBaseBlock(0, 8), false)
                .add(Blocks.IRON_BLOCK.builtInRegistryHolder(), new BeaconBaseBlock(1, 10), false)
                .add(Blocks.GOLD_BLOCK.builtInRegistryHolder(), new BeaconBaseBlock(2, 12), false)
                .add(Blocks.EMERALD_BLOCK.builtInRegistryHolder(), new BeaconBaseBlock(3, 15), false)
                .add(Blocks.DIAMOND_BLOCK.builtInRegistryHolder(), new BeaconBaseBlock(4, 20), false)
                .add(Blocks.NETHERITE_BLOCK.builtInRegistryHolder(), new BeaconBaseBlock(5, 30), false);
        this.builder(NeoForgeDataMapToken.unwrap(ModRegistry.BEACON_PAYMENT_ITEMS_DATA_MAP_TYPE))
                .add(Items.COPPER_INGOT.builtInRegistryHolder(), new BeaconPaymentItem(3, 1), false)
                .add(Items.IRON_INGOT.builtInRegistryHolder(), new BeaconPaymentItem(11, 2), false)
                .add(Items.GOLD_INGOT.builtInRegistryHolder(), new BeaconPaymentItem(15, 3), false)
                .add(Items.EMERALD.builtInRegistryHolder(), new BeaconPaymentItem(25, 5), false)
                .add(Items.DIAMOND.builtInRegistryHolder(), new BeaconPaymentItem(45, 10), false)
                .add(Items.NETHERITE_INGOT.builtInRegistryHolder(), new BeaconPaymentItem(90, 15), false);
        this.builder(NeoForgeDataMapToken.unwrap(ModRegistry.BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE))
                .add(MobEffects.SPEED, new BeaconLevelEffect(1), false)
                .add(MobEffects.HASTE, new BeaconLevelEffect(1, 4, 1), false)
                .add(ModRegistry.REACH_MOB_EFFECT, new BeaconLevelEffect(1, 2), false)
                .add(MobEffects.RESISTANCE, new BeaconLevelEffect(2, 4, 2), false)
                .add(MobEffects.JUMP_BOOST, new BeaconLevelEffect(2), false)
                .add(MobEffects.REGENERATION, new BeaconLevelEffect(2, 2, 4), false)
                .add(MobEffects.STRENGTH, new BeaconLevelEffect(3, 4, 3), false)
                .add(MobEffects.FIRE_RESISTANCE, new BeaconLevelEffect(3), false)
                .add(ModRegistry.NUTRITION_MOB_EFFECT, new BeaconLevelEffect(3, 4, 4), false)
                .add(ModRegistry.BANE_OF_RAIDERS_MOB_EFFECT, new BeaconLevelEffect(4, 0, 4), false)
                .add(ModRegistry.BANE_OF_PHANTOMS_MOB_EFFECT, new BeaconLevelEffect(4, 0, 4), false)
                .add(ModRegistry.FLIGHT_MOB_EFFECT, new BeaconLevelEffect(5, 0, 10), false);
    }
}
