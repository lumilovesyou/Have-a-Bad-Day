package com.lumi.habd.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

import static com.lumi.habd.resources.BlinkingResources.BLINK_TICKS;
import static com.lumi.habd.resources.BlinkingResources.DROPPER_TICKS;

public class DropperItem extends Item {
    public DropperItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        //Add sound effect! ~~~~~~~~~~~~~~
        if (player.getXRot() < -20) {
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
        entity.setAttached(DROPPER_TICKS, 360);
        stack.consume(1, entity);
        return stack;
    }

    @Override
    public ItemUseAnimation getUseAnimation(final ItemStack itemStack) {
        return ItemUseAnimation.BOW;
    }
}
