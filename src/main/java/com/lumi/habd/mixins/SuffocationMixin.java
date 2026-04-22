package com.lumi.habd.mixins;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.lumi.habd.HaveABadDay.REQUIRE_BREATHING;

@Mixin(LivingEntity.class)
public abstract class SuffocationMixin extends Entity {
    public SuffocationMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    //Stops oxygen from filling on its own in a bubble column
    @Redirect(
        method = "baseTick()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"
        )
    )
    public boolean isBubbleColumn(BlockState blockstate, Object object) {
        if (level().getServer().getGameRules().get(REQUIRE_BREATHING)) {
            return false;
        }
        return blockstate.is(Blocks.BUBBLE_COLUMN);
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
        if (entity.is(EntityType.PLAYER) && level().getServer().getGameRules().get(REQUIRE_BREATHING)) {
            return true;
        }
        return entity.isEyeInFluid(type);
    }
}