package me.whizvox.worldleveling.common.lib;

import me.whizvox.worldleveling.WorldLeveling;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class WLCreativeTab extends CreativeModeTab {

  private WLCreativeTab() {
    super(WorldLeveling.MOD_ID);
  }

  @Override
  public ItemStack makeIcon() {
    return new ItemStack(WLItems.SOOTY_ALLOY_INGOT.get());
  }

  public static final CreativeModeTab TAB = new WLCreativeTab();

}
