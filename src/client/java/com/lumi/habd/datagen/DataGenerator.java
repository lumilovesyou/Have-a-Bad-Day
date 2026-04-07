package com.lumi.habd.datagen;

import com.lumi.habd.advancements.Advancements;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(Advancements::new);
        pack.addProvider(ItemModelProvider::new);
        pack.addProvider(ModRecipeProvider::new);
    }
}