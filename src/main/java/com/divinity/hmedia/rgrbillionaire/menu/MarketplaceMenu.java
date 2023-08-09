package com.divinity.hmedia.rgrbillionaire.menu;

import com.divinity.hmedia.rgrbillionaire.entity.special.DummyMerchant;
import com.divinity.hmedia.rgrbillionaire.init.MenuInit;
import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import dev._100media.hundredmediaquests.menu.AlwaysValidMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffers;

public class MarketplaceMenu extends AlwaysValidMenu {

    private final Merchant trader;
    private final MerchantContainer tradeContainer;

    public MarketplaceMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf buf) {
        this(pContainerId, pPlayerInventory, new DummyMerchant(pPlayerInventory.player, BillionaireUtils.allOffers.get(buf.readInt())));
    }

    public MarketplaceMenu(int pContainerId, Inventory pPlayerInventory, Merchant pTrader) {
        super(MenuType.MERCHANT, pContainerId);
        this.trader = pTrader;
        this.tradeContainer = new MerchantContainer(pTrader) {
            @Override
            public void setChanged() {}
        };
        this.addSlot(new Slot(this.tradeContainer, 0, 136, 37) {
            @Override
            public boolean isActive() {
                return false;
            }
        });
        this.addSlot(new Slot(this.tradeContainer, 1, 162, 37) {
            @Override
            public boolean isActive() {
                return false;
            }
        });
        this.addSlot(new MerchantResultSlot(pPlayerInventory.player, pTrader, this.tradeContainer, 2, 179, 37));
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 108 + j * 18, 84 + i * 18));
            }
        }
        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(pPlayerInventory, k, 108 + k * 18, 142));
        }
    }

    @Override
    public MenuType<?> getType() {
        return MenuInit.MARKET_MENU.get();
    }

    public void slotsChanged(Container pInventory) {
        super.slotsChanged(pInventory);
    }

    public void setResultItem(ItemStack stack) {
        if (!tradeContainer.getItem(2).isEmpty()) {
            ItemStack resultStack = tradeContainer.getItem(2);
            if (resultStack.getCount() < resultStack.getMaxStackSize()) {
                resultStack.grow(stack.getCount());
            }
            else {
                ItemStack containerStack = this.tradeContainer.removeItemNoUpdate(2);
                Player player = this.trader.getTradingPlayer();
                if (!containerStack.isEmpty() && player != null) {
                    player.getInventory().placeItemBackInInventory(containerStack);
                }
            }
        }
        else {
            this.tradeContainer.setItem(2, stack.copy());
        }
    }

    public boolean stillValid(Player pPlayer) {
        return this.trader.getTradingPlayer() == pPlayer;
    }

    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        return false;
    }

    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex == 2) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
                this.playTradeSound();
            }
            else if (pIndex != 0 && pIndex != 1) {
                if (pIndex >= 3 && pIndex < 30) {
                    if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex >= 30 && pIndex < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(pPlayer, itemstack1);
        }
        return itemstack;
    }

    private void playTradeSound() {
        Player player = this.trader.getTradingPlayer();
        if (player != null && player.level().isClientSide) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), this.trader.getNotifyTradeSound(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }

    /**
     * Called when the container is closed.
     */
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.trader.setTradingPlayer((Player)null);
        if (!this.trader.isClientSide()) {
            if (!pPlayer.isAlive() || pPlayer instanceof ServerPlayer && ((ServerPlayer)pPlayer).hasDisconnected()) {
                ItemStack itemstack = this.tradeContainer.removeItemNoUpdate(2);
                if (!itemstack.isEmpty()) {
                    pPlayer.drop(itemstack, false);
                }
            }
            else if (pPlayer instanceof ServerPlayer) {
                ItemStack stack = this.tradeContainer.removeItemNoUpdate(2);
                if (!stack.isEmpty()) {
                    pPlayer.getInventory().placeItemBackInInventory(stack);
                }
            }

        }
    }

    public MerchantOffers getOffers() {
        return this.trader.getOffers();
    }

    public DummyMerchant getTrader() {
        return (DummyMerchant) this.trader;
    }
}
