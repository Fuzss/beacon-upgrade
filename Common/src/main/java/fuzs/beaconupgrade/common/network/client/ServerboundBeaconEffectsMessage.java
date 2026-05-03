package fuzs.beaconupgrade.common.network.client;

import fuzs.beaconupgrade.common.world.inventory.UpgradedBeaconMenu;
import fuzs.beaconupgrade.common.world.level.block.entity.UpgradedBeaconBlockEntity;
import fuzs.puzzleslib.common.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.common.api.network.v4.message.play.ServerboundPlayMessage;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;

public record ServerboundBeaconEffectsMessage(int containerId,
                                              Object2IntMap<Holder<MobEffect>> mobEffects) implements ServerboundPlayMessage {
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundBeaconEffectsMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundBeaconEffectsMessage::containerId,
            UpgradedBeaconBlockEntity.MOB_EFFECTS_STREAM_CODEC,
            ServerboundBeaconEffectsMessage::mobEffects,
            ServerboundBeaconEffectsMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                if (context.player().containerMenu instanceof UpgradedBeaconMenu menu
                        && menu.containerId == ServerboundBeaconEffectsMessage.this.containerId) {
                    menu.updateEffects(ServerboundBeaconEffectsMessage.this.mobEffects);
                }
            }
        };
    }
}
