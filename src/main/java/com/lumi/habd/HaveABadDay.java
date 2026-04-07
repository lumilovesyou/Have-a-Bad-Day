package com.lumi.habd;

import com.lumi.habd.effects.RefreshedEffect;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.lumi.habd.items.ItemRegistrar.*;
import static com.lumi.habd.resources.BlinkingResources.*;
import static com.lumi.habd.resources.BreathingResources.BreathRefreshPacket;

import com.lumi.habd.resources.BlinkingResources.BlinkTickPacket;
import com.lumi.habd.advancements.Criterion.ModCriteria;
import com.lumi.habd.resources.BlinkingResources.BlinkRefreshPacket;

public class HaveABadDay implements ModInitializer {
    ////To-do:
    //Make eye drops take any dye (and be coloured appropriately)
    //Add breathing sound effect
    //Find a way to generate default resource packs
        //Add 16x16 pixel art for dropper resource pack
        //Add female alt breathings sounds resource pack
            //Add support for female gender mod to add breathing sounds as appropriate
    //(Maybe) Add support for entity texture features blinking
    ////

	public static final String MODID = "have-a-bad-day";
    public static int MAX_BLINK_TICKS = 60;

	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    //Effect registration
    public static final Holder<MobEffect> REFRESHED_EFFECT =
            Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(MODID, "refreshed"), new RefreshedEffect());

	@Override
	public void onInitialize() {
        LOGGER.info("Lumi says \"Have a bad day!\"");

        //Item registration
        initItems();

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
            if (player.getAttachedOrElse(BLINK_TICKS, 0) > 0) {
                player.setAttached(BLINK_TICKS, 0);
            }
            ModCriteria.BLINK.trigger(player);
        });

        ServerPlayNetworking.registerGlobalReceiver(BreathRefreshPacket.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            if (player.isEyeInFluid(FluidTags.WATER) && !(player.level().getBlockState(BlockPos.containing(player.getX(), player.getEyeY(), player.getZ())).is(Blocks.BUBBLE_COLUMN) || player.hasEffect(MobEffects.WATER_BREATHING))) {
                player.setAirSupply(player.getAirSupply() - (player.getMaxAirSupply() / 5));
            } else if (((player.isEyeInFluid(FluidTags.LAVA) || player.isOnFire())) && !player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                player.setAirSupply(player.getAirSupply() - (player.getMaxAirSupply() / 5));
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
                //Skip if creative
                if (player.gameMode.isCreative() || player.hasEffect(REFRESHED_EFFECT)) continue;

                int currentEyeTicks = player.getAttachedOrElse(BLINK_TICKS, 0);

                if (currentEyeTicks < MAX_BLINK_TICKS) {
                    //Makes blinking required more often if you're on fire or in the nether
                    int newValue = currentEyeTicks + ((player.isOnFire()) ? 3 : ((player.level().dimension() == Level.NETHER) ? 2 : 1));
                    sendBlinkPacket(player, newValue);
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

    private void sendBlinkPacket(ServerPlayer player, int value) {
        if (player.getAttachedOrElse(BLINK_TICKS, 0) != value) {
            player.setAttached(BLINK_TICKS, value);
            ServerPlayNetworking.send(player, new BlinkTickPacket(value));
        }
    }
}