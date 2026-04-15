package com.lumi.habd.datagen;

import static com.lumi.habd.items.ItemRegistrar.EYE_DROPS;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.model.*;

public class ItemModelProvider extends FabricModelProvider {
    @Override
    public void generateBlockStateModels(net.minecraft.client.data.models.BlockModelGenerators blockModelGenerator) {}

    @Override
    public void generateItemModels(net.minecraft.client.data.models.ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateBooleanDispatch(
            EYE_DROPS,
            ItemModelUtils.isUsingItem(),
            ItemModelUtils.plainModel(itemModelGenerator.createFlatItemModel(EYE_DROPS, "_using", ModelTemplates.FLAT_ITEM)),
            ItemModelUtils.plainModel(itemModelGenerator.createFlatItemModel(EYE_DROPS, ModelTemplates.FLAT_ITEM))
        );
    }

    @Override
    public String getName() {
        return "ItemModelProvider";
    }

    public ItemModelProvider(FabricPackOutput output) {
        super(output);
    }
}
