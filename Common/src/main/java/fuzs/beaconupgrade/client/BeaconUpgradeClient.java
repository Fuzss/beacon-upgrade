package fuzs.beaconupgrade.client;

import fuzs.beaconupgrade.BeaconUpgrade;
import fuzs.beaconupgrade.client.handler.BlockStateTranslator;
import fuzs.beaconupgrade.handler.BlockConversionHandler;
import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BlockEntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.api.client.core.v1.context.MenuScreensContext;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import fuzs.puzzleslib.api.client.renderer.v1.model.ModelLoadingHelper;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public class BeaconUpgradeClient implements ClientModConstructor {

    @Override
    public void onRegisterBlockStateResolver(BlockStateResolverContext context) {
        BlockConversionHandler.getBlockConversions().forEach((Block oldBlock, Block newBlock) -> {
            context.registerBlockStateResolver(newBlock,
                    (ResourceManager resourceManager, Executor executor) -> {
                        return ModelLoadingHelper.loadBlockState(resourceManager, oldBlock, executor);
                    },
                    (BlockStateModelLoader.LoadedModels loadedModels, BiConsumer<BlockState, BlockStateModel.UnbakedRoot> blockStateConsumer) -> {
                        Map<BlockState, BlockState> blockStates = BlockStateTranslator.INSTANCE.convertAllBlockStates(
                                newBlock,
                                oldBlock);
                        for (BlockState blockState : newBlock.getStateDefinition().getPossibleStates()) {
                            BlockStateModel.UnbakedRoot model = loadedModels.models().get(blockStates.get(blockState));
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
        context.registerMenuScreen(ModRegistry.BEACON_MENU_TYPE.value(), BeaconScreen::new);
    }

    @Override
    public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        context.registerBlockEntityRenderer(ModRegistry.BEACON_BLOCK_ENTITY_TYPE.value(),
                (BlockEntityRendererProvider.Context rendererProviderContext) -> {
                    return new BeaconRenderer<>();
                });
    }

    @Override
    public void onRegisterBlockRenderTypes(RenderTypesContext<Block> context) {
        // this runs deferred by default, so we should have all entries from other mods available to us
        for (Map.Entry<Block, Block> entry : BlockConversionHandler.getBlockConversions().entrySet()) {
            context.registerChunkRenderType(entry.getValue(), context.getChunkRenderType(entry.getKey()));
        }
    }
}
