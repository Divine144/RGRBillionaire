package com.divinity.hmedia.rgrbillionaire.init;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.ability.DoubleJumpAbility;
import com.divinity.hmedia.rgrbillionaire.ability.ExploitWorkingClassAbility;
import com.divinity.hmedia.rgrbillionaire.ability.GoldenJetpackAbility;
import com.divinity.hmedia.rgrbillionaire.ability.GrandGiveawayAbility;
import dev._100media.hundredmediaabilities.HundredMediaAbilitiesMod;
import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AbilityInit {
    public static final DeferredRegister<Ability> ABILITIES = DeferredRegister.create(new ResourceLocation(HundredMediaAbilitiesMod.MODID, "abilities"), RGRBillionaire.MODID);

    public static final RegistryObject<Ability> EXPLOIT_WORKING_CLASS_ABILITY = ABILITIES.register("exploit_working_class", ExploitWorkingClassAbility::new);
    public static final RegistryObject<Ability> GRAND_GIVEAWAY_ABILITY = ABILITIES.register("the_grand_giveaway", GrandGiveawayAbility::new);
    public static final RegistryObject<Ability> GOLDEN_JETPACK = ABILITIES.register("golden_jetpack", GoldenJetpackAbility::new);
    public static final RegistryObject<Ability> DOUBLE_JUMP = ABILITIES.register("double_jump", DoubleJumpAbility::new);

}
