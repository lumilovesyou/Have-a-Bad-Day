package com.lumi.habd;

import com.lumi.habd.custom.Advancements.Criterion.ModCriteria;
import com.lumi.habd.custom.Blinking.BlinkRefreshPacket;
import com.lumi.habd.custom.Blinking.BlinkTickPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.lumi.habd.custom.Blinking.EyesDriedDamageSource;
import static com.lumi.habd.custom.Blinking.TICKS;

public class HaveABadDay implements ModInitializer {
	public static final String MODID = "have-a-bad-day";
    public static int ticksToBlink = 60;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
        LOGGER.info("Lumi says \"Have a bad day!\"");

        ////Packet sending registration
        PayloadTypeRegistry.clientboundPlay().register(
                BlinkTickPacket.TYPE,
                BlinkTickPacket.CODEC
        );

        PayloadTypeRegistry.serverboundPlay().register(
                BlinkRefreshPacket.TYPE,
                BlinkRefreshPacket.CODEC
        );
        ////

        //Packet receiving registration
        ServerPlayNetworking.registerGlobalReceiver(BlinkRefreshPacket.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            player.setAttached(TICKS, 0);
            ModCriteria.BLINK.trigger(player);
        });

        //Blinking ticks and damage
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 10 != 0) return;

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                int currentTick = player.getAttachedOrElse(TICKS, 0);
                if (currentTick < ticksToBlink) {
                    player.setAttached(TICKS, currentTick + 1);
                    ServerPlayNetworking.send(player, new BlinkTickPacket(currentTick + 1));
                } else {
                    if (server.getTickCount() % 20 != 0) return;
                    DamageSource EyesDriedDamage = new DamageSource(
                        server.registryAccess()
                            .lookupOrThrow(Registries.DAMAGE_TYPE)
                            .get(EyesDriedDamageSource.identifier()).get()
                    );
                    player.hurt(
                            EyesDriedDamage,
                            2.0f
                    );
                }
            }
        });
    }
}