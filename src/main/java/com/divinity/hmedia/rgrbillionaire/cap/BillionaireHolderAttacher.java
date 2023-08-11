package com.divinity.hmedia.rgrbillionaire.cap;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RGRBillionaire.MODID)
public class BillionaireHolderAttacher extends CapabilityAttacher {
    public static final Capability<BillionaireHolder> EXAMPLE_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation EXAMPLE_RL = new ResourceLocation(RGRBillionaire.MODID, "example");
    private static final Class<BillionaireHolder> CAPABILITY_CLASS = BillionaireHolder.class;

    @SuppressWarnings("ConstantConditions")
    public static BillionaireHolder getHolderUnwrap(Entity player) {
        return getHolder(player).orElse(null);
    }

    public static LazyOptional<BillionaireHolder> getHolder(Entity player) {
        return player.getCapability(EXAMPLE_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Entity> event, Entity entity) {
        genericAttachCapability(event, new BillionaireHolder(entity), EXAMPLE_CAPABILITY, EXAMPLE_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerPlayerAttacher(BillionaireHolderAttacher::attach, BillionaireHolderAttacher::getHolder, true);
    }
}
