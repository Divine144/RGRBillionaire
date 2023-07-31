package com.divinity.hmedia.rgrbillionaire.init;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import dev._100media.hundredmediamorphs.HundredMediaMorphsMod;
import dev._100media.hundredmediamorphs.morph.Morph;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;

public class MorphInit {
    public static final DeferredRegister<Morph> MORPHS = DeferredRegister.create(new ResourceLocation(HundredMediaMorphsMod.MODID, "morphs"), RGRBillionaire.MODID);

    // TODO: Specify money caps and effects after each morph is obtained here

}
