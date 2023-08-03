package com.divinity.hmedia.rgrbillionaire.mixin;

import com.divinity.hmedia.rgrbillionaire.client.CustomRenderTypes;
import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBuffers.class)
public class RenderBuffersMixin {

    @Inject(method = "put", at = @At("HEAD"))
    private static void put(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> pMapBuilders, RenderType pRenderType, CallbackInfo ci) {
        if (!pMapBuilders.containsKey(CustomRenderTypes.ENERGY_SWIRL_ITEM)) {
            pMapBuilders.put(CustomRenderTypes.ENERGY_SWIRL_ITEM, new BufferBuilder(pRenderType.bufferSize())); // Game will crash without this
        }
    }
}
