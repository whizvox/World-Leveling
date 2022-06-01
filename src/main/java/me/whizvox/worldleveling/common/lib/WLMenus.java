package me.whizvox.worldleveling.common.lib;

import me.whizvox.worldleveling.WorldLeveling;
import me.whizvox.worldleveling.common.inventory.menu.ForgeMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WLMenus {

  private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, WorldLeveling.MOD_ID);

  public static void register(IEventBus bus) {
    MENUS.register(bus);
  }

  private static ForgeMenu createForgeMenu(RegistryObject<MenuType<ForgeMenu>> menuType, int windowId, Inventory playerInv) {
    return new ForgeMenu(menuType.get(), windowId, playerInv);
  }

  private static ForgeMenu createSootyForgeMenu(int windowId, Inventory playerInv) {
    return createForgeMenu(SOOTY_FORGE, windowId, playerInv);
  }

  private static ForgeMenu createDarkForgeMenu(int windowId, Inventory playerInv) {
    return createForgeMenu(DARK_FORGE, windowId, playerInv);
  }

  public static final RegistryObject<MenuType<ForgeMenu>>
      SOOTY_FORGE = MENUS.register("sooty_forge", () -> new MenuType<>(WLMenus::createSootyForgeMenu));

  public static final RegistryObject<MenuType<ForgeMenu>>
      DARK_FORGE = MENUS.register("dark_forge", () -> new MenuType<>(WLMenus::createDarkForgeMenu));

}
