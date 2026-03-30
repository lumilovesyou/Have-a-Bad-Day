package com.lumi.habd.mixin;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class SuffocationMixin {
    @Redirect(
        method = "baseTick()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;canBreatheUnderwater()Z"
        )
    )
    public boolean canBreatheUnderwater(LivingEntity entity) {
        if (entity.is(EntityType.PLAYER)) {
            return false;
        }
        return entity.is(EntityTypeTags.CAN_BREATHE_UNDER_WATER);
    }

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