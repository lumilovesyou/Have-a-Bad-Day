package com.lumi.habd.advancements;

import com.lumi.habd.advancements.Criterion.BlinkAdvancementCriteria;
import com.lumi.habd.advancements.Criterion.BreatheAdvancementCriteria;
import com.lumi.habd.advancements.Criterion.ModCriteria;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.lumi.habd.HaveABadDay.MODID;

public class Advancements extends FabricAdvancementProvider {
    public Advancements(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer) {
        //Parent category
        AdvancementHolder PARENT = Advancement.Builder.advancement()
            .display(
                Items.WITHER_ROSE,
                Component.literal("Have a Bad Day"),
                Component.literal("It's not that bad... right?"),
                Identifier.withDefaultNamespace("block/obsidian"),
                AdvancementType.TASK,
                true,
                false,
                false
            )
            .addCriterion("blink", ModCriteria.BLINK.createCriterion(new BlinkAdvancementCriteria.Conditions(Optional.empty())))
            .save(consumer, String.format("%s:have_a_bad_day", MODID));

        //Blinking advancement
        AdvancementHolder BLINK = Advancement.Builder.advancement()
            .display(
                Items.ENDER_PEARL,
                Component.literal("You missed it!"),
                Component.literal("Blink for the first time"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(PARENT)
            .addCriterion("blink", ModCriteria.BLINK.createCriterion(new BlinkAdvancementCriteria.Conditions(Optional.empty())))
            .save(consumer, String.format("%s:blink", MODID));

        //Breathing advancement
        AdvancementHolder BREATH = Advancement.Builder.advancement()
            .display(
                Items.WIND_CHARGE,
                Component.literal("Feel relaxed?"),
                Component.literal("Breathe for the first time"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(PARENT)
            .addCriterion("breathe", ModCriteria.BREATHE.createCriterion(new BreatheAdvancementCriteria.Conditions(Optional.empty())))
            .save(consumer, String.format("%s:breathe", MODID));
    }
}
