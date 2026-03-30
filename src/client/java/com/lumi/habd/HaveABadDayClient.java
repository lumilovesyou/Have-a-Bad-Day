package com.lumi.habd;

import com.lumi.habd.custom.Advancements.Criterion.ModCriteria;
import com.lumi.habd.custom.Blinking.BlinkRefreshPacket;
import com.lumi.habd.custom.Blinking.BlinkTickPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import static com.lumi.habd.HaveABadDay.MODID;
import static com.lumi.habd.custom.Blinking.TICKS;

public class HaveABadDayClient implements ClientModInitializer {
    public static int blinkCooldown = 0;

    KeyMapping.Category Keybinds = new KeyMapping.Category(
        Identifier.fromNamespaceAndPath(MODID, "keybinds")
    );

    KeyMapping BlinkKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        String.format("key.%s.spook", MODID),
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_B,
        Keybinds
    ));

	@Override
	public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(BlinkTickPacket.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                LocalPlayer player = context.client().player;
                player.setAttached(TICKS, payload.tick());
            });
        });

        ModCriteria.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (blinkCooldown != 0) blinkCooldown--;
            if(BlinkKey.consumeClick() && client.player != null && blinkCooldown == 0) {
                ClientPlayNetworking.send(new BlinkRefreshPacket());
                client.player.setAttached(TICKS, 0);
                blinkCooldown = 2;
            }
        });
	}
}