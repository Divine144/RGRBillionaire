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
    public static final RegistryObject<SoundEvent> MARKET_CRASHER = sound("market_crasher");
    public static final RegistryObject<SoundEvent> GRAND_GIVEAWAY = sound("grand_giveaway");
    public static final RegistryObject<SoundEvent> ROCKET_READY = sound("rocket_ready");
    public static final RegistryObject<SoundEvent> BILLIONAIRE_CLUB = sound("billionaires_club");
    public static final RegistryObject<SoundEvent> BOING = sound("boing");
    public static final RegistryObject<SoundEvent> RICH_LOW = sound("richlow");
    public static final RegistryObject<SoundEvent> RICH_LITTLE_LOW = sound("richlittlelow");
    public static final RegistryObject<SoundEvent> RICH_NORMAL = sound("richnormal");
    public static final RegistryObject<SoundEvent> RICH_HIGH = sound("richhigh");

    public static final RegistryObject<SoundEvent> JETPACK = sound("jetpack");


    private static RegistryObject<SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(RGRBillionaire.MODID, name)));
    }
}
