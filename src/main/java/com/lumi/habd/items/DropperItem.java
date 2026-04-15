package com.lumi.habd.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import static com.lumi.habd.HaveABadDay.REFRESHED_EFFECT;
import static com.lumi.habd.resources.BlinkingResources.BLINK_TICKS;
import static com.lumi.habd.sounds.SoundRegistrar.FEMALE_BREATHE;

public class DropperItem extends Item {
    public DropperItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        //Add sound effect! ~~~~~~~~~~~~~~
        if (player.getXRot() < -70) {
            player.startUsingItem(hand);
        } else {
            //Trigger advancement to inform the player to look upwards
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(final ItemStack itemStack, final LivingEntity user) {
        return 80;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        entity.setAttached(BLINK_TICKS, 0);
        entity.addEffect(new MobEffectInstance(REFRESHED_EFFECT, 3600, 0, false, true, true));
        return stack.hurtAndConvertOnBreak(1, Items.GLASS_BOTTLE, entity, entity.getUsedItemHand().asEquipmentSlot());
    }

    @Override
    public ItemUseAnimation getUseAnimation(final ItemStack itemStack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public int getDefaultMaxStackSize() {
        return 1;
    }
}
