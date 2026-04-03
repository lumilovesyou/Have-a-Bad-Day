package com.lumi.habd.mixins;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class SuffocationMixin {
    //Stop oxygen from filling on it's own in a bubble column
    @Redirect(
        method = "baseTick()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"
        )
    )
    public boolean isBubbleColumn(BlockState blockstate, Object object) {
        return false;
    }

    //Make the player always losing oxygen
    @Redirect(
        method = "baseTick()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"
        )
    )
    public boolean isEyeInFluid(LivingEntity entity, final TagKey<Fluid> type) {
        if (entity.is(EntityType.PLAYER)) {
            return true;
        }
        return entity.isEyeInFluid(type);
    }
}