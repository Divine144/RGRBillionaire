package com.divinity.hmedia.rgrbillionaire.network.clientbound;

import com.divinity.hmedia.rgrbillionaire.network.ClientHandler;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class StopSoundPacket implements IPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(ClientHandler::stopSound);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
    }

    public static StopSoundPacket read(FriendlyByteBuf buf) {
        return new StopSoundPacket();
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, StopSoundPacket.class, StopSoundPacket::read);
    }
}