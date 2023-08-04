package com.divinity.hmedia.rgrbillionaire.client.screen;

import com.divinity.hmedia.rgrbillionaire.entity.AIRoboButlerEntity;
import com.divinity.hmedia.rgrbillionaire.menu.ButlerInventoryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ButlerInventoryScreen extends AbstractContainerScreen<ButlerInventoryMenu> {

    private static final ResourceLocation HORSE_INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/horse.png");

    private final AIRoboButlerEntity horse;
    private float xMouse;
    private float yMouse;

    public ButlerInventoryScreen(ButlerInventoryMenu pMenu, Inventory pPlayerInventory, Component title) {
        super(pMenu, pPlayerInventory, title);
        this.horse = pMenu.butler;
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(HORSE_INVENTORY_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
        pGuiGraphics.blit(HORSE_INVENTORY_LOCATION, i + 79, j + 17, 0, this.imageHeight, horse.getInventoryColumns() * 18, 54);

        // Saddle
        pGuiGraphics.blit(HORSE_INVENTORY_LOCATION, i + 7, j + 35 - 18, 18, this.imageHeight + 54, 18, 18);
        InventoryScreen.renderEntityInInventoryFollowsMouse(pGuiGraphics, i + 51, j + 60, 17, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.horse);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        this.xMouse = (float)pMouseX;
        this.yMouse = (float)pMouseY;
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
