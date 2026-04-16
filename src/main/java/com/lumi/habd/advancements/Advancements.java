package com.lumi.habd.advancements;

import com.lumi.habd.advancements.Criterion.IDAdvancementCriteria;
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
import static com.lumi.habd.items.ItemRegistrar.EYE_DROPS;

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
            .addCriterion("id_trigger", ModCriteria.ID_TRIGGER.createCriterion(new IDAdvancementCriteria.Conditions(Optional.empty(), 0)))
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
                true
            )
            .parent(PARENT)
            .addCriterion("id_trigger", ModCriteria.ID_TRIGGER.createCriterion(new IDAdvancementCriteria.Conditions(Optional.empty(), 1)))
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
                true
            )
            .parent(PARENT)
            .addCriterion("id_trigger", ModCriteria.ID_TRIGGER.createCriterion(new IDAdvancementCriteria.Conditions(Optional.empty(), 2)))
            .save(consumer, String.format("%s:breathe", MODID));

        //Breathing fire advancement
        AdvancementHolder BREATH_FIRE = Advancement.Builder.advancement()
            .display(
                Items.FIRE_CHARGE,
                Component.literal("Fire breather"),
                Component.literal("Try breathing in lava or when on fire"),
                null,
                AdvancementType.TASK,
                true,
                true,
                true
            )
            .parent(BREATH)
            .addCriterion("id_trigger", ModCriteria.ID_TRIGGER.createCriterion(new IDAdvancementCriteria.Conditions(Optional.empty(), 3)))
            .save(consumer, String.format("%s:breathe_fire", MODID));

        //Eye dropper advancement
        AdvancementHolder USE_EYE_DROPPER = Advancement.Builder.advancement()
            .display(
                EYE_DROPS,
                Component.literal("It says gullible on the ceiling!"),
                Component.literal("Try to use eye drops while not looking up"),
                null,
                AdvancementType.TASK,
                true,
                true,
                true
            )
            .parent(PARENT)
            .addCriterion("id_trigger", ModCriteria.ID_TRIGGER.createCriterion(new IDAdvancementCriteria.Conditions(Optional.empty(), 4)))
            .save(consumer, String.format("%s:use_eye_dropper", MODID));
    }
}
