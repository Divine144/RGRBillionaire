package com.divinity.hmedia.rgrbillionaire.network.serverbound;

import com.divinity.hmedia.rgrbillionaire.menu.MarketplaceMenu;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class UpdateMarketTradesPacket implements IPacket {

    int select;

    public UpdateMarketTradesPacket(int select) {
        this.select = select;
    }
    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                if (player.containerMenu instanceof MarketplaceMenu marketplaceMenu) {
                    System.out.println(this.select);
                    marketplaceMenu.tryMoveItems(this.select);
                    marketplaceMenu.setSelectionHint(this.select);
                }
            }
        });
    }

    public static UpdateMarketTradesPacket read(FriendlyByteBuf buffer) {
        return new UpdateMarketTradesPacket(buffer.readInt());
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, UpdateMarketTradesPacket.class, UpdateMarketTradesPacket::read);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeInt(select);
    }
}
