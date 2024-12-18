package com.divinity.hmedia.rgrbillionaire.init;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import dev._100media.hundredmediaabilities.HundredMediaAbilitiesMod;
import dev._100media.hundredmediaabilities.marker.Marker;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MarkerInit {
    public static final DeferredRegister<Marker> MARKERS = DeferredRegister.create(new ResourceLocation(HundredMediaAbilitiesMod.MODID, "markers"), RGRBillionaire.MODID);

    public static final RegistryObject<Marker> NO_ADDED_PRODUCTION_RATE = MARKERS.register("no_added_production_rate", Marker::new);
    public static final RegistryObject<Marker> NO_BASE_PRODUCTION_RATE = MARKERS.register("no_base_production_rate", Marker::new);
    public static final RegistryObject<Marker> TAX_FORM = MARKERS.register("tax_form", Marker::new);
    public static final RegistryObject<Marker> BILLIONAIRES_CLUB = MARKERS.register("billionaires_club", Marker::new);

    public static final RegistryObject<Marker> MUG_OF_COFFEE = MARKERS.register("mug_of_coffee", Marker::new);
}
