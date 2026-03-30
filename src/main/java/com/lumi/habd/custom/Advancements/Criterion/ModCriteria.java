package com.lumi.habd.custom.Advancements.Criterion;

import net.minecraft.advancements.CriteriaTriggers;

import static com.lumi.habd.HaveABadDay.MODID;

public class ModCriteria {
    public static final BlinkAdvancementCriteria BLINK = CriteriaTriggers.register(String.format("%s:blink", MODID), new BlinkAdvancementCriteria());
    public static final BreatheAdvancementCriteria BREATHE = CriteriaTriggers.register(String.format("%s:breathe", MODID), new BreatheAdvancementCriteria());

    public static void init() {}
}