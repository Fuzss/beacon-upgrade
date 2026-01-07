package fuzs.beaconupgrade.world.level.block.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import fuzs.beaconupgrade.init.ModRegistry;
import fuzs.beaconupgrade.world.inventory.UpgradedBeaconMenu;
import fuzs.puzzleslib.api.block.v1.entity.TickingBlockEntity;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class UpgradedBeaconBlockEntity extends BeaconBlockEntity implements TickingBlockEntity {
    /**
     * The default {@link MobEffectInstance} amplifier for mob effects that are not present.
     *
     * @see MobEffectInstance#MIN_AMPLIFIER
     * @see MobEffectInstance#MAX_AMPLIFIER
     */
    public static final int DEFAULT_AMPLIFIER = -1;
    /**
     * Always require a level of at least one, as the beacon structure itself will not activate with less.
     */
    public static final int MIN_PYRAMID_LEVELS = 1;
    /**
     * Pick some reasonable value, as the number of blocks required for checking on the pyramid gets out of hand quickly
     * otherwise.
     */
    public static final int MAX_PYRAMID_LEVELS = 5;
    public static final int PYRAMID_LEVELS_DATA_SLOT = 0;
    public static final int PYRAMID_STRENGTH_DATA_SLOT = 1;
    public static final int PAYMENT_ITEM_DATA_SLOT = 2;
    public static final int EFFECT_TARGETS_DATA_SLOT = 3;
    public static final int CONTAINER_DATA_SLOTS = 4;
    public static final int PYRAMID_LEVELS_DATA_SLOTS = MAX_PYRAMID_LEVELS;
    public static final Codec<List<Holder<Block>>> PYRAMID_LEVELS_CODEC = Codec.list(BuiltInRegistries.BLOCK.holderByNameCodec(),
            MIN_PYRAMID_LEVELS,
            MAX_PYRAMID_LEVELS);
    public static final Codec<Object2IntMap<Holder<MobEffect>>> MOB_EFFECTS_CODEC = Codec.unboundedMap(MobEffect.CODEC,
                    Codec.intRange(MobEffectInstance.MIN_AMPLIFIER, MobEffectInstance.MAX_AMPLIFIER))
            .xmap(Object2IntOpenHashMap::new, Function.identity());
    public static final StreamCodec<RegistryFriendlyByteBuf, Object2IntMap<Holder<MobEffect>>> MOB_EFFECTS_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2IntOpenHashMap::new, MobEffect.STREAM_CODEC, ByteBufCodecs.VAR_INT),
            Object2IntOpenHashMap::new,
            Function.identity());
    public static final String TAG_PYRAMID_LEVELS = "pyramid_levels";
    public static final String TAG_MOB_EFFECTS = "mob_effects";
    public static final String TAG_PAYMENT_ITEM = "payment_item";
    public static final String TAG_EFFECT_TARGETS = "effect_targets";
    /**
     * @see BeaconBlockEntity#TAG_PRIMARY
     */
    private static final String TAG_PRIMARY = "primary_effect";
    /**
     * @see BeaconBlockEntity#TAG_SECONDARY
     */
    private static final String TAG_SECONDARY = "secondary_effect";

    @Nullable
    private Holder<Item> paymentItem;
    private BeaconEffectTargets effectTargets = BeaconEffectTargets.PLAYERS;
    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case PYRAMID_LEVELS_DATA_SLOT -> {
                    return UpgradedBeaconBlockEntity.this.getPyramidLevels();
                }
                case PYRAMID_STRENGTH_DATA_SLOT -> {
                    return UpgradedBeaconBlockEntity.this.getPyramidStrength();
                }
                case PAYMENT_ITEM_DATA_SLOT -> {
                    if (UpgradedBeaconBlockEntity.this.paymentItem != null) {
                        return BuiltInRegistries.ITEM.getIdOrThrow(UpgradedBeaconBlockEntity.this.paymentItem.value());
                    } else {
                        return -1;
                    }
                }
                case EFFECT_TARGETS_DATA_SLOT -> {
                    return UpgradedBeaconBlockEntity.this.effectTargets.ordinal();
                }
                default -> {
                    return -1;
                }
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case PAYMENT_ITEM_DATA_SLOT -> {
                    Item item = BuiltInRegistries.ITEM.byId(value);
                    if (item == Items.AIR) {
                        UpgradedBeaconBlockEntity.this.paymentItem = null;
                    } else {
                        UpgradedBeaconBlockEntity.this.paymentItem = BuiltInRegistries.ITEM.wrapAsHolder(item);
                    }
                }
                case EFFECT_TARGETS_DATA_SLOT ->
                        UpgradedBeaconBlockEntity.this.effectTargets = BeaconEffectTargets.BY_ID.apply(value);
            }
        }

        @Override
        public int getCount() {
            return CONTAINER_DATA_SLOTS;
        }
    };
    private final Object2IntMap<Holder<MobEffect>> mobEffects;
    private final ContainerData mobEffectsData = new ContainerData() {
        @Override
        public int get(int index) {
            MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.byId(index);
            if (mobEffect != null) {
                Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(mobEffect);
                return UpgradedBeaconBlockEntity.this.mobEffects.getInt(holder);
            } else {
                return DEFAULT_AMPLIFIER;
            }
        }

        @Override
        public void set(int index, int value) {
            MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.byId(index);
            if (mobEffect != null) {
                Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(mobEffect);
                UpgradedBeaconBlockEntity.this.setMobEffectAmplifier(holder, value);
            }
        }

        @Override
        public int getCount() {
            return BuiltInRegistries.MOB_EFFECT.size();
        }
    };
    private final ContainerData pyramidLevelsData = new ContainerData() {
        @Override
        public int get(int index) {
            if (index >= 0 && index < UpgradedBeaconBlockEntity.this.pyramidLevels.size()) {
                Holder<Block> holder = UpgradedBeaconBlockEntity.this.pyramidLevels.get(index);
                return BuiltInRegistries.BLOCK.getIdOrThrow(holder.value());
            } else {
                // This will return the default value from the block registry, which is air.
                return -1;
            }
        }

        @Override
        public void set(int index, int value) {
            // NO-OP
        }

        @Override
        public int getCount() {
            return PYRAMID_LEVELS_DATA_SLOTS;
        }
    };
    /**
     * The stored integers represent the numeric block ids (via {@link net.minecraft.core.IdMap#getId(Object)}) for the
     * block on that layer with the lowest value returned from {@link BeaconBaseBlock#getMaxPyramidStrength()}.
     *
     * @see #getPyramidLevels(Level, BlockPos)
     */
    private List<Holder<Block>> pyramidLevels = Collections.emptyList();

    public UpgradedBeaconBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
        this.type = ModRegistry.BEACON_BLOCK_ENTITY_TYPE.value();
        this.mobEffects = new Object2IntOpenHashMap<>();
        this.mobEffects.defaultReturnValue(DEFAULT_AMPLIFIER);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModRegistry.BEACON_BLOCK_ENTITY_TYPE.value();
    }

    public int getPyramidLevels() {
        return this.pyramidLevels.size();
    }

    public int getPyramidStrength() {
        int pyramidLevelBonus = 0;
        for (int pyramidLevel = 0; pyramidLevel < this.pyramidLevels.size(); pyramidLevel++) {
            BeaconBaseBlock beaconBaseBlock = BeaconBaseBlock.get(this.pyramidLevels.get(pyramidLevel));
            if (beaconBaseBlock != null) {
                pyramidLevelBonus += beaconBaseBlock.getPyramidStrength(pyramidLevel + 1);
            }
        }

        return pyramidLevelBonus;
    }

    private void setMobEffectAmplifier(Holder<MobEffect> mobEffect, int amplifier) {
        setMobEffectAmplifier(this.mobEffects, this.getPyramidLevels(), mobEffect, amplifier);
    }

    public static void setMobEffectAmplifier(Object2IntMap<Holder<MobEffect>> mobEffects, int pyramidLevels, Holder<MobEffect> mobEffect, int amplifier) {
        int maxAmplifier = BeaconLevelEffect.get(mobEffect).getMaxAmplifier(pyramidLevels);
        amplifier = Math.clamp(amplifier, DEFAULT_AMPLIFIER, maxAmplifier);
        if (amplifier >= MobEffectInstance.MIN_AMPLIFIER) {
            mobEffects.put(mobEffect, amplifier);
        } else {
            mobEffects.removeInt(mobEffect);
        }
    }

    @Override
    public void clientTick(Level level, BlockPos blockPos, BlockState blockState) {
        tick(level, blockPos, blockState, this);
    }

    @Override
    public void serverTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        tick(serverLevel, blockPos, blockState, this);
        if (serverLevel.getGameTime() % 80L == 0L) {
            if (this.getPyramidLevels() > 0 && !this.getBeamSections().isEmpty()) {
                this.applyEffects(serverLevel, blockPos);
            }
        }
    }

    public List<Holder<Block>> updatePowerLevels(Level level, BlockPos blockPos) {
        return this.pyramidLevels = this.getPyramidLevels(level, blockPos);
    }

    /**
     * @see BeaconBlockEntity#updateBase(Level, int, int, int)
     */
    private List<Holder<Block>> getPyramidLevels(Level level, BlockPos blockPos) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        ImmutableList.Builder<Holder<Block>> builder = ImmutableList.builder();
        for (int pyramidLevel = MIN_PYRAMID_LEVELS; pyramidLevel <= MAX_PYRAMID_LEVELS; pyramidLevel++) {
            int posY = blockPos.getY() - pyramidLevel;
            if (posY < level.getMinY()) {
                break;
            }

            int minBonus = Integer.MAX_VALUE;
            Holder<Block> minBonusHolder = null;
            for (int posX = blockPos.getX() - pyramidLevel; posX <= blockPos.getX() + pyramidLevel; ++posX) {
                for (int posZ = blockPos.getZ() - pyramidLevel; posZ <= blockPos.getZ() + pyramidLevel; ++posZ) {
                    BlockState blockState = level.getBlockState(mutableBlockPos.set(posX, posY, posZ));
                    if (minBonusHolder == null || !blockState.is(minBonusHolder)) {
                        BeaconBaseBlock beaconBaseBlock = BeaconBaseBlock.get(blockState.getBlockHolder());
                        if (beaconBaseBlock != null) {
                            int maxBonus = beaconBaseBlock.getMaxPyramidStrength();
                            if (maxBonus < minBonus) {
                                minBonus = maxBonus;
                                minBonusHolder = blockState.getBlockHolder();
                            }
                        } else {
                            return builder.build();
                        }
                    }
                }
            }

            if (minBonusHolder != null) {
                builder.add(minBonusHolder);
            } else {
                return builder.build();
            }
        }

        return builder.build();
    }

    /**
     * @see BeaconBlockEntity#applyEffects(Level, BlockPos, int, Holder, Holder)
     */
    private void applyEffects(ServerLevel serverLevel, BlockPos blockPos) {
        BeaconPaymentItem beaconPaymentItem = this.paymentItem != null ? BeaconPaymentItem.get(this.paymentItem) : null;
        if (beaconPaymentItem != null && !this.mobEffects.isEmpty()) {
            int duration = beaconPaymentItem.getDuration(this.getPyramidLevels());
            for (LivingEntity livingEntity : this.getEffectTargets(serverLevel, blockPos)) {
                for (Object2IntMap.Entry<Holder<MobEffect>> entry : this.mobEffects.object2IntEntrySet()) {
                    livingEntity.addEffect(new MobEffectInstance(entry.getKey(),
                            duration,
                            entry.getIntValue(),
                            true,
                            true));
                }
            }
        }
    }

    private List<LivingEntity> getEffectTargets(ServerLevel serverLevel, BlockPos blockPos) {
        double effectiveRadius = this.getEffectiveRadius();
        AABB aABB = new AABB(blockPos).inflate(effectiveRadius).expandTowards(0.0F, serverLevel.getHeight(), 0.0F);
        return serverLevel.getEntitiesOfClass(LivingEntity.class, aABB, this.effectTargets);
    }

    private int getEffectiveRadius() {
        int effectiveRadius = 0;
        for (int pyramidLevel = 0; pyramidLevel < this.pyramidLevels.size(); pyramidLevel++) {
            BeaconBaseBlock beaconBaseBlock = BeaconBaseBlock.get(this.pyramidLevels.get(pyramidLevel));
            if (beaconBaseBlock != null) {
                effectiveRadius += beaconBaseBlock.getEffectiveRadius(pyramidLevel + 1);
            }
        }

        return effectiveRadius;
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.pyramidLevels = valueInput.read(TAG_PYRAMID_LEVELS, PYRAMID_LEVELS_CODEC)
                .orElseGet(Collections::emptyList);
        this.levels = this.pyramidLevels.size();
        this.mobEffects.clear();
        valueInput.read(TAG_MOB_EFFECTS, MOB_EFFECTS_CODEC).ifPresent((Object2IntMap<Holder<MobEffect>> mobEffects) -> {
            mobEffects.forEach(this::setMobEffectAmplifier);
        });
        this.paymentItem = valueInput.read(TAG_PAYMENT_ITEM, Item.CODEC).orElse(null);
        this.effectTargets = valueInput.read(TAG_EFFECT_TARGETS, BeaconEffectTargets.CODEC)
                .orElse(BeaconEffectTargets.PLAYERS);
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        valueOutput.discard("Levels");
        valueOutput.discard(TAG_PRIMARY);
        valueOutput.discard(TAG_SECONDARY);
        if (!this.pyramidLevels.isEmpty()) {
            valueOutput.store(TAG_PYRAMID_LEVELS, PYRAMID_LEVELS_CODEC, this.pyramidLevels);
        }

        if (!this.mobEffects.isEmpty()) {
            valueOutput.store(TAG_MOB_EFFECTS, MOB_EFFECTS_CODEC, this.mobEffects);
        }

        valueOutput.storeNullable(TAG_PAYMENT_ITEM, Item.CODEC, this.paymentItem);
        valueOutput.store(TAG_EFFECT_TARGETS, BeaconEffectTargets.CODEC, this.effectTargets);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        if (this.lockKey.canUnlock(player)) {
            return new UpgradedBeaconMenu(containerId,
                    inventory,
                    this.containerData,
                    this.pyramidLevelsData,
                    this.mobEffectsData,
                    ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()));
        } else {
            return super.createMenu(containerId, inventory, player);
        }
    }
}
