package me.whizvox.worldleveling.common.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class InventoryUtils {

  public static ItemStack insertIntoInventory(IItemHandler inventory, ItemStack stack, boolean simulate) {
    ItemStack overflow = stack.copy();
    for (int i = 0; i < inventory.getSlots() && !overflow.isEmpty(); i++) {
      overflow = inventory.insertItem(i, overflow, simulate);
    }
    return overflow;
  }

  public static boolean isMenuValid(@Nullable BlockEntity tile, Player player) {
    return tile == null || tile.getBlockPos().distToCenterSqr(player.position()) <= 64.0;
  }

}
