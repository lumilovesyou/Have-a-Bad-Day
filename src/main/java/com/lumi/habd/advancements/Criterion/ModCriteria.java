package com.lumi.habd.advancements.Criterion;

import net.minecraft.advancements.CriteriaTriggers;

import static com.lumi.habd.HaveABadDay.MODID;

public class ModCriteria {
    public static final IDAdvancementCriteria ID_TRIGGER = CriteriaTriggers.register(String.format("%s:id_trigger", MODID), new IDAdvancementCriteria());

    public static void init() {}
}