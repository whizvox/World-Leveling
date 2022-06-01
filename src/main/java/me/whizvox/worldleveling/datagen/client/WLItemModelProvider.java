package me.whizvox.worldleveling.datagen.client;

import me.whizvox.worldleveling.WorldLeveling;
import me.whizvox.worldleveling.common.lib.WLItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class WLItemModelProvider extends ItemModelProvider {


  public WLItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
    super(generator, WorldLeveling.MOD_ID, existingFileHelper);
  }

  private void simpleWithParent(String baseName, ResourceLocation parent, ResourceLocation texture) {
    withExistingParent(baseName, parent).texture("layer0", texture);
  }

  private void generated(Item item, ResourceLocation texture) {
    simpleWithParent(item.getRegistryName().getPath(), mcLoc("item/generated"), texture);
  }

  private void generated(Item item) {
    generated(item, modLoc("item/" + item.getRegistryName().getPath()));
  }

  private void handheld(Item item, ResourceLocation texture) {
    simpleWithParent(item.getRegistryName().getPath(), mcLoc("item/handheld"), texture);
  }

  private void handheld(Item item) {
    handheld(item, modLoc("item/" + item.getRegistryName().getPath()));
  }

  @Override
  protected void registerModels() {
    generated(WLItems.SOOTY_ALLOY_INGOT.get());
    generated(WLItems.DARK_ALLOY_INGOT.get());
    generated(WLItems.SOOTY_ALLOY_COMPOUND.get());
    generated(WLItems.DARK_ALLOY_COMPOUND.get());

    handheld(WLItems.PROSPECTORS_PICK.get());

  }

}
