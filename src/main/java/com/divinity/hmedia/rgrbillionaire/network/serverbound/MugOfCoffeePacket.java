package com.divinity.hmedia.rgrbillionaire.network.serverbound;

import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class MugOfCoffeePacket implements IPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                BillionaireHolderAttacher.getHolder(player).ifPresent(p -> {
                    if (p.isCanDoubleJump() && !player.onGround()) {
                        player.hurtMarked = true;
                        player.jumpFromGround();
                        player.serverLevel().sendParticles(ParticleTypes.POOF, player.getX(), player.getY() - 1, player.getZ(), 4, 0, 0, 0, 0.1);
                        p.setCanDoubleJump(false);
                    }
                });
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
    }

    public static MugOfCoffeePacket read(FriendlyByteBuf buf) {
        return new MugOfCoffeePacket();
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, MugOfCoffeePacket.class, MugOfCoffeePacket::read);
    }
}
