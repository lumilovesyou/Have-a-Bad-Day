package com.lumi.habd.mixin.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.lumi.habd.HaveABadDay.MODID;
import static com.lumi.habd.HaveABadDay.ticksToBlink;
import static com.lumi.habd.HaveABadDayClient.blinkCooldown;
import static com.lumi.habd.custom.BlinkingResources.TICKS;

@Mixin(Gui.class)
public abstract class CameraOverlaysMixin {
    //To-do: replace current texture with a one like the freezing texture but with blood vessels instead ~~~~~~~~~~~~~~

    @Shadow
    abstract void extractTextureOverlay(final GuiGraphicsExtractor graphics, final Identifier texture, final float alpha);

    @Shadow
    private Minecraft minecraft;

    @Inject(
            method = "extractCameraOverlays",
            at = @At("TAIL")
    )
    private void eyeOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (minecraft.player.getAttachedOrElse(TICKS, 0) != 0) extractTextureOverlay(graphics, Identifier.fromNamespaceAndPath(MODID, "textures/misc/eye_overlay.png"), (float)Math.min(minecraft.player.getAttachedOrElse(TICKS, 0), ticksToBlink) / ticksToBlink);
        if (blinkCooldown != 0) extractTextureOverlay(graphics, Identifier.fromNamespaceAndPath(MODID, "textures/misc/blink_overlay.png"), 1.0F);
    }
}