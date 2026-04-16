package com.lumi.habd.items;

import com.lumi.habd.advancements.Criterion.ModCriteria;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import static com.lumi.habd.HaveABadDay.REFRESHED_EFFECT;

public class DropperItem extends Item {
    public DropperItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.getXRot() < -70) {
            player.startUsingItem(hand);
        } else {
            if (player instanceof ServerPlayer serverPlayer) {
                //ID four for eye drops
                ModCriteria.ID_TRIGGER.trigger(serverPlayer, 4);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(final ItemStack itemStack, final LivingEntity user) {
        return 80;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(REFRESHED_EFFECT, 3600, 0, false, true, true));
        //Play drip sound
        entity.level().playSound(
                null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON,
                entity.getSoundSource(),
                1.0f,
                1.0f
        );
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
