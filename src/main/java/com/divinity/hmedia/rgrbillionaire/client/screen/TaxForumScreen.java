package com.divinity.hmedia.rgrbillionaire.client.screen;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.menu.TaxForumMenu;
import com.divinity.hmedia.rgrbillionaire.network.NetworkHandler;
import com.divinity.hmedia.rgrbillionaire.network.serverbound.FinishedTaxForumPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class TaxForumScreen extends AbstractContainerScreen<TaxForumMenu> {

    private static final ResourceLocation[] LOCATIONS = new ResourceLocation[] {
            new ResourceLocation(RGRBillionaire.MODID, "textures/gui/screen/tax_forum_1.png"),
            new ResourceLocation(RGRBillionaire.MODID, "textures/gui/screen/tax_forum_2.png"),
            new ResourceLocation(RGRBillionaire.MODID, "textures/gui/screen/tax_forum_3.png"),
            new ResourceLocation(RGRBillionaire.MODID, "textures/gui/screen/tax_forum_4.png")
    };

    private int selectedGuiCounter = 0;

    public TaxForumScreen(TaxForumMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.addWidget(new Button.Builder(Component.empty(), p -> {
            selectedGuiCounter+= 1;
            if (!this.children().isEmpty()) {
                this.children().remove(0);
            }
        }).size(45, 30).pos((int) ((int) ((116) / 0.5) + (-17 / 0.5)), (int) ((int) ((j) / 0.5) + (25 / 0.5))).build());

        this.addWidget(new Button.Builder(Component.empty(), p -> {
            if (selectedGuiCounter == 1) {
                selectedGuiCounter += 1;
                if (!this.children().isEmpty()) {
                    this.children().remove(0);
                }
            }
        }).size(45, 30).pos((int) ((int) ((150) / 0.5) + (-17 / 0.5)), (int) ((int) ((j) / 0.5) + (25 / 0.5))).build());

        this.addWidget(new Button.Builder(Component.empty(), p -> {
            if (selectedGuiCounter == 2) {
                selectedGuiCounter += 1;
                NetworkHandler.INSTANCE.sendToServer(new FinishedTaxForumPacket());
                if (!this.children().isEmpty()) {
                    this.children().remove(0);
                }
            }
        }).size(85, 30).pos((int) ((int) ((125) / 0.5) + (-17 / 0.5)), (int) ((int) ((j) / 0.5) + (50 / 0.5))).build());
    }

    @Override
    public void onClose() {
        if (selectedGuiCounter >= 3) {
            super.onClose();
        }
    }

    @Override
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
        RenderSystem.setShaderTexture(0, LOCATIONS[Mth.clamp(selectedGuiCounter, 0, 3)]);
        int i = (this.width - this.imageWidth);
        int j = (this.height - this.imageHeight);
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().scale(0.5f, 0.5f, 0.5f);
        pGuiGraphics.blit(LOCATIONS[Mth.clamp(selectedGuiCounter, 0, 3)], (int) ((116) / 0.5), 0, 0, 0, 512, 512, 512, 512);
        pGuiGraphics.pose().popPose();
    }
}
