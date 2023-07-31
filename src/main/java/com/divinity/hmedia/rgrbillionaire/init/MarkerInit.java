package com.divinity.hmedia.rgrbillionaire.init;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import dev._100media.hundredmediaabilities.HundredMediaAbilitiesMod;
import dev._100media.hundredmediaabilities.marker.Marker;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MarkerInit {
    public static final DeferredRegister<Marker> MARKERS = DeferredRegister.create(new ResourceLocation(HundredMediaAbilitiesMod.MODID, "markers"), RGRBillionaire.MODID);

    public static final RegistryObject<Marker> JETPACK_FLIGHT = MARKERS.register("jetpack_flight", Marker::new);
}
