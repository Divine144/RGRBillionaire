package com.divinity.hmedia.rgrbillionaire.client.screen;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.menu.MinebookMenu;
import com.divinity.hmedia.rgrbillionaire.network.NetworkHandler;
import com.divinity.hmedia.rgrbillionaire.network.serverbound.OpenMarketMenuPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

public class MinebookScreen extends AbstractContainerScreen<MinebookMenu> {

    private static final ResourceLocation GUI = new ResourceLocation(RGRBillionaire.MODID, "textures/gui/screen/starlinked_gui.png");

    public MinebookScreen(MinebookMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 256;
        this.imageHeight = 256;
    }

    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        ItemStack stack = Items.POTION.getDefaultInstance();
        PotionUtils.setPotion(stack, Potions.REGENERATION);
        this.addRenderableWidget(new CustomImageButton(i + 25, j + 97, 30, 31, Items.DIRT.getDefaultInstance(), "Blocks Market", p -> {
            NetworkHandler.INSTANCE.sendToServer(new OpenMarketMenuPacket(0));
        }));
        this.addRenderableWidget(new CustomImageButton(i + 65, j + 97, 30, 31, Items.DIAMOND.getDefaultInstance(), "Ores Market", p -> {
            NetworkHandler.INSTANCE.sendToServer(new OpenMarketMenuPacket(1));
        }));
        this.addRenderableWidget(new CustomImageButton(i + 105, j + 97, 30, 31, Items.COOKED_BEEF.getDefaultInstance(), "Food Market", p -> {
            NetworkHandler.INSTANCE.sendToServer(new OpenMarketMenuPacket(2));
        }));
        this.addRenderableWidget(new CustomImageButton(i + 145, j + 97, 30, 31, Items.ENCHANTED_BOOK.getDefaultInstance(), "Enchanted Book Market", p -> {
            NetworkHandler.INSTANCE.sendToServer(new OpenMarketMenuPacket(3));
        }));
        this.addRenderableWidget(new CustomImageButton(i + 185, j + 97, 30, 31, stack, "Potion/Misc Market", p -> {
            NetworkHandler.INSTANCE.sendToServer(new OpenMarketMenuPacket(4));
        }));
    }

    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {

    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        this.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        int i = (this.width - this.imageWidth) / 2 - 6;
        int j = (this.height - this.imageHeight) / 2 + 20;
        pGuiGraphics.blit(GUI, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    private class CustomImageButton extends Button {

        private ItemStack imageStack;
        private String onHover;
        private int offsetX;
        private int offsetY;

        public CustomImageButton(int pX, int pY, int pWidth, int pHeight, ItemStack imageStack, String onHover, OnPress pOnPress) {
            super(pX, pY, pWidth, pHeight, Component.literal(""), pOnPress, DEFAULT_NARRATION);
            this.imageStack = imageStack;
            this.onHover = onHover;
        }

        public CustomImageButton(int pX, int pY, int pWidth, int pHeight, int offsetX, int offsetY, ItemStack imageStack, String onHover, OnPress pOnPress) {
            this(pX, pY, pWidth, pHeight, imageStack, onHover, pOnPress);
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            pGuiGraphics.pose().pushPose();
            float scaleFactor = 1.275f;
            pGuiGraphics.pose().scale(scaleFactor, scaleFactor, scaleFactor);
            pGuiGraphics.renderItem(imageStack, (int) (this.getX() / scaleFactor) + 4 + offsetX, (int) (this.getY() / scaleFactor) + (int) ((6 + offsetY) / scaleFactor));
            pGuiGraphics.pose().popPose();
            var minecraft = MinebookScreen.this.minecraft;
            if (minecraft != null) {
                if (this.isMouseOver(pMouseX, pMouseY)) {
                    pGuiGraphics.renderTooltip(minecraft.font, Component.literal(onHover).withStyle(ChatFormatting.AQUA), pMouseX, pMouseY);
                }
            }
        }
    }
}
