package fuzs.beaconupgrade.init;

import fuzs.beaconupgrade.BeaconUpgrade;
import fuzs.beaconupgrade.world.effect.NutritionMobEffect;
import fuzs.beaconupgrade.world.inventory.UpgradedBeaconMenu;
import fuzs.beaconupgrade.world.item.enchantment.ClampedLevelBasedValue;
import fuzs.beaconupgrade.world.level.block.BeaconBaseBlock;
import fuzs.beaconupgrade.world.level.block.BeaconLevelEffect;
import fuzs.beaconupgrade.world.level.block.BeaconPaymentItem;
import fuzs.beaconupgrade.world.level.block.entity.UpgradedBeaconBlockEntity;
import fuzs.neoforgedatapackextensions.api.v1.DataMapRegistry;
import fuzs.neoforgedatapackextensions.api.v1.DataMapToken;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Collections;

public class ModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(BeaconUpgrade.MOD_ID);
    public static final Holder.Reference<BlockEntityType<UpgradedBeaconBlockEntity>> BEACON_BLOCK_ENTITY_TYPE = REGISTRIES.registerBlockEntityType(
            "beacon",
            UpgradedBeaconBlockEntity::new,
            Collections::emptySet);
    public static final Holder.Reference<MenuType<UpgradedBeaconMenu>> BEACON_MENU_TYPE = REGISTRIES.registerMenuType(
            "beacon",
            UpgradedBeaconMenu::new);
    public static final Holder.Reference<MobEffect> LONG_REACH_MOB_EFFECT = REGISTRIES.registerMobEffect("long_reach",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xDEF58F) {
            }.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE,
                    BeaconUpgrade.id("effect.long_reach"),
                    1.0,
                    AttributeModifier.Operation.ADD_VALUE));
    public static final Holder.Reference<MobEffect> NUTRITION_MOB_EFFECT = REGISTRIES.registerMobEffect("nutrition",
            () -> new NutritionMobEffect(MobEffectCategory.BENEFICIAL, 0xC75F79));

    static final TagFactory TAGS = TagFactory.make(BeaconUpgrade.MOD_ID);
    public static final TagKey<Block> UNALTERED_BEACONS_BLOCK_TAG = TAGS.registerBlockTag("unaltered_beacons");

    public static final DataMapToken<Block, BeaconBaseBlock> BEACON_BASE_BLOCKS_DATA_MAP_TYPE = DataMapRegistry.INSTANCE.register(
            BeaconUpgrade.id("beacon_base_blocks"),
            Registries.BLOCK,
            BeaconBaseBlock.CODEC,
            BeaconBaseBlock.CODEC,
            true);
    public static final DataMapToken<Item, BeaconPaymentItem> BEACON_PAYMENT_ITEMS_DATA_MAP_TYPE = DataMapRegistry.INSTANCE.register(
            BeaconUpgrade.id("beacon_payment_items"),
            Registries.ITEM,
            BeaconPaymentItem.CODEC,
            BeaconPaymentItem.CODEC,
            true);
    public static final DataMapToken<MobEffect, BeaconLevelEffect> BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE = DataMapRegistry.INSTANCE.register(
            BeaconUpgrade.id("beacon_level_effects"),
            Registries.MOB_EFFECT,
            BeaconLevelEffect.CODEC,
            BeaconLevelEffect.CODEC,
            true);

    public static void bootstrap() {
        REGISTRIES.register(Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE,
                "clamped",
                () -> ClampedLevelBasedValue.CODEC);
    }
}
