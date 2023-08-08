package com.divinity.hmedia.rgrbillionaire.network.serverbound;

import com.divinity.hmedia.rgrbillionaire.init.MenuInit;
import com.divinity.hmedia.rgrbillionaire.menu.MinebookMenu;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.simple.SimpleChannel;

public class OpenMinebookScreenPacket implements IPacket {

    public OpenMinebookScreenPacket() {}

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                NetworkHooks.openScreen(player,
                        new SimpleMenuProvider((id, inv, pl) -> new MinebookMenu(MenuInit.MINEBOOK_SCREEN.get(), id), Component.literal("How are we here")));
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {}

    public static OpenMinebookScreenPacket read(FriendlyByteBuf buf) {
        return new OpenMinebookScreenPacket();
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, OpenMinebookScreenPacket.class, OpenMinebookScreenPacket::read);
    }
}
