package com.divinity.hmedia.rgrbillionaire.init;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.item.CoinCannonItem;
import com.divinity.hmedia.rgrbillionaire.item.MarketCrasherItem;
import com.divinity.hmedia.rgrbillionaire.item.MoneyItem;
import com.divinity.hmedia.rgrbillionaire.item.SwordOfTruthItem;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.animated.SimpleAnimatedItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RGRBillionaire.MODID);

    public static final RegistryObject<Item> COIN_CANNON = ITEMS.register("the_coin_cannon", () -> new CoinCannonItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MARKET_CRASHER = ITEMS.register("market_crasher", () -> new MarketCrasherItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ROCKET_TO_MARS = ITEMS.register("rocket_to_mars", () -> new SimpleAnimatedItem(new AnimatedItemProperties().stacksTo(1)));
    public static final RegistryObject<Item> SWORD_OF_TRUTH = ITEMS.register("tax_audit_sword_of_truth", () -> new SwordOfTruthItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MARKETPLACE = ITEMS.register("starlinked_minebook_marketplace", () -> new SimpleAnimatedItem(new AnimatedItemProperties().stacksTo(1)));
    public static final RegistryObject<Item> MONEY = ITEMS.register("money", () -> new MoneyItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HEART = ITEMS.register("heart", () -> new Item(new Item.Properties().stacksTo(1)));

    public static Item.Properties getItemProperties() {
        return new Item.Properties();
    }
}
