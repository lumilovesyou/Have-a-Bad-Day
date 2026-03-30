package com.lumi.habd.custom.Advancements.Criterion;

import net.minecraft.advancements.CriteriaTriggers;

import static com.lumi.habd.HaveABadDay.MODID;

public class ModCriteria {
    public static final BlinkAdvancementCriteria BLINK = CriteriaTriggers.register(MODID + ":blink", new BlinkAdvancementCriteria());

    public static void init() {}
}