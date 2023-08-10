package com.divinity.hmedia.rgrbillionaire.item;

import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.init.AbilityInit;
import com.divinity.hmedia.rgrbillionaire.init.SkillInit;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MugOfCoffeeItem extends Item {

    public MugOfCoffeeItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pFood, Level pLevel, LivingEntity pLivingEntity) {
        if (pLivingEntity instanceof Player player) {
            player.eat(pLevel, pFood.copy());
            if (!player.level().isClientSide) {
                BillionaireHolderAttacher.getHolder(player).ifPresent(h -> h.setMugEatTicks(30 * 20));
                SkillInit.unlockAbility(player, AbilityInit.DOUBLE_JUMP.get());
            }
        }
        return pFood;
    }
}
