package com.divinity.hmedia.rgrbillionaire.client.screen;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolder;
import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.cap.MoneyHolder;
import com.divinity.hmedia.rgrbillionaire.cap.MoneyHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import com.divinity.hmedia.rgrbillionaire.menu.MarketplaceMenu;
import com.divinity.hmedia.rgrbillionaire.menu.offer.CustomMerchantOffer;
import com.divinity.hmedia.rgrbillionaire.network.NetworkHandler;
import com.divinity.hmedia.rgrbillionaire.network.serverbound.OpenMinebookScreenPacket;
import com.divinity.hmedia.rgrbillionaire.network.serverbound.UpdateMarketOfferPacket;
import com.divinity.hmedia.rgrbillionaire.network.serverbound.UpdateMarketTradesPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class MarketplaceScreen extends AbstractContainerScreen<MarketplaceMenu> {

    private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation(RGRBillionaire.MODID,"textures/gui/screen/villager2.png");
    private static final Component TRADES_LABEL = Component.literal("Offers");

    private int shopItem;
    private int clickedItem = -1;
    private boolean displayConfirm;
    private final MarketplaceScreen.TradeOfferButton[] tradeOfferButtons = new MarketplaceScreen.TradeOfferButton[7];
    int scrollOff;
    private boolean isDragging;

    public MarketplaceScreen(MarketplaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 276;
        this.inventoryLabelX = 107;
    }

    private void postButtonClick() {
        displayConfirm = true;
    }

    @Override
    public void onClose() {
        NetworkHandler.INSTANCE.sendToServer(new OpenMinebookScreenPacket());
    }

    private void postButtonConfirmClick() {
        ItemStack resultStack = this.menu.getOffers().get(shopItem).getResult();
        ItemStack costA = this.menu.getOffers().get(shopItem).getCostA();
        int moneyAmount = MoneyHolderAttacher.getItemStackCapability(costA).map(MoneyHolder::getAmount).orElse(0);
        int playerAmount = 0, maxPlayerAmount = 0;
        if (this.minecraft != null && this.minecraft.player != null) {
            playerAmount = BillionaireHolderAttacher.getHolder(this.minecraft.player).map(BillionaireHolder::getMoney).orElse(0);
            maxPlayerAmount = BillionaireHolderAttacher.getHolder(this.minecraft.player).map(BillionaireHolder::getMoneyCap).orElse(0);
        }
        if (costA.is(ItemInit.MONEY.get())) {
            if (playerAmount > moneyAmount) {
                if (!resultStack.is(ItemInit.MONEY.get()) ) {
                    if (resultStack.is(Items.LINGERING_POTION)) {
                        if (minecraft != null && minecraft.getConnection() != null) {
                            var advancements = minecraft.getConnection().getAdvancements().getAdvancements();
                            if (advancements.get(KILLED_DRAGON) == null) {
                                return;
                            }
                        }
                    }
                    if (Screen.hasShiftDown()) {
                        for (int i = playerAmount; i >= moneyAmount; i -= moneyAmount) {
                            if (this.minecraft != null && this.minecraft.player != null) {
                                LocalPlayer player = minecraft.player;
                                if (player.getInventory().getSlotWithRemainingSpace(resultStack.copy()) != -1 || player.getInventory().getFreeSlot() != -1) {
                                    this.menu.setResultItem(resultStack.copy());
                                    NetworkHandler.INSTANCE.sendToServer(new UpdateMarketTradesPacket(shopItem));
                                }
                            }
                        }
                    }
                    else {
                        this.menu.setResultItem(resultStack.copy());
                        NetworkHandler.INSTANCE.sendToServer(new UpdateMarketTradesPacket(shopItem));
                    }

                }
            }
        }
        else {
            int resultMoneyAmount = MoneyHolderAttacher.getItemStackCapability(resultStack).map(MoneyHolder::getAmount).orElse(0);
            if (this.minecraft != null && this.minecraft.player != null) {
                int i = minecraft.player.getInventory().findSlotMatchingItem(costA);
                if (i != -1) {
                    ItemStack stack = minecraft.player.getInventory().getItem(i);
                    if (stack != ItemStack.EMPTY) {
                        if (stack.getCount() >= costA.getCount()) {
                            if (Screen.hasShiftDown()) {
                                for (int j = stack.getCount(); j >= costA.getCount(); j -= costA.getCount()) {
                                    if (playerAmount + resultMoneyAmount <= maxPlayerAmount) {
                                        playerAmount += resultMoneyAmount;
                                        NetworkHandler.INSTANCE.sendToServer(new UpdateMarketTradesPacket(shopItem));
                                    }
                                    else break;
                                }
                            }
                            else {
                                if (playerAmount + resultMoneyAmount <= maxPlayerAmount) {
                                    NetworkHandler.INSTANCE.sendToServer(new UpdateMarketTradesPacket(shopItem));
                                }
                            }
                        }
                    }
                }
                else if (this.menu.getCarried().is(costA.getItem()) && this.menu.getCarried().getCount() >= costA.getCount()) {
                    if (Screen.hasShiftDown()) {
                        ItemStack stack = this.menu.getCarried();
                        for (int j = stack.getCount(); j >= costA.getCount(); j -= costA.getCount()) {
                            if (playerAmount + resultMoneyAmount <= maxPlayerAmount) {
                                playerAmount += resultMoneyAmount;
                                stack.shrink(costA.getCount());
                                NetworkHandler.INSTANCE.sendToServer(new UpdateMarketTradesPacket(shopItem));
                            }
                            else break;
                        }
                    }
                    else {
                        this.menu.setCarried(ItemStack.EMPTY);
                        NetworkHandler.INSTANCE.sendToServer(new UpdateMarketTradesPacket(shopItem));
                    }
                }
            }
        }
        clickedItem = -1;
    }

    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int k = j + 16 + 2;

        for(int l = 0; l < 7; ++l) {
            this.tradeOfferButtons[l] = this.addRenderableWidget(new MarketplaceScreen.TradeOfferButton(i + 5, k, l, (p_99174_) -> {
                if (p_99174_ instanceof MarketplaceScreen.TradeOfferButton) {

                    this.shopItem = ((MarketplaceScreen.TradeOfferButton)p_99174_).getIndex() + this.scrollOff;
                    this.postButtonClick();
                    if (shopItem == clickedItem) {
                        this.postButtonConfirmClick();
                    }
                    this.clickedItem = shopItem;
                }
            }));
            k += 20;
        }
    }

    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        if (this.minecraft != null) {
            LocalPlayer player = this.minecraft.player;
            if (player != null) {
                int carriedMoney = BillionaireHolderAttacher.getHolder(player).map(BillionaireHolder::getMoney).orElse(0);
                int maxCarriedMoney = BillionaireHolderAttacher.getHolder(player).map(BillionaireHolder::getMoneyCap).orElse(0);
                Component component = Component.literal("Money: $%s/$%s".formatted(carriedMoney, maxCarriedMoney)).withStyle(ChatFormatting.DARK_GREEN);
                int j = this.font.width(component);
                int k = 49 + this.imageWidth / 2 - j / 2;
                pGuiGraphics.drawString(this.font, component, k, 6, 4210752, false);
                if (displayConfirm) {
                    Component title = Component.literal("");
                    int k1 = this.shopItem;
                    MerchantOffer merchantoffer1 = this.menu.getOffers().get(k1);
                    ItemStack costA = merchantoffer1.getCostA();

                    if (costA.is(ItemInit.MONEY.get())) {
                        int moneyAmount = MoneyHolderAttacher.getItemStackCapability(costA).map(MoneyHolder::getAmount).orElse(0);
                        int playerAmount = 0;
                        if (this.minecraft != null && this.minecraft.player != null) {
                            playerAmount = BillionaireHolderAttacher.getHolder(this.minecraft.player).map(BillionaireHolder::getMoney).orElse(0);
                        }
                        if (playerAmount < moneyAmount) {
                            title = Component.literal("Insufficient Funds").withStyle(ChatFormatting.RED);
                            if (merchantoffer1.getResult().is(Items.LINGERING_POTION)) {
                                if (minecraft != null && minecraft.getConnection() != null) {
                                    var advancements = minecraft.getConnection().getAdvancements().getAdvancements();
                                    if (advancements.get(KILLED_DRAGON) == null) {
                                        title = Component.literal("Not Unlocked").withStyle(ChatFormatting.RED);
                                    }
                                }
                            }
                        }
                        else {
                            title = Component.literal("Click to Confirm Purchase").withStyle(ChatFormatting.DARK_GREEN);
                            if (merchantoffer1.getResult().is(Items.LINGERING_POTION)) {
                                if (minecraft != null && minecraft.getConnection() != null) {
                                    var advancements = minecraft.getConnection().getAdvancements().getAdvancements();
                                    if (advancements.get(KILLED_DRAGON) == null) {
                                        title = Component.literal("Not Unlocked").withStyle(ChatFormatting.RED);
                                    }
                                }
                            }
                        }
                    }
                    else {
                        if (this.minecraft != null && this.minecraft.player != null) {
                            int i = minecraft.player.getInventory().findSlotMatchingItem(costA);
                            if (i != -1) {
                                ItemStack stack = minecraft.player.getInventory().getItem(i);
                                if (stack != ItemStack.EMPTY) {
                                    if (stack.getCount() >= costA.getCount()) {
                                        title = Component.literal("Click to Confirm Selling").withStyle(ChatFormatting.DARK_GREEN);
                                    }
                                    else title = Component.literal("Insufficient Materials").withStyle(ChatFormatting.RED);
                                }
                                else title = Component.literal("Insufficient Materials").withStyle(ChatFormatting.RED);
                            }
                            else if (this.menu.getCarried().is(costA.getItem()) && this.menu.getCarried().getCount() >= costA.getCount()) {
                                title = Component.literal("Click to Confirm Selling").withStyle(ChatFormatting.DARK_GREEN);
                            }
                            else title = Component.literal("Insufficient Materials").withStyle(ChatFormatting.RED);
                        }
                    }
                    pGuiGraphics.drawString(this.font, title, 49 + this.imageWidth / 2 - this.font.width(title) / 2, 16, 4210752, false);
                }

            }
        }
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
        int l = this.font.width(TRADES_LABEL);
        pGuiGraphics.drawString(this.font, TRADES_LABEL, 5 - l / 2 + 48, 6, 4210752, false);
    }

    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(VILLAGER_LOCATION, i, j, 0, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);
        MerchantOffers merchantoffers = this.menu.getOffers();
        if (!merchantoffers.isEmpty()) {
            int k = this.shopItem;
            if (k < 0 || k >= merchantoffers.size()) {
                return;
            }

            MerchantOffer merchantoffer = merchantoffers.get(k);
            if (merchantoffer.isOutOfStock()) {
                pGuiGraphics.blit(VILLAGER_LOCATION, this.leftPos + 83 + 99, this.topPos + 35, 0, 311.0F, 0.0F, 28, 21, 512, 256);
            }
        }

    }

    private void renderScroller(GuiGraphics pGuiGraphics, int pPosX, int pPosY, MerchantOffers pMerchantOffers) {
        int i = pMerchantOffers.size() + 1 - 7;
        if (pMerchantOffers.size() < 50) {
            if (i > 1) {
                int j = 139 - (27 + (i - 1) * 139 / i);
                int k = 1 + j / i + 139 / i;
                int l = 113;
                int i1 = Math.min(113, this.scrollOff * k);
                if (this.scrollOff == i - 1) {
                    i1 = 113;
                }
                pGuiGraphics.blit(VILLAGER_LOCATION, pPosX + 94, pPosY + 18 + i1, 0, 0.0F, 199.0F, 6, 27, 512, 256);
            }
            else {
                pGuiGraphics.blit(VILLAGER_LOCATION, pPosX + 94, pPosY + 18, 0, 6.0F, 199.0F, 6, 27, 512, 256);
            }
        }
        else {
            if (i > 1) {
                int j = 139 - (27 + (i - 1) * 139 / i);
                int k = 1 + j / i + 139 / i;
                int l = 113;
                int i1 = Math.min(137, this.scrollOff * k);
                if (this.scrollOff == i - 1) {
                    i1 = 137;
                }
                pGuiGraphics.blit(VILLAGER_LOCATION, pPosX + 94, pPosY + 18 + i1, 0, 0.0F, 199.0F, 6, 4, 512, 256);
            }
            else {
                pGuiGraphics.blit(VILLAGER_LOCATION, pPosX + 94, pPosY + 18, 0, 6.0F, 199.0F, 6, 4, 512, 256);
            }
        }

    }

    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        MerchantOffers merchantoffers = this.menu.getOffers();
        if (!merchantoffers.isEmpty()) {
            int i = (this.width - this.imageWidth) / 2;
            int j = (this.height - this.imageHeight) / 2;
            int k = j + 16 + 1;
            int l = i + 5 + 5;
            this.renderScroller(pGuiGraphics, i, j, merchantoffers);
            int i1 = 0;

            for(MerchantOffer merchantoffer : merchantoffers) {
                if (!this.canScroll(merchantoffers.size()) || i1 >= this.scrollOff && i1 < 7 + this.scrollOff) {
                    ItemStack itemstack = merchantoffer.getBaseCostA();
                    ItemStack itemstack1 = merchantoffer.getCostA();
                    ItemStack itemstack2 = merchantoffer.getCostB();
                    ItemStack itemstack3 = merchantoffer.getResult();
                    pGuiGraphics.pose().pushPose();
                    pGuiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
                    int j1 = k + 2;
                    this.renderAndDecorateCostA(pGuiGraphics, itemstack1, itemstack, l, j1);
                    if (itemstack1.is(ItemInit.MONEY.get())) {
                        pGuiGraphics.drawString(this.font, "Buy", l + 20, j1 + 4, 0xFFF0F0);
                    }
                    else {
                        pGuiGraphics.drawString(this.font, "Sell", l + 20, j1 + 4, 0xFFF0F0);
                    }
                    if (!itemstack2.isEmpty()) {
                        pGuiGraphics.renderFakeItem(itemstack2, i + 5 + 35, j1);
                        pGuiGraphics.renderItemDecorations(this.font, itemstack2, i + 5 + 35, j1);
                    }

                    this.renderButtonArrows(pGuiGraphics, merchantoffer, i, j1);
                    pGuiGraphics.renderFakeItem(itemstack3, i + 5 + 68, j1);
                    pGuiGraphics.renderItemDecorations(this.font, itemstack3, i + 5 + 68, j1);
                    pGuiGraphics.pose().popPose();
                    k += 20;
                    ++i1;
                } else {
                    ++i1;
                }
            }

            int k1 = this.shopItem;
            for(MarketplaceScreen.TradeOfferButton merchantscreen$tradeofferbutton : this.tradeOfferButtons) {
                if (merchantscreen$tradeofferbutton.isHoveredOrFocused()) {
                    if (minecraft != null && minecraft.player != null) {
                        merchantscreen$tradeofferbutton.renderToolTip(minecraft.player, pGuiGraphics, pMouseX, pMouseY);
                    }
                }
                merchantscreen$tradeofferbutton.visible = merchantscreen$tradeofferbutton.index < this.menu.getOffers().size();
            }

            RenderSystem.enableDepthTest();
        }

        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    private static final ResourceLocation KILLED_DRAGON = new ResourceLocation("end/kill_dragon");

    private void renderButtonArrows(GuiGraphics pGuiGraphics, MerchantOffer pMerchantOffers, int pPosX, int pPosY) {
        RenderSystem.enableBlend();
        ItemStack costA = pMerchantOffers.getCostA();
        if (costA.is(ItemInit.MONEY.get())) {
            int moneyAmount = MoneyHolderAttacher.getItemStackCapability(costA).map(MoneyHolder::getAmount).orElse(0);
            int playerAmount = 0;

            if (this.minecraft != null && this.minecraft.player != null) {
                playerAmount = BillionaireHolderAttacher.getHolder(this.minecraft.player).map(BillionaireHolder::getMoney).orElse(0);
            }
            if (playerAmount < moneyAmount) {
                pGuiGraphics.blit(VILLAGER_LOCATION, pPosX + 5 + 35 + 20, pPosY + 3, 0, 25.0F, 171.0F, 10, 9, 512, 256);
            }
            else  {
                if (pMerchantOffers.getResult().is(Items.LINGERING_POTION)) {
                    if (minecraft != null && minecraft.getConnection() != null) {
                        var advancements = minecraft.getConnection().getAdvancements().getAdvancements();
                        if (advancements.get(KILLED_DRAGON) != null) {
                            pGuiGraphics.blit(VILLAGER_LOCATION, pPosX + 5 + 35 + 20, pPosY + 3, 0, 15.0F, 171.0F, 10, 9, 512, 256);
                        }
                        else pGuiGraphics.blit(VILLAGER_LOCATION, pPosX + 5 + 35 + 20, pPosY + 3, 0, 25.0F, 171.0F, 10, 9, 512, 256);
                    }
                }
                else pGuiGraphics.blit(VILLAGER_LOCATION, pPosX + 5 + 35 + 20, pPosY + 3, 0, 15.0F, 171.0F, 10, 9, 512, 256);
            }
        }
        else {
            if (this.minecraft != null && this.minecraft.player != null) {
                int i = minecraft.player.getInventory().findSlotMatchingItem(costA);
                if (i != -1) {
                    ItemStack stack = minecraft.player.getInventory().getItem(i);
                    if (stack != ItemStack.EMPTY) {
                        if (stack.getCount() >= costA.getCount()) {
                           pGuiGraphics.blit(VILLAGER_LOCATION, pPosX + 5 + 35 + 20, pPosY + 3, 0, 15.0F, 171.0F, 10, 9, 512, 256);
                        }
                        else pGuiGraphics.blit(VILLAGER_LOCATION, pPosX + 5 + 35 + 20, pPosY + 3, 0, 25.0F, 171.0F, 10, 9, 512, 256);
                    }
                    else pGuiGraphics.blit(VILLAGER_LOCATION, pPosX + 5 + 35 + 20, pPosY + 3, 0, 25.0F, 171.0F, 10, 9, 512, 256);
                }
                else if (this.menu.getCarried().is(costA.getItem()) && this.menu.getCarried().getCount() >= costA.getCount()) {
                    pGuiGraphics.blit(VILLAGER_LOCATION, pPosX + 5 + 35 + 20, pPosY + 3, 0, 15.0F, 171.0F, 10, 9, 512, 256);
                }
                else pGuiGraphics.blit(VILLAGER_LOCATION, pPosX + 5 + 35 + 20, pPosY + 3, 0, 25.0F, 171.0F, 10, 9, 512, 256);
            }
            else pGuiGraphics.blit(VILLAGER_LOCATION, pPosX + 5 + 35 + 20, pPosY + 3, 0, 25.0F, 171.0F, 10, 9, 512, 256);
        }
    }

    private void renderAndDecorateCostA(GuiGraphics pGuiGraphics, ItemStack pRealCost, ItemStack pBaseCost, int pX, int pY) {
        pGuiGraphics.renderFakeItem(pRealCost, pX, pY);
        if (pBaseCost.getCount() == pRealCost.getCount()) {
            pGuiGraphics.renderItemDecorations(this.font, pRealCost, pX, pY);
        }
        else {
            pGuiGraphics.renderItemDecorations(this.font, pBaseCost, pX, pY, pBaseCost.getCount() == 1 ? "1" : null);
            // Forge: fixes Forge-8806, code for count rendering taken from GuiGraphics#renderGuiItemDecorations
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
            String count = pRealCost.getCount() == 1 ? "1" : String.valueOf(pRealCost.getCount());
            font.drawInBatch(count, (float) (pX + 14) + 19 - 2 - font.width(count), (float)pY + 6 + 3, 0xFFFFFF, true, pGuiGraphics.pose().last().pose(), pGuiGraphics.bufferSource(), net.minecraft.client.gui.Font.DisplayMode.NORMAL, 0, 15728880, false);
            pGuiGraphics.pose().popPose();
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0.0F, 0.0F, 300.0F);
            pGuiGraphics.blit(VILLAGER_LOCATION, pX + 7, pY + 12, 0, 0.0F, 176.0F, 9, 2, 512, 256);
            pGuiGraphics.pose().popPose();
        }

    }

    private boolean canScroll(int pNumOffers) {
        return pNumOffers > 7;
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (!Screen.hasShiftDown()) {
            int i = this.menu.getOffers().size();
            if (this.canScroll(i)) {
                int j = i - 7;
                this.scrollOff = Mth.clamp((int) ((double) this.scrollOff - pDelta), 0, j);
            }

            return true;
        }
        for (var button : tradeOfferButtons) {
            button.mouseScrolled(pMouseX, pMouseY, pDelta);
        }
        return false;
    }

    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        int i = this.menu.getOffers().size();
        if (this.isDragging) {
            int j = this.topPos + 18;
            int k = j + 139;
            int l = i - 7;
            float f = ((float)pMouseY - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
            f = f * (float)l + 0.5F;
            this.scrollOff = Mth.clamp((int)f, 0, l);
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        this.isDragging = false;
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (this.canScroll(this.menu.getOffers().size()) && pMouseX > (double)(i + 94) && pMouseX < (double)(i + 94 + 6) && pMouseY > (double)(j + 18) && pMouseY <= (double)(j + 18 + 139 + 1)) {
            this.isDragging = true;
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @OnlyIn(Dist.CLIENT)
    class TradeOfferButton extends Button {
        final int index;

        public TradeOfferButton(int pX, int pY, int pIndex, Button.OnPress pOnPress) {
            super(pX, pY, 88, 20, CommonComponents.EMPTY, pOnPress, DEFAULT_NARRATION);
            this.index = pIndex;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            if (pButton == 1 && this.active && this.visible) {
                if (this.clicked(pMouseX, pMouseY)) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    CustomMerchantOffer offer = MarketplaceScreen.this.menu.getTrader().getOfferAt(this.index + MarketplaceScreen.this.scrollOff);
                    if (offer != null && offer.hasSellOffer()) {
                        ItemStack resultCopy = offer.getResult().copy();
                        ItemStack aCopy = offer.getCostA().copy();
                        MarketplaceScreen.this.menu.getOffers().set(this.index + MarketplaceScreen.this.scrollOff,
                                new CustomMerchantOffer(resultCopy, aCopy, offer.getMaxUses(), offer.getXp(), offer.getPriceMultiplier()).markSellOffer());
                        NetworkHandler.INSTANCE.sendToServer(new UpdateMarketOfferPacket(this.index + MarketplaceScreen.this.scrollOff, false, true));
                    }
                }
            }
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }

        @Override
        public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
            if (this.isMouseOver(pMouseX, pMouseY)) {
                if (Screen.hasShiftDown()) {
                    var customOffer = MarketplaceScreen.this.menu.getTrader().getOfferAt(this.index + MarketplaceScreen.this.scrollOff);
                    if (customOffer != null) {
                        ItemStack stack = customOffer.getNextStack();
                        if (!stack.isEmpty()) {
                            NetworkHandler.INSTANCE.sendToServer(new UpdateMarketOfferPacket(this.index + MarketplaceScreen.this.scrollOff, true, customOffer.hasSellOffer()));
                        }
                    }
                    return true;
                }
            }
            return super.mouseScrolled(pMouseX, pMouseY, pDelta);
        }



        @Override
        protected boolean isValidClickButton(int pButton) {
            return super.isValidClickButton(pButton);
        }

        @Override
        public boolean isFocused() {
            return super.isFocused() && MarketplaceScreen.this.displayConfirm;
        }


        public void renderToolTip(@NotNull LocalPlayer player, GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
            var holder = BillionaireHolderAttacher.getHolderUnwrap(player);
            if (holder != null) {
                if (this.isHovered && MarketplaceScreen.this.menu.getOffers().size() > this.index + MarketplaceScreen.this.scrollOff) {
                    if (pMouseX < this.getX() + 20) {
                        ItemStack itemstack = MarketplaceScreen.this.menu.getOffers().get(this.index + MarketplaceScreen.this.scrollOff).getCostA();
                        if (itemstack.is(ItemInit.MONEY.get())) {
                            var holder1 = MoneyHolderAttacher.getItemStackCapabilityUnwrap(itemstack);
                            if (holder1 != null) {
                                pGuiGraphics.renderTooltip(MarketplaceScreen.this.font, Component.literal("$%s".formatted(holder1.getAmount()))
                                        .withStyle(ChatFormatting.GREEN), pMouseX, pMouseY);
                            }
                        }
                        else {
                            var list = Screen.getTooltipFromItem(MarketplaceScreen.this.minecraft, itemstack);
                            int i = player.getInventory().findSlotMatchingItem(itemstack);
                            if (i != -1) {
                                ItemStack stack = player.getInventory().getItem(i);
                                if (stack != ItemStack.EMPTY) {
                                    if (stack.getCount() >= itemstack.getCount()) {
                                        list.add(Component.literal("Click to Sell").withStyle(ChatFormatting.GREEN));
                                    }
                                    else list.add(Component.literal("Not enough Materials").withStyle(ChatFormatting.RED));
                                }
                                else list.add(Component.literal("Not enough Materials").withStyle(ChatFormatting.RED));
                            }
                            else if (MarketplaceScreen.this.menu.getCarried().is(itemstack.getItem()) && MarketplaceScreen.this.menu.getCarried().getCount() >= itemstack.getCount()) {
                                list.add(Component.literal("Click to Sell").withStyle(ChatFormatting.GREEN));
                            }
                            else list.add(Component.literal("Not enough Materials").withStyle(ChatFormatting.RED));
                            pGuiGraphics.renderTooltip(MarketplaceScreen.this.font, list, itemstack.getTooltipImage(), pMouseX, pMouseY);
                        }
                    }
                    else if (pMouseX < this.getX() + 70 && pMouseX > this.getX() + 20) {
                        ItemStack itemstack2 = MarketplaceScreen.this.menu.getOffers().get(this.index + MarketplaceScreen.this.scrollOff).getCostA();
                        if (itemstack2.is(ItemInit.MONEY.get())) {
                            int amount = BillionaireHolderAttacher.getHolder(player).map(BillionaireHolder::getMoney).orElse(0);
                            int moneyAmount = MoneyHolderAttacher.getItemStackCapability(itemstack2).map(MoneyHolder::getAmount).orElse(0);
                            Component component = Component.literal("");
                            if (MarketplaceScreen.this.menu.getOffers().get(this.index + MarketplaceScreen.this.scrollOff).getResult().is(Items.LINGERING_POTION)) {
                                if (minecraft != null && minecraft.getConnection() != null) {
                                    var advancements = minecraft.getConnection().getAdvancements().getAdvancements();
                                    if (advancements.get(KILLED_DRAGON) == null) {
                                        component = Component.literal("Not Unlocked").withStyle(ChatFormatting.RED);
                                    }
                                    else component = Component.literal("Cost: $%s".formatted(moneyAmount)).withStyle(amount >= moneyAmount ? ChatFormatting.GREEN : ChatFormatting.RED);
                                }
                            }
                            else component = Component.literal("Cost: $%s".formatted(moneyAmount)).withStyle(amount >= moneyAmount ? ChatFormatting.GREEN : ChatFormatting.RED);
                            pGuiGraphics.renderTooltip(MarketplaceScreen.this.font, component, pMouseX, pMouseY);
                        }
                        else {
                            ChatFormatting formatting = ChatFormatting.RED;
                            int i = player.getInventory().findSlotMatchingItem(itemstack2);
                            if (i != -1) {
                                ItemStack stack = player.getInventory().getItem(i);
                                if (stack != ItemStack.EMPTY) {
                                    if (stack.getCount() >= itemstack2.getCount()) {
                                        formatting = ChatFormatting.GREEN;
                                    }
                                }
                            }
                            Component component = Component.literal("Cost: %s ".formatted(itemstack2.getCount())).append(itemstack2.getItem().getName(itemstack2)).withStyle(formatting);
                            pGuiGraphics.renderTooltip(MarketplaceScreen.this.font, component, pMouseX, pMouseY);
                        }
                    }
                    else if (pMouseX > this.getX() + 65) {
                        ItemStack itemstack1 = MarketplaceScreen.this.menu.getOffers().get(this.index + MarketplaceScreen.this.scrollOff).getResult();
                        if (itemstack1.is(ItemInit.MONEY.get())) {
                            var holder1 = MoneyHolderAttacher.getItemStackCapabilityUnwrap(itemstack1);
                            if (holder1 != null) {
                                pGuiGraphics.renderTooltip(MarketplaceScreen.this.font, Component.literal("$%s".formatted(holder1.getAmount()))
                                        .withStyle(ChatFormatting.GREEN), pMouseX, pMouseY);
                            }
                        }
                        else {
                            ItemStack moneyStack = MarketplaceScreen.this.menu.getOffers().get(this.index + MarketplaceScreen.this.scrollOff).getCostA();
                            var list = Screen.getTooltipFromItem(MarketplaceScreen.this.minecraft, itemstack1);
                            int amount = BillionaireHolderAttacher.getHolder(player).map(BillionaireHolder::getMoney).orElse(0);
                            int moneyAmount = MoneyHolderAttacher.getItemStackCapability(moneyStack).map(MoneyHolder::getAmount).orElse(0);
                            if (amount >= moneyAmount) {
                                if (itemstack1.is(Items.LINGERING_POTION)) {
                                    if (minecraft != null && minecraft.getConnection() != null) {
                                        var advancements = minecraft.getConnection().getAdvancements().getAdvancements();
                                        if (advancements.get(KILLED_DRAGON) == null) {
                                            list.add(Component.literal("Not Unlocked").withStyle(ChatFormatting.RED));
                                        }
                                        else list.add(Component.literal("Click to Purchase").withStyle(ChatFormatting.GREEN));
                                    }
                                }
                                else list.add(Component.literal("Click to Purchase").withStyle(ChatFormatting.GREEN));
                            }
                            else {
                                list.add(Component.literal("Not Enough Money").withStyle(ChatFormatting.RED));
                            }
                            pGuiGraphics.renderTooltip(MarketplaceScreen.this.font, list, itemstack1.getTooltipImage(), pMouseX, pMouseY);
                        }
                    }
                }
            }

        }
    }
}
