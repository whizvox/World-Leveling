package me.whizvox.worldleveling.datagen;

import me.whizvox.worldleveling.datagen.client.WLBlockStateProvider;
import me.whizvox.worldleveling.datagen.client.WLItemModelProvider;
import me.whizvox.worldleveling.datagen.common.loot.WLLootTableProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class WLDataGen {

  public static void register(IEventBus bus) {
    bus.addListener(WLDataGen::onGatherData);
  }

  private static void onGatherData(final GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    ExistingFileHelper fileHelper = event.getExistingFileHelper();

    if (event.includeServer()) {
      gen.addProvider(new WLLootTableProvider(gen));
    }
    if (event.includeClient()) {
      gen.addProvider(new WLBlockStateProvider(gen, fileHelper));
      gen.addProvider(new WLItemModelProvider(gen, fileHelper));
    }
  }

}
