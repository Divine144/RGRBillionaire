package com.divinity.hmedia.rgrbillionaire.network.serverbound;

import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.entity.AIRoboButlerEntity;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class RecallButlerPacket implements IPacket {
    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                BillionaireHolderAttacher.getHolder(player).ifPresent(p -> {
                    int id = p.getLinkedEntityID();
                    var entity = player.serverLevel().getEntity(id);
                    if (entity instanceof AIRoboButlerEntity roboButlerEntity) {
                        roboButlerEntity.setSitting(true);
                        roboButlerEntity.setPos(player.getX(), player.getY(), player.getZ());
                        player.sendSystemMessage(Component.literal("Butler Recalled").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD), true);
                    }
                });
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
    }

    public static RecallButlerPacket read(FriendlyByteBuf buf) {
        return new RecallButlerPacket();
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, RecallButlerPacket.class, RecallButlerPacket::read);
    }
}
