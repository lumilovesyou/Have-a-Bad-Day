package com.lumi.habd.custom;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import static com.lumi.habd.HaveABadDay.MODID;

public class BreathingResources {
    //Player breathe packet
    public record BreathRefreshPacket() implements CustomPacketPayload {
        public static final Type<BreathRefreshPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MODID, "breath_refresh"));

        public static final StreamCodec<ByteBuf, BreathRefreshPacket> CODEC = StreamCodec.unit(new BreathRefreshPacket());

        @Override
        public Type<BreathRefreshPacket> type() { return TYPE; }
    }
}
