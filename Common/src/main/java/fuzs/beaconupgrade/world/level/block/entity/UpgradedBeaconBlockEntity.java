package fuzs.beaconupgrade.world.level.block.entity;

import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.beaconupgrade.world.inventory.UpgradedBeaconMenu;
import fuzs.puzzleslib.api.block.v1.entity.TickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class UpgradedBeaconBlockEntity extends BeaconBlockEntity implements TickingBlockEntity {

    public UpgradedBeaconBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
        this.type = ModRegistry.BEACON_BLOCK_ENTITY_TYPE.value();
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModRegistry.BEACON_BLOCK_ENTITY_TYPE.value();
    }

    @Override
    public void clientTick() {
        tick(this.getLevel(), this.getBlockPos(), this.getBlockState(), this);
    }

    @Override
    public void serverTick() {
        tick(this.getLevel(), this.getBlockPos(), this.getBlockState(), this);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        if (this.lockKey.canUnlock(player)) {
            return new UpgradedBeaconMenu(containerId,
                    inventory,
                    this.dataAccess,
                    ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()));
        } else {
            return super.createMenu(containerId, inventory, player);
        }
    }
}
