package fuzs.beaconupgrade.init;

import fuzs.beaconupgrade.BeaconUpgrade;
import fuzs.beaconupgrade.world.effect.NutritionMobEffect;
import fuzs.beaconupgrade.world.inventory.UpgradedBeaconMenu;
import fuzs.beaconupgrade.world.item.enchantment.ClampedLevelBasedValue;
import fuzs.beaconupgrade.world.level.block.BeaconBaseBlock;
import fuzs.beaconupgrade.world.level.block.BeaconLevelEffect;
import fuzs.beaconupgrade.world.level.block.BeaconPaymentItem;
import fuzs.beaconupgrade.world.level.block.entity.UpgradedBeaconBlockEntity;
import fuzs.neoforgedatapackextensions.api.v1.DataMapToken;
import fuzs.neoforgedatapackextensions.api.v2.DataMapRegistrar;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentType;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import fuzs.puzzleslib.api.network.v4.PlayerSet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
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
    public static final Holder.Reference<MobEffect> REACH_MOB_EFFECT = REGISTRIES.registerMobEffect("reach",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x7FE3A1) {
            }.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE,
                    BeaconUpgrade.id("effect.reach"),
                    1.0,
                    AttributeModifier.Operation.ADD_VALUE));
    public static final Holder.Reference<MobEffect> NUTRITION_MOB_EFFECT = REGISTRIES.registerMobEffect("nutrition",
            () -> new NutritionMobEffect(MobEffectCategory.BENEFICIAL, 0xE38A6B));
    public static final Holder.Reference<MobEffect> BANE_OF_RAIDERS_MOB_EFFECT = REGISTRIES.registerMobEffect(
            "bane_of_raiders",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xB84A3A));
    public static final Holder.Reference<MobEffect> BANE_OF_PHANTOMS_MOB_EFFECT = REGISTRIES.registerMobEffect(
            "bane_of_phantoms",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x6F6AAE));
    public static final Holder.Reference<MobEffect> BANE_OF_TRADERS_MOB_EFFECT = REGISTRIES.registerMobEffect(
            "bane_of_traders",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xC2A34F));
    public static final Holder.Reference<MobEffect> FLIGHT_MOB_EFFECT = REGISTRIES.whenOnFabricLike()
            .registerMobEffect("flight", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xB8F2FF) {
                @Override
                public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
                    super.onEffectAdded(livingEntity, amplifier);
                    if (livingEntity instanceof Player player && !player.getAbilities().mayfly) {
                        player.getAbilities().mayfly = true;
                        player.onUpdateAbilities();
                    }
                }
            });

    static final TagFactory TAGS = TagFactory.make(BeaconUpgrade.MOD_ID);
    public static final TagKey<Block> UNALTERED_BEACONS_BLOCK_TAG = TAGS.registerBlockTag("unaltered_beacons");

    public static final DataMapToken<Block, BeaconBaseBlock> BEACON_BASE_BLOCKS_DATA_MAP_TYPE = DataMapRegistrar.register(
            BeaconUpgrade.id("beacon_base_blocks"),
            Registries.BLOCK,
            BeaconBaseBlock.CODEC,
            BeaconBaseBlock.CODEC,
            true);
    public static final DataMapToken<Item, BeaconPaymentItem> BEACON_PAYMENT_ITEMS_DATA_MAP_TYPE = DataMapRegistrar.register(
            BeaconUpgrade.id("beacon_payment_items"),
            Registries.ITEM,
            BeaconPaymentItem.CODEC,
            BeaconPaymentItem.CODEC,
            true);
    public static final DataMapToken<MobEffect, BeaconLevelEffect> BEACON_LEVEL_EFFECTS_DATA_MAP_TYPE = DataMapRegistrar.register(
            BeaconUpgrade.id("beacon_level_effects"),
            Registries.MOB_EFFECT,
            BeaconLevelEffect.CODEC,
            BeaconLevelEffect.CODEC,
            true);

    public static final DataAttachmentType<Entity, Unit> FALL_DAMAGE_IMMUNITY_ATTACHMENT_TYPE = DataAttachmentRegistry.<Unit>entityBuilder()
            .persistent(Unit.CODEC)
            .networkSynchronized(Unit.STREAM_CODEC, PlayerSet::ofEntity)
            .build(BeaconUpgrade.id("fall_damage_immunity"));

    public static void bootstrap() {
        REGISTRIES.register(Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE,
                "clamped",
                () -> ClampedLevelBasedValue.CODEC);
    }
}
