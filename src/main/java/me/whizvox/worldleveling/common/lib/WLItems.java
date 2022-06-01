package me.whizvox.worldleveling.common.lib;

import me.whizvox.worldleveling.WorldLeveling;
import me.whizvox.worldleveling.common.api.ability.mining.ForgeTypes;
import me.whizvox.worldleveling.common.item.ForgeStructureItem;
import me.whizvox.worldleveling.common.item.ProspectorsPickItem;
import me.whizvox.worldleveling.common.item.WLBlockItem;
import me.whizvox.worldleveling.common.item.WLMaterialItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WLItems {

  private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WorldLeveling.MOD_ID);

  public static void register(IEventBus bus) {
    ITEMS.register(bus);
  }

  public static final RegistryObject<Item>
      SOOTY_ALLOY_COMPOUND = ITEMS.register("sooty_alloy_compound", WLMaterialItem::new),
      DARK_ALLOY_COMPOUND = ITEMS.register("dark_alloy_compound", WLMaterialItem::new),
      SOOTY_ALLOY_INGOT = ITEMS.register("sooty_alloy_ingot", WLMaterialItem::new),
      DARK_ALLOY_INGOT = ITEMS.register("dark_alloy_ingot", WLMaterialItem::new);

  public static final RegistryObject<ProspectorsPickItem>
      PROSPECTORS_PICK = ITEMS.register("prospectors_pick", ProspectorsPickItem::new);
  
  public static final RegistryObject<BlockItem>
      SOOTY_BRICKS = ITEMS.register("sooty_bricks", () -> new WLBlockItem(WLBlocks.SOOTY_BRICKS)),
      DARK_BRICKS = ITEMS.register("dark_bricks", () -> new WLBlockItem(WLBlocks.DARK_BRICKS));

  public static final RegistryObject<ForgeStructureItem>
      SOOTY_FORGE = ITEMS.register("sooty_forge", () -> new ForgeStructureItem(ForgeTypes.SOOTY)),
      DARK_FORGE = ITEMS.register("dark_forge", () -> new ForgeStructureItem(ForgeTypes.DARK));

}
