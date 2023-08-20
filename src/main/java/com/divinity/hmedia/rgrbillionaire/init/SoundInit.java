package com.divinity.hmedia.rgrbillionaire.init;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundInit {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RGRBillionaire.MODID);

    public static final RegistryObject<SoundEvent> BUTLER_IDLE = sound("butler_idle");
    public static final RegistryObject<SoundEvent> COIN_CANNON_SHOOT = sound("coin_cannon_shoot");
    public static final RegistryObject<SoundEvent> GRAND_GIVEAWAY = sound("grand_giveaway");
    public static final RegistryObject<SoundEvent> ROCKET_READY = sound("rocket_ready");

    private static RegistryObject<SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(RGRBillionaire.MODID, name)));
    }
}
