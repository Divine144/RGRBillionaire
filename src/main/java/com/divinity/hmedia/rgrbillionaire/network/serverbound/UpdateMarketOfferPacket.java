package com.divinity.hmedia.rgrbillionaire.network.serverbound;

import com.divinity.hmedia.rgrbillionaire.menu.MarketplaceMenu;
import com.divinity.hmedia.rgrbillionaire.menu.offer.CustomMerchantOffer;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public record UpdateMarketOfferPacket(int select, boolean hasVariant, boolean hasSellOffer) implements IPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                if (player.containerMenu instanceof MarketplaceMenu marketplaceMenu) {
                    MerchantOffer offer = marketplaceMenu.getOffers().get(select);
                    ItemStack resultCopy = offer.getResult().copy();
                    ItemStack aCopy = offer.getCostA().copy();
                    if (!hasVariant) {
                        var customOffer = new CustomMerchantOffer(resultCopy, aCopy, offer.getMaxUses(), offer.getXp(), offer.getPriceMultiplier());
                        if (hasSellOffer) {
                            customOffer.markSellOffer();
                        }
                        marketplaceMenu.getOffers().set(select, customOffer);
                    }
                    else {
                        var customOffer = marketplaceMenu.getTrader().getOfferAt(select);
                        if (customOffer != null) {
                            customOffer.getNextStack();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeInt(select);
        packetBuf.writeBoolean(hasVariant);
        packetBuf.writeBoolean(hasSellOffer);
    }

    public static UpdateMarketOfferPacket read(FriendlyByteBuf buffer) {
        return new UpdateMarketOfferPacket(buffer.readInt(), buffer.readBoolean(), buffer.readBoolean());
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, UpdateMarketOfferPacket.class, UpdateMarketOfferPacket::read);
    }
}
