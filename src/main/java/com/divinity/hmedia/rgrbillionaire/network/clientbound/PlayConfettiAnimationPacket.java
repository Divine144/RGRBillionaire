package com.divinity.hmedia.rgrbillionaire.network.clientbound;

import com.divinity.hmedia.rgrbillionaire.network.ClientHandler;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class PlayConfettiAnimationPacket implements IPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(ClientHandler::startConfettiAnimation);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
    }

    public static PlayConfettiAnimationPacket read(FriendlyByteBuf buf) {
        return new PlayConfettiAnimationPacket();
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, PlayConfettiAnimationPacket.class, PlayConfettiAnimationPacket::read);
    }
}
