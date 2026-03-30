package com.lumi.habd.mixin;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class SuffocationMixin {
    //Does nothing as of now, need to overwrite in water too
    //Pushing to GitHub first though
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
}