package com.divinity.hmedia.rgrbillionaire.network.clientbound;

import com.divinity.hmedia.rgrbillionaire.network.ClientHandler;
import com.divinity.hmedia.rgrbillionaire.network.serverbound.OpenMarketMenuPacket;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public record SyncCryptoBlockPacket(int amount, BlockPos pos) implements IPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientHandler.syncCryptoMiner(amount, pos));
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeInt(amount);
        packetBuf.writeBlockPos(pos);
    }

    public static SyncCryptoBlockPacket read(FriendlyByteBuf buf) {
        return new SyncCryptoBlockPacket(buf.readInt(), buf.readBlockPos());
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, SyncCryptoBlockPacket.class, SyncCryptoBlockPacket::read);
    }
}
