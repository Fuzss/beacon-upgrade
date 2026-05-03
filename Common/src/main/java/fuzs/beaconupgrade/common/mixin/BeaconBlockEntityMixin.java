package fuzs.beaconupgrade.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fuzs.beaconupgrade.common.world.level.block.entity.UpgradedBeaconBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeaconBlockEntity.class)
abstract class BeaconBlockEntityMixin extends BlockEntity {

    public BeaconBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @WrapOperation(method = "tick",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;updateBase(Lnet/minecraft/world/level/Level;III)I"))
    private static int tick(Level level, int x, int y, int z, Operation<Integer> operation, Level levelX, BlockPos pos, BlockState selfState, BeaconBlockEntity entity) {
        if (entity instanceof UpgradedBeaconBlockEntity) {
            return ((UpgradedBeaconBlockEntity) entity).updatePowerLevels(level, pos).size();
        } else {
            return operation.call(level, x, y, z);
        }
    }
}
