package com.divinity.hmedia.rgrbillionaire.menu;

import com.divinity.hmedia.rgrbillionaire.entity.AIRoboButlerEntity;
import com.divinity.hmedia.rgrbillionaire.init.MenuInit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;

public class ButlerInventoryMenu extends AbstractContainerMenu {

    private final Container horseContainer;
    public AIRoboButlerEntity butler;

    public ButlerInventoryMenu(int id, Inventory playerInv, FriendlyByteBuf extraData) {
        this(id, playerInv, (AIRoboButlerEntity) Objects.requireNonNull(playerInv.player.level().getEntity(extraData.readInt())));
    }

    public ButlerInventoryMenu(int pContainerId, Inventory pPlayerInventory, final AIRoboButlerEntity pHorse) {
        super(null, pContainerId);
        this.horseContainer = pHorse.getInventory();
        this.butler = pHorse;
        horseContainer.startOpen(pPlayerInventory.player);

        this.addSlot(new Slot(horseContainer, 0, 8, 18) {

            @Override
            public boolean mayPlace(ItemStack p_39677_) {
                return p_39677_.is(Items.GOLDEN_APPLE) && !this.hasItem();
            }
        });
        this.addSlot(new Slot(horseContainer, 1, 8, 36) {

            @Override
            public boolean mayPlace(ItemStack p_39690_) {
                return false;
            }

            @Override
            public boolean isActive() {
                return false;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        for(int k = 0; k < 3; ++k) {
            for(int l = 0; l < pHorse.getInventoryColumns(); ++l) {
                this.addSlot(new Slot(horseContainer, 2 + l + k * pHorse.getInventoryColumns(), 80 + l * 18, 18 + k * 18));
            }
        }

        for(int i1 = 0; i1 < 3; ++i1) {
            for(int k1 = 0; k1 < 9; ++k1) {
                this.addSlot(new Slot(pPlayerInventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 + -18));
            }
        }

        for(int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(pPlayerInventory, j1, 8 + j1 * 18, 142));
        }
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean stillValid(Player pPlayer) {
        return !this.butler.hasInventoryChanged(this.horseContainer) && this.horseContainer.stillValid(pPlayer) && this.butler.isAlive() && this.butler.distanceTo(pPlayer) < 8.0F;
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int i = this.horseContainer.getContainerSize();
            if (pIndex < i) {
                if (!this.moveItemStackTo(itemstack1, i, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).mayPlace(itemstack1) && !this.getSlot(1).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).mayPlace(itemstack1)) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i <= 2 || !this.moveItemStackTo(itemstack1, 2, i, false)) {
                int j = i + 27;
                int k = j + 9;
                if (pIndex >= j && pIndex < k) {
                    if (!this.moveItemStackTo(itemstack1, i, j, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex >= i && pIndex < j) {
                    if (!this.moveItemStackTo(itemstack1, j, k, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, j, j, false)) {
                    return ItemStack.EMPTY;
                }

                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public MenuType<?> getType() {
        return MenuInit.BUTLER_MENU.get();
    }

    /**
     * Called when the container is closed.
     */
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.horseContainer.stopOpen(pPlayer);
    }
}
