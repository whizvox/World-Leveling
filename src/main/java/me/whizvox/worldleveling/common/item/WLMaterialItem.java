package me.whizvox.worldleveling.common.item;

import me.whizvox.worldleveling.common.lib.WLCreativeTab;
import net.minecraft.world.item.Item;

public class WLMaterialItem extends Item {

  public WLMaterialItem(Properties props) {
    super(props
        .tab(WLCreativeTab.TAB)
    );
  }

  public WLMaterialItem() {
    this(new Item.Properties());
  }

}
