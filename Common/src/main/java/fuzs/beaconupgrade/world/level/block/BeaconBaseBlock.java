package fuzs.beaconupgrade.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.beaconupgrade.world.item.enchantment.ClampedLevelBasedValue;
import fuzs.neoforgedatapackextensions.api.v2.DataMapLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public record BeaconBaseBlock(LevelBasedValue strength) {
    public static final Codec<BeaconBaseBlock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    LevelBasedValue.CODEC.fieldOf("strength").forGetter(BeaconBaseBlock::strength))
            .apply(instance, BeaconBaseBlock::new));
    public static final BeaconBaseBlock DEFAULT = new BeaconBaseBlock(1);

    public BeaconBaseBlock(int strength) {
        this(new ClampedLevelBasedValue(LevelBasedValue.constant(strength),
                LevelBasedValue.constant(0.0F),
                LevelBasedValue.perLevel(1.0F)));
    }

    public static boolean is(BlockState blockState) {
        return get(blockState) != null;
    }

    public static @Nullable BeaconBaseBlock get(BlockState blockState) {
        BeaconBaseBlock beaconBaseBlock = DataMapLookup.getData(ModRegistry.BEACON_BASE_BLOCKS_DATA_MAP_TYPE,
                blockState.getBlockHolder());
        if (beaconBaseBlock != null) {
            return beaconBaseBlock;
        } else if (blockState.is(BlockTags.BEACON_BASE_BLOCKS)) {
            return BeaconBaseBlock.DEFAULT;
        } else {
            return null;
        }
    }
}
