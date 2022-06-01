package me.whizvox.worldleveling.common.inventory.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ForgeFuelSlot extends SlotItemHandler {

  public ForgeFuelSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
    super(itemHandler, index, xPosition, yPosition);
  }

  @Override
  public boolean mayPlace(@NotNull ItemStack stack) {
    return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
  }

}
