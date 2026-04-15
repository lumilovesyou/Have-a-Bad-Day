package com.lumi.habd.sounds;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import static com.lumi.habd.HaveABadDay.MODID;

public class SoundRegistrar {
    private SoundRegistrar() {}

    public static final SoundEvent MALE_BREATHE = registerSound("male_breathe");
    public static final SoundEvent FEMALE_BREATHE = registerSound("female_breathe");

    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.fromNamespaceAndPath(MODID, id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
    }

    public static void initSounds() {}
}
