package me.whizvox.worldleveling.common.item;

import me.whizvox.worldleveling.common.lib.WLCreativeTab;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class WLBlockItem extends BlockItem {

  public WLBlockItem(Supplier<? extends Block> blockSupplier) {
    super(blockSupplier.get(), new Properties().tab(WLCreativeTab.TAB));
  }

}
