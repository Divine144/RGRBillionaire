package com.divinity.hmedia.rgrbillionaire.init;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.menu.ButlerInventoryMenu;
import com.divinity.hmedia.rgrbillionaire.menu.MarketplaceMenu;
import com.divinity.hmedia.rgrbillionaire.menu.MinebookMenu;
import com.divinity.hmedia.rgrbillionaire.menu.TaxForumMenu;
import dev._100media.hundredmediaquests.menu.AlwaysValidMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuInit {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, RGRBillionaire.MODID);

    public static final RegistryObject<MenuType<?>> SKILL_TREE = MENUS.register("skill_tree", () -> IForgeMenuType.create(new IContainerFactory<>() {
        @Override
        public AbstractContainerMenu create(int windowId, Inventory inv, FriendlyByteBuf data) {
            return new AlwaysValidMenu(SKILL_TREE.get(), windowId);
        }
    }));
    public static final RegistryObject<MenuType<?>> EVOLUTION_TREE = MENUS.register("evolution_tree", () -> IForgeMenuType.create(new IContainerFactory<>() {
        @Override
        public AbstractContainerMenu create(int windowId, Inventory inv, FriendlyByteBuf data) {
            return new AlwaysValidMenu(EVOLUTION_TREE.get(), windowId);
        }
    }));
    public static final RegistryObject<MenuType<?>> COMBAT_TREE = MENUS.register("combat_tree", () -> IForgeMenuType.create(new IContainerFactory<>() {
        @Override
        public AbstractContainerMenu create(int windowId, Inventory inv, FriendlyByteBuf data) {
            return new AlwaysValidMenu(COMBAT_TREE.get(), windowId);
        }
    }));
    public static final RegistryObject<MenuType<?>> UTILITY_TREE = MENUS.register("utility_tree", () -> IForgeMenuType.create(new IContainerFactory<>() {
        @Override
        public AbstractContainerMenu create(int windowId, Inventory inv, FriendlyByteBuf data) {
            return new AlwaysValidMenu(UTILITY_TREE.get(), windowId);
        }
    }));
    public static final RegistryObject<MenuType<MinebookMenu>> MINEBOOK_SCREEN = MENUS.register("minebook_menu", () -> IForgeMenuType.create(new IContainerFactory<>() {
        @Override
        public MinebookMenu create(int windowId, Inventory inv, FriendlyByteBuf data) {
            return new MinebookMenu(MINEBOOK_SCREEN.get(), windowId);
        }
    }));

    public static final RegistryObject<MenuType<TaxForumMenu>> TAX_FORUM_SCREEN = MENUS.register("tax_forum_menu", () -> IForgeMenuType.create(new IContainerFactory<>() {
        @Override
        public TaxForumMenu create(int windowId, Inventory inv, FriendlyByteBuf data) {
            return new TaxForumMenu(TAX_FORUM_SCREEN.get(), windowId);
        }
    }));
    public static final RegistryObject<MenuType<ButlerInventoryMenu>> BUTLER_MENU = MENUS.register("butler_menu", () -> IForgeMenuType.create(ButlerInventoryMenu::new));

    public static final RegistryObject<MenuType<MarketplaceMenu>> MARKET_MENU = MENUS.register("market_menu", () -> IForgeMenuType.create(MarketplaceMenu::new));

}
