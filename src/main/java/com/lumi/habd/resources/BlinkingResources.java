package com.lumi.habd.resources;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

import static com.lumi.habd.HaveABadDay.MODID;

public class BlinkingResources {
    //Not blinking damage source
    public static final ResourceKey<DamageType> EyesDriedDamageSource = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(MODID, "eyes_dried"));

    //Ticks since player blinked
    public static final AttachmentType<Integer> BLINK_TICKS = AttachmentRegistry.createDefaulted(
            Identifier.fromNamespaceAndPath(MODID, "blinking"),
            () -> 0
    );

    //Ticks of eye drops
    //Added a new value for this to both simplify the code and prevents problems I experienced when trying to make blink ticks work in a different manner
    public static final AttachmentType<Integer> DROPPER_TICKS = AttachmentRegistry.<Integer>builder()
            .initializer(() -> 0)
            .persistent(Codec.INT)
            .buildAndRegister(Identifier.fromNamespaceAndPath(MODID, "drops"));

    //Time passed without blinking
    public record BlinkTickPacket(int tick) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<BlinkTickPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MODID, "blink_tick"));

        public static final StreamCodec<ByteBuf, BlinkTickPacket> CODEC = StreamCodec.composite(ByteBufCodecs.INT, BlinkTickPacket::tick, BlinkTickPacket::new);

        @Override
        public CustomPacketPayload.Type<BlinkTickPacket> type() { return TYPE; }
    }

    //Player blinked packet
    public record BlinkRefreshPacket() implements CustomPacketPayload {
        public static final Type<BlinkRefreshPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MODID, "blink_refresh"));

        public static final StreamCodec<ByteBuf, BlinkRefreshPacket> CODEC = StreamCodec.unit(new BlinkRefreshPacket());

        @Override
        public Type<BlinkRefreshPacket> type() { return TYPE; }
    }
}
