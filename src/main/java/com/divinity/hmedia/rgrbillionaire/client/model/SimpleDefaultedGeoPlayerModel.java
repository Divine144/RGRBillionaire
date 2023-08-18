package com.divinity.hmedia.rgrbillionaire.client.model;

import dev._100media.hundredmediageckolib.client.model.SimpleGeoHumanoidModel;
import dev._100media.hundredmediageckolib.client.model.SimpleGeoPlayerModel;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

public class SimpleDefaultedGeoPlayerModel <T extends GeoAnimatable> extends SimpleGeoPlayerModel<T> {
    private static final SimpleGeoHumanoidModel.Properties<?> defaultedProperties = new SimpleGeoHumanoidModel.Properties<>()
            .headBone("head")
            .bodyBone("body")
            .armBones("right_arm", "left_arm")
            .legABones("left_leg")
            .legBBones("right_leg");

    public SimpleDefaultedGeoPlayerModel(String namespace, String name) {
        super(namespace, name, defaultedProperties);
    }

    public SimpleDefaultedGeoPlayerModel(ResourceLocation assetPath) {
        super(assetPath, defaultedProperties);
    }
}
