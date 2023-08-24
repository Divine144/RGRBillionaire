package com.divinity.hmedia.rgrbillionaire.network.serverbound;

import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.init.SoundInit;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
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
                        Vec3 vec3 = player.getDeltaMovement();
                        player.setDeltaMovement(vec3.x, (player.getJumpBoostPower() + 0.42) * 2, vec3.z);
                        if (player.isSprinting()) {
                            float f = player.getYRot() * ((float)Math.PI / 180F);
                            player.setDeltaMovement(player.getDeltaMovement().add((double)(-Mth.sin(f) * 0.2F), 0.0D, (double)(Mth.cos(f) * 0.2F)));
                        }
                        player.serverLevel().sendParticles(ParticleTypes.POOF, player.getX(), player.getY() - 1, player.getZ(), 4, 0, 0, 0, 0.1);
                        player.level().playSound(null, player.blockPosition(), SoundInit.BOING.get(), SoundSource.PLAYERS, 1f, 1f);
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
