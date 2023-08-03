package com.divinity.hmedia.rgrbillionaire.event;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.client.renderer.ShootableCoinRenderer;
import com.divinity.hmedia.rgrbillionaire.init.EntityInit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RGRBillionaire.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.PENNY_ENTITY.get(), ctx -> new ShootableCoinRenderer(ctx, "penny_entity"));
        event.registerEntityRenderer(EntityInit.QUARTER_ENTITY.get(), ctx -> new ShootableCoinRenderer(ctx, "quarter_entity"));
        event.registerEntityRenderer(EntityInit.SILVER_DOLLAR_ENTITY.get(), ctx -> new ShootableCoinRenderer(ctx, "silver_dollar_entity"));
    }
}
