package com.lumi.habd.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import static com.lumi.habd.HaveABadDay.REFRESHED_EFFECT;
import static com.lumi.habd.resources.BlinkingResources.BLINK_TICKS;

public class DropperItem extends Item {
    public DropperItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        //Add sound effect! ~~~~~~~~~~~~~~
        if (player.getXRot() < -40) {
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
        EquipmentSlot slot = entity.getItemBySlot(EquipmentSlot.MAINHAND) == stack ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        stack.hurtAndConvertOnBreak(1, Items.GLASS_BOTTLE, entity, slot);
        return stack;
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
