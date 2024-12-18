package com.divinity.hmedia.rgrbillionaire.network;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.cap.GlobalLevelHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.network.clientbound.PlayConfettiAnimationPacket;
import com.divinity.hmedia.rgrbillionaire.network.clientbound.StopSoundPacket;
import com.divinity.hmedia.rgrbillionaire.network.clientbound.SyncCryptoBlockPacket;
import com.divinity.hmedia.rgrbillionaire.network.serverbound.*;
import com.google.common.collect.ImmutableList;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleLevelCapabilityStatusPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;
import java.util.function.BiConsumer;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RGRBillionaire.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int nextId = 0;

    public static void register() {
        List<BiConsumer<SimpleChannel, Integer>> packets = ImmutableList.<BiConsumer<SimpleChannel, Integer>>builder()
                .add(SimpleEntityCapabilityStatusPacket::register)
                .add(SimpleLevelCapabilityStatusPacket::register)
                .add(UpdateMarketTradesPacket::register)
                .add(OpenMarketMenuPacket::register)
                .add(OpenMinebookScreenPacket::register)
                .add(UpdateMarketOfferPacket::register)
                .add(SyncCryptoBlockPacket::register)
                .add(FinishedTaxForumPacket::register)
                .add(MugOfCoffeePacket::register)
                .add(RecallButlerPacket::register)
                .add(PlayConfettiAnimationPacket::register)
                .add(StopSoundPacket::register)
                .build();
        SimpleEntityCapabilityStatusPacket.registerRetriever(BillionaireHolderAttacher.EXAMPLE_RL, BillionaireHolderAttacher::getHolderUnwrap);
        SimpleLevelCapabilityStatusPacket.registerRetriever(GlobalLevelHolderAttacher.EXAMPLE_GLOBAL_LEVEL_CAPABILITY_RL, GlobalLevelHolderAttacher::getGlobalLevelCapabilityUnwrap);
        packets.forEach(consumer -> consumer.accept(INSTANCE, getNextId()));
    }

    private static int getNextId() {
        return nextId++;
    }
}