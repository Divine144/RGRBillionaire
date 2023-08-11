package com.divinity.hmedia.rgrbillionaire.network.serverbound;

import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.cap.MoneyHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import com.divinity.hmedia.rgrbillionaire.menu.MarketplaceMenu;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
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
                    ItemStack resultStack = marketplaceMenu.getOffers().get(select).getResult();
                    ItemStack costAStack = marketplaceMenu.getOffers().get(select).getCostA();
                    if (costAStack.is(ItemInit.MONEY.get())) {
                        BillionaireHolderAttacher.getHolder(player).ifPresent(cap -> {
                            MoneyHolderAttacher.getItemStackCapability(costAStack).ifPresent(money -> {
                                cap.addMoney(-money.getAmount());
                            });
                        });
                        marketplaceMenu.setResultItem(resultStack.copy());
                    }
                    else if (resultStack.is(ItemInit.MONEY.get())) {
                        int i = player.getInventory().findSlotMatchingItem(costAStack);
                        if (i != -1) {
                            ItemStack stack = player.getInventory().getItem(i);
                            if (stack != ItemStack.EMPTY) {
                                if (stack.getCount() >= costAStack.getCount()) {
                                    stack.shrink(costAStack.getCount());
                                    BillionaireHolderAttacher.getHolder(player).ifPresent(cap -> {
                                        MoneyHolderAttacher.getItemStackCapability(resultStack).ifPresent(money -> {
                                            cap.addMoney(money.getAmount());
                                        });
                                    });

                                }
                            }
                        }
                        else if (marketplaceMenu.getCarried().is(costAStack.getItem()) && marketplaceMenu.getCarried().getCount() >= costAStack.getCount()) {
                            marketplaceMenu.getCarried().shrink(costAStack.getCount());
                            BillionaireHolderAttacher.getHolder(player).ifPresent(cap -> {
                                MoneyHolderAttacher.getItemStackCapability(resultStack).ifPresent(money -> {
                                    cap.addMoney(money.getAmount());
                                });
                            });
                        }
                    }
                    else {
                        marketplaceMenu.setResultItem(resultStack.copy());
                    }
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
