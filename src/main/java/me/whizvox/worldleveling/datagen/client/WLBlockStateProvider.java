package me.whizvox.worldleveling.datagen.client;

import me.whizvox.worldleveling.WorldLeveling;
import me.whizvox.worldleveling.common.block.ForgeInterfaceBlock;
import me.whizvox.worldleveling.common.block.ForgeValveBlock;
import me.whizvox.worldleveling.common.item.ForgeStructureItem;
import me.whizvox.worldleveling.common.lib.WLBlocks;
import me.whizvox.worldleveling.common.lib.WLItems;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class WLBlockStateProvider extends BlockStateProvider {

  public WLBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
    super(gen, WorldLeveling.MOD_ID, exFileHelper);
  }

  private void simpleBlockAndItem(Block block) {
    simpleBlock(block);
    simpleBlockItem(block, cubeAll(block));
  }

  @Override
  protected void registerStatesAndModels() {
    simpleBlockAndItem(WLBlocks.SOOTY_BRICKS.get());
    simpleBlockAndItem(WLBlocks.DARK_BRICKS.get());
    forgeInterfaces();
    forgeValves();
    forgeStructureItems();
  }

  private void forgeInterfaces() {
    for (ForgeInterfaceBlock block : new ForgeInterfaceBlock[] {
        WLBlocks.SOOTY_FORGE_INTERFACE.get(),
        WLBlocks.DARK_FORGE_INTERFACE.get()
    }) {
      String baseName = block.forgeType.toString().toLowerCase();
      ResourceLocation unlitFront = modLoc("block/" + baseName + "_forge_interface_unlit");
      ResourceLocation litFront = modLoc("block/" + baseName + "_forge_interface_lit");
      ResourceLocation siding = modLoc("block/" + baseName + "_bricks");
      ModelFile baseModelUnlit = models().orientable(baseName + "_forge_interface_unlit", siding, unlitFront, siding);
      ModelFile baseModelLit = models().orientable(baseName + "_forge_interface_lit", siding, litFront, siding);
      getVariantBuilder(block).forAllStates(state -> {
        int yRot = (int) state.getValue(ForgeInterfaceBlock.FACING).getOpposite().toYRot();
        boolean lit = state.getValue(ForgeInterfaceBlock.LIT);
        ConfiguredModel.Builder<?> builder = ConfiguredModel.builder();
        if (yRot != 0) {
          builder.rotationY(yRot);
        }
        if (lit) {
          builder.modelFile(baseModelLit);
        } else {
          builder.modelFile(baseModelUnlit);
        }
        return builder.build();
      });
    }
  }

  private void forgeValves() {
    for (ForgeValveBlock block : new ForgeValveBlock[] {
        WLBlocks.SOOTY_FORGE_VALVE.get(),
        WLBlocks.DARK_FORGE_VALVE.get()
    }) {
      String baseName = block.forgeType.toString().toLowerCase();
      ResourceLocation itemOutputTexture = modLoc("block/" + baseName + "_forge_item_output");
      ResourceLocation fuelInputTexture = modLoc("block/" + baseName + "_forge_fuel_input");
      ResourceLocation airInputTexture = modLoc("block/" + baseName + "_forge_air_input");
      ResourceLocation sidingTexture = modLoc("block/" + baseName + "_bricks");
      ModelFile itemInputModel = models().getExistingFile(modLoc("block/" + baseName + "_forge_item_input"));
      ModelFile itemOutputModel = models().orientableWithBottom(baseName + "_forge_item_output", sidingTexture, sidingTexture, itemOutputTexture, sidingTexture);
      ModelFile fuelInputModel = models().orientable(baseName + "_forge_fuel_input", sidingTexture, fuelInputTexture, sidingTexture);
      ModelFile airInputModel = models().orientable(baseName + "_forge_air_input", sidingTexture, airInputTexture, sidingTexture);
      getVariantBuilder(block).forAllStates(state -> {
        Direction facing = state.getValue(ForgeValveBlock.FACING);
        int yRot = (int) facing.getOpposite().toYRot();
        ForgeValveBlock.Type type = state.getValue(ForgeValveBlock.TYPE);
        ConfiguredModel.Builder<?> builder = ConfiguredModel.builder();
        if (yRot != 0) {
          builder.rotationY(yRot);
        }
        switch (type) {
          case ITEM_IN -> builder.modelFile(itemInputModel);
          case ITEM_OUT -> builder.modelFile(itemOutputModel);
          case FUEL_IN -> builder.modelFile(fuelInputModel);
          case AIR_IN -> builder.modelFile(airInputModel);
        }
        return builder.build();
      });
    }
  }

  private void forgeStructureItems() {
    for (ForgeStructureItem item : new ForgeStructureItem[] {
        WLItems.SOOTY_FORGE.get(),
        WLItems.DARK_FORGE.get()
    }) {
      String baseName = item.forgeType.toString().toLowerCase();
      ModelFile baseModelLit = models().getExistingFile(modLoc(baseName + "_forge_interface_lit"));
      itemModels().getBuilder(item.getRegistryName().getPath()).parent(baseModelLit);
    }
  }

}
