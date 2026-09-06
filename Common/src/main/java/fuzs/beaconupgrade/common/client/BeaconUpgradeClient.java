package fuzs.beaconupgrade.common.client;

import fuzs.beaconupgrade.common.BeaconUpgrade;
import fuzs.beaconupgrade.common.client.gui.screens.inventory.UpgradedBeaconScreen;
import fuzs.beaconupgrade.common.handler.BlockConversionHandler;
import fuzs.beaconupgrade.common.init.ModRegistry;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BlockEntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.api.client.core.v1.context.MenuScreensContext;
import fuzs.puzzleslib.api.client.renderer.v1.model.ModelLoadingHelper;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public class BeaconUpgradeClient implements ClientModConstructor {

    @Override
    public void onRegisterBlockStateResolver(BlockStateResolverContext context) {
        BlockConversionHandler.getBlockConversions().forEach((Block originalBlock, Block substituteBlock) -> {
            context.registerBlockStateResolver(substituteBlock,
                    (ResourceManager resourceManager, Executor executor) -> {
                        return ModelLoadingHelper.loadBlockState(resourceManager, originalBlock, executor);
                    },
                    (Map<BlockState, UnbakedModel> loadedModels, BiConsumer<BlockState, UnbakedModel> blockStateConsumer) -> {
                        for (BlockState blockState : substituteBlock.getStateDefinition().getPossibleStates()) {
                            UnbakedModel model = loadedModels.get(originalBlock.withPropertiesOf(blockState));
                            if (model != null) {
                                blockStateConsumer.accept(blockState, model);
                            } else {
                                BeaconUpgrade.LOGGER.warn("Missing model for variant: '{}'", blockState);
                                blockStateConsumer.accept(blockState, ModelLoadingHelper.missingModel());
                            }
                        }
                    });
        });
    }

    @Override
    public void onRegisterMenuScreens(MenuScreensContext context) {
        context.registerMenuScreen(ModRegistry.BEACON_MENU_TYPE.value(), UpgradedBeaconScreen::new);
    }

    @Override
    public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        context.registerBlockEntityRenderer(ModRegistry.BEACON_BLOCK_ENTITY_TYPE.value(), BeaconRenderer::new);
    }
}
