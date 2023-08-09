package com.divinity.hmedia.rgrbillionaire.client.renderer;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.item.GoldenJetpackArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GoldenJetpackArmorRenderer extends GeoArmorRenderer<GoldenJetpackArmorItem> {

    public GoldenJetpackArmorRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(RGRBillionaire.MODID, "armor/golden_jetpack")));
    }
}
