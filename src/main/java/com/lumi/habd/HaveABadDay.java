package com.lumi.habd;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.lumi.habd.custom.BlinkingResources.TICKS;
import static com.lumi.habd.custom.BreathingResources.BreathRefreshPacket;
import static com.lumi.habd.custom.BlinkingResources.EyesDriedDamageSource;
import com.lumi.habd.custom.BlinkingResources.BlinkTickPacket;
import com.lumi.habd.custom.Advancements.Criterion.ModCriteria;
import com.lumi.habd.custom.BlinkingResources.BlinkRefreshPacket;

public class HaveABadDay implements ModInitializer {
	public static final String MODID = "have-a-bad-day";
    public static int ticksToBlink = 60;

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

        PayloadTypeRegistry.serverboundPlay().register(
                BreathRefreshPacket.TYPE,
                BreathRefreshPacket.CODEC
        );
        ////

        ////Packet receiving registration
        //Remember to add sound effects for blinking + breathing ~~~~~~~~~~~~~~
        ServerPlayNetworking.registerGlobalReceiver(BlinkRefreshPacket.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            player.setAttached(TICKS, 0);
            ModCriteria.BLINK.trigger(player);
        });

        ServerPlayNetworking.registerGlobalReceiver(BreathRefreshPacket.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            if (player.isEyeInFluid(FluidTags.WATER) && !player.level().getBlockState(BlockPos.containing(player.getX(), player.getEyeY(), player.getZ())).is(Blocks.BUBBLE_COLUMN)) {
                player.setAirSupply(player.getAirSupply() - 4);
            } else if ((player.isEyeInFluid(FluidTags.LAVA) || player.isOnFire()) && !player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                player.setAirSupply(player.getAirSupply() - 4);
                player.hurt(
                        player.damageSources().onFire(),
                        4.0f
                );
                //Maybe custom advancement for breathing lava? ~~~~~~~~~~~~~~
            } else {
                player.setAirSupply(player.getMaxAirSupply() - 1);
            }
            ModCriteria.BREATHE.trigger(player);
        });
        ////

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