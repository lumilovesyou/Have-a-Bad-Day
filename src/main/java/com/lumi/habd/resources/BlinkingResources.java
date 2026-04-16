package com.lumi.habd.resources;

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
