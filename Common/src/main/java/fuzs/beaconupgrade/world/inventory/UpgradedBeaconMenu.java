package fuzs.beaconupgrade.world.inventory;

import fuzs.beaconupgrade.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;

public class UpgradedBeaconMenu extends BeaconMenu {

    public UpgradedBeaconMenu(int containerId, Container container) {
        super(containerId, container);
    }

    public UpgradedBeaconMenu(int containerId, Container container, ContainerData beaconData, ContainerLevelAccess access) {
        super(containerId, container, beaconData, access);
    }

    @Override
    public MenuType<?> getType() {
        return ModRegistry.BEACON_MENU_TYPE.value();
    }

    @Override
    public boolean stillValid(Player player) {
        // TODO fix this properly
        return true;
    }

    @Override
    public void removed(Player player) {
        this.access.execute((Level level, BlockPos blockPos) -> {
            this.clearContainer(player, this.beacon);
        });
        super.removed(player);
    }
}
