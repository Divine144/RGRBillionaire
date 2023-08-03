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

public class CannonHolderAttacher  extends CapabilityAttacher {

    private static final Class<CannonHolder> CAPABILITY_CLASS = CannonHolder.class;
    public static final Capability<CannonHolder> EXAMPLE_ITEM_STACK_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation EXAMPLE_ITEM_STACK_CAPABILITY_RL = new ResourceLocation(RGRBillionaire.MODID, "cannon_holder_attacher_capability");

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static CannonHolder getItemStackCapabilityUnwrap(ItemStack itemStack) {
        return getItemStackCapability(itemStack).orElse(null);
    }

    public static LazyOptional<CannonHolder> getItemStackCapability(ItemStack itemStack) {
        return itemStack.getCapability(EXAMPLE_ITEM_STACK_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<ItemStack> event, ItemStack itemStack) {
        if (itemStack.is(ItemInit.COIN_CANNON.get())) {
            genericAttachCapability(event, new CannonHolder(itemStack), EXAMPLE_ITEM_STACK_CAPABILITY, EXAMPLE_ITEM_STACK_CAPABILITY_RL);
        }
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerItemStackAttacher(CannonHolderAttacher::attach, CannonHolderAttacher::getItemStackCapability);
    }
}
