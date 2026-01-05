package fuzs.beaconupgrade;

import fuzs.beaconupgrade.config.ServerConfig;
import fuzs.beaconupgrade.handler.BlockConversionHandler;
import fuzs.beaconupgrade.handler.FlightEffectHandler;
import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.beaconupgrade.network.client.ServerboundBeaconEffectsMessage;
import fuzs.beaconupgrade.world.level.block.UpgradedBeaconBlock;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.api.event.v1.AddBlockEntityTypeBlocksCallback;
import fuzs.puzzleslib.api.event.v1.RegistryEntryAddedCallback;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingFallCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.MobEffectEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.api.event.v1.server.TagsUpdatedCallback;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.BeaconBlock;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class BeaconUpgrade implements ModConstructor {
    public static final String MOD_ID = "beaconupgrade";
    public static final String MOD_NAME = "Beacon Upgrade";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).server(ServerConfig.class);

    public static final Predicate<Block> BLOCK_PREDICATE = (Block block) -> {
        return block instanceof BeaconBlock && !(block instanceof UpgradedBeaconBlock);
    };

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        RegistryEntryAddedCallback.registryEntryAdded(Registries.BLOCK)
                .register(BlockConversionHandler.onRegistryEntryAdded(BLOCK_PREDICATE,
                        UpgradedBeaconBlock::new,
                        MOD_ID));
        AddBlockEntityTypeBlocksCallback.EVENT.register(BlockConversionHandler.onAddBlockEntityTypeBlocks(ModRegistry.BEACON_BLOCK_ENTITY_TYPE));
        PlayerInteractEvents.USE_BLOCK.register(BlockConversionHandler.onUseBlock(ModRegistry.UNALTERED_BEACONS_BLOCK_TAG,
                SoundEvents.BEACON_ACTIVATE,
                () -> CONFIG.get(ServerConfig.class).convertVanillaBeaconWhenInteracting));
        TagsUpdatedCallback.EVENT.register(EventPhase.FIRST,
                BlockConversionHandler.onTagsUpdated(ModRegistry.UNALTERED_BEACONS_BLOCK_TAG, BLOCK_PREDICATE));
        LivingFallCallback.EVENT.register(FlightEffectHandler::onLivingFall);
        MobEffectEvents.EXPIRE.register(EventPhase.BEFORE, FlightEffectHandler::onMobEffectExpire);
        if (ModLoaderEnvironment.INSTANCE.getModLoader().isFabricLike()) {
            MobEffectEvents.REMOVE.register(FlightEffectHandler::onMobEffectRemove);
            MobEffectEvents.EXPIRE.register(FlightEffectHandler::onMobEffectRemove);
        }
    }

    @Override
    public void onRegisterPayloadTypes(PayloadTypesContext context) {
        context.playToServer(ServerboundBeaconEffectsMessage.class, ServerboundBeaconEffectsMessage.STREAM_CODEC);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
