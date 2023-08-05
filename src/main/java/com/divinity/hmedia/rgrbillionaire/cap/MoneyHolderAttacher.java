package com.divinity.hmedia.rgrbillionaire.cap;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class MoneyHolderAttacher extends CapabilityAttacher {
    private static final Class<MoneyHolder> CAPABILITY_CLASS = MoneyHolder.class;
    public static final Capability<MoneyHolder> EXAMPLE_ITEM_STACK_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation EXAMPLE_ITEM_STACK_CAPABILITY_RL = new ResourceLocation(RGRBillionaire.MODID, "money_holder_attacher_capability");

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static MoneyHolder getItemStackCapabilityUnwrap(ItemStack itemStack) {
        return getItemStackCapability(itemStack).orElse(null);
    }

    public static LazyOptional<MoneyHolder> getItemStackCapability(ItemStack itemStack) {
        return itemStack.getCapability(EXAMPLE_ITEM_STACK_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<ItemStack> event, ItemStack itemStack) {
        if (itemStack.is(ItemInit.MONEY.get())) {
            genericAttachCapability(event, new MoneyHolder(itemStack), EXAMPLE_ITEM_STACK_CAPABILITY, EXAMPLE_ITEM_STACK_CAPABILITY_RL);
        }
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerItemStackAttacher(MoneyHolderAttacher::attach, MoneyHolderAttacher::getItemStackCapability);
    }
}
