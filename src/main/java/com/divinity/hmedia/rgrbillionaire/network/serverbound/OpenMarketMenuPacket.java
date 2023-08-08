package com.divinity.hmedia.rgrbillionaire.network.serverbound;

import com.divinity.hmedia.rgrbillionaire.entity.special.DummyMerchant;
import com.divinity.hmedia.rgrbillionaire.menu.MarketplaceMenu;
import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.simple.SimpleChannel;

public record OpenMarketMenuPacket(int menuID) implements IPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                NetworkHooks.openScreen(player,
                        new SimpleMenuProvider((id, inv, pl) ->
                                new MarketplaceMenu(id, inv, new DummyMerchant(pl, BillionaireUtils.allOffers.get(menuID))),
                                Component.literal("How are we here")),
                        buf -> {
                        buf.writeInt(menuID);
                });
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeInt(menuID);
    }

    public static OpenMarketMenuPacket read(FriendlyByteBuf buf) {
        return new OpenMarketMenuPacket(buf.readInt());
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, OpenMarketMenuPacket.class, OpenMarketMenuPacket::read);
    }
}
