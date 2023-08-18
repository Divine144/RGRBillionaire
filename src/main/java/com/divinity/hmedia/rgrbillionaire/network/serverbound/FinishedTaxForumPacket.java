package com.divinity.hmedia.rgrbillionaire.network.serverbound;

import com.divinity.hmedia.rgrbillionaire.init.MarkerInit;
import dev._100media.capabilitysyncer.network.IPacket;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class FinishedTaxForumPacket implements IPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                MarkerHolderAttacher.getMarkerHolder(player).ifPresent(m -> m.removeMarker(MarkerInit.TAX_FORM.get(), false));
                player.removeEffect(MobEffects.WEAKNESS);
            }
        });
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {

    }

    public static FinishedTaxForumPacket read(FriendlyByteBuf buf) {
        return new FinishedTaxForumPacket();
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, FinishedTaxForumPacket.class, FinishedTaxForumPacket::read);
    }
}
