package com.divinity.hmedia.rgrbillionaire.cap;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class GlobalLevelHolderAttacher extends CapabilityAttacher {

    private static final Class<GlobalLevelHolder> CAPABILITY_CLASS = GlobalLevelHolder.class;
    public static final Capability<GlobalLevelHolder> EXAMPLE_GLOBAL_LEVEL_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation EXAMPLE_GLOBAL_LEVEL_CAPABILITY_RL = new ResourceLocation(RGRBillionaire.MODID, "butler_global_level_capability");

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static GlobalLevelHolder getGlobalLevelCapabilityUnwrap(Level level) {
        return getGlobalLevelCapability(level).orElse(null);
    }

    public static LazyOptional<GlobalLevelHolder> getGlobalLevelCapability(Level level) {
        return getGlobalLevelCapability(level, EXAMPLE_GLOBAL_LEVEL_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Level> event, Level level) {
        genericAttachCapability(event, new GlobalLevelHolder(level), EXAMPLE_GLOBAL_LEVEL_CAPABILITY, EXAMPLE_GLOBAL_LEVEL_CAPABILITY_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerGlobalLevelAttacher(GlobalLevelHolderAttacher::attach, GlobalLevelHolderAttacher::getGlobalLevelCapability);
    }
}
