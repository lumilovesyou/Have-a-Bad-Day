package com.lumi.habd;

import com.lumi.habd.advancements.Criterion.ModCriteria;
import com.lumi.habd.effects.RefreshedEffect;
import com.lumi.habd.resources.BlinkingResources.BlinkRefreshPacket;
import com.lumi.habd.resources.BlinkingResources.BlinkTickPacket;
import com.wildfire.api.WildfireAPI;
import com.wildfire.main.config.enums.Gender;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.fabric.impl.resource.ResourceLoaderImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.lumi.habd.items.ItemRegistrar.initItems;
import static com.lumi.habd.resources.BlinkingResources.BLINK_TICKS;
import static com.lumi.habd.resources.BlinkingResources.EyesDriedDamageSource;
import static com.lumi.habd.resources.BreathingResources.BreathRefreshPacket;
import static com.lumi.habd.sounds.SoundRegistrar.*;

public class HaveABadDay implements ModInitializer {
	public static final String MODID = "have-a-bad-day";
    public static int MAX_BLINK_TICKS = 60;

	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    //Effect registration
    public static final Holder<MobEffect> REFRESHED_EFFECT =
        Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(MODID, "refreshed"), new RefreshedEffect());

    //Game rules
    public static final GameRule<Boolean> REQUIRE_BREATHING = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(Identifier.fromNamespaceAndPath(MODID, "require_breathing"));
    public static final GameRule<Boolean> REQUIRE_BLINKING = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(Identifier.fromNamespaceAndPath(MODID, "require_blinking"));

	@Override
	public void onInitialize() {
        LOGGER.info("Lumi says \"Have a bad day!\"");

        //Sound registration
        initSounds();

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
        ServerPlayNetworking.registerGlobalReceiver(BlinkRefreshPacket.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            if (player.getAttachedOrElse(BLINK_TICKS, 0) > 0) {
                player.setAttached(BLINK_TICKS, 0);
            }
            //ID one for blink advancement
            ModCriteria.ID_TRIGGER.trigger(player, 1);
        });

        ServerPlayNetworking.registerGlobalReceiver(BreathRefreshPacket.TYPE, (payload, context) -> {
            //Skip if gamerule
            if (!context.server().getGameRules().get(REQUIRE_BREATHING)) return;

            ServerPlayer player = context.player();
            if (player.isEyeInFluid(FluidTags.WATER) && !(player.level().getBlockState(BlockPos.containing(player.getX(), player.getEyeY(), player.getZ())).is(Blocks.BUBBLE_COLUMN) || player.hasEffect(MobEffects.WATER_BREATHING))) {
                //Make player drown faster if they try to breath in water
                player.setAirSupply(player.getAirSupply() - (player.getMaxAirSupply() / 5));
            } else if (((player.isEyeInFluid(FluidTags.LAVA) || player.isOnFire())) && !player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                //Same as breathing underwater but damages them too
                player.setAirSupply(player.getAirSupply() - (player.getMaxAirSupply() / 5));
                player.hurt(
                    player.damageSources().onFire(),
                    4.0f
                );
                //ID three for breathing fire
                ModCriteria.ID_TRIGGER.trigger(player, 3);
            } else {
                player.setAirSupply(player.getMaxAirSupply() - 1);
                //Play breathing sound
                player.level().playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    genderIsFemale(player) ? FEMALE_BREATHE : MALE_BREATHE,
                    player.getSoundSource(),
                    1.0f,
                    1.0f
                );
            }
            //ID two for breathe
            ModCriteria.ID_TRIGGER.trigger(player, 2);
        });
        ////

        //Blinking ticks and damage
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 10 != 0) return;

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                //Skip if creative
                if (player.gameMode.isCreative()) continue;

                int currentEyeTicks = player.getAttachedOrElse(BLINK_TICKS, 0);

                //Fixes bug where taking eye drops sometimes keeps eyes strained effect on screen
                if (player.hasEffect(REFRESHED_EFFECT) || !server.getGameRules().get(REQUIRE_BLINKING)) {
                    if (currentEyeTicks != 0) {
                        sendBlinkTickPacket(player, 0);
                    }
                    continue;
                }

                if (currentEyeTicks < MAX_BLINK_TICKS) {
                    //Makes blinking required more often if you're on fire or in the nether
                    int newValue = currentEyeTicks + ((player.isOnFire()) ? 3 : ((player.level().dimension() == Level.NETHER) ? 2 : 1));
                    sendBlinkTickPacket(player, newValue);
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

        //Add resource packs
        registerResourcePack("female-breathing-sounds", "Female Breathing Sounds"); //Support for female breathing sounds without wildfire
        registerResourcePack("x16-dropper-textures", "x16 Dropper Textures"); //I'm not good at art but at least I tried
    }

    private void registerResourcePack(String id, String name) {
        Identifier resource = Identifier.fromNamespaceAndPath(MODID, id);
        ResourceLoaderImpl.registerBuiltinPack(
                resource,
                String.format("resourcepacks/%s", resource.getPath()),
                FabricLoader.getInstance().getModContainer(MODID).orElseThrow(),
                Component.literal(name),
                PackActivationType.NORMAL
        );
    }

    private void sendBlinkTickPacket(ServerPlayer player, int value) {
        if (player.getAttachedOrElse(BLINK_TICKS, 0) != value) {
            player.setAttached(BLINK_TICKS, value);
            ServerPlayNetworking.send(player, new BlinkTickPacket(value));
        }
    }

    //Wildfire support
    private Boolean genderIsFemale(ServerPlayer player) {
        if (FabricLoader.getInstance().isModLoaded("wildfire_gender")) {
            //Unfortunately no support for the "other" gender :{
            return (WildfireAPI.getPlayerGender(player.getUUID()) == Gender.FEMALE);
        }
        return false;
    }
}