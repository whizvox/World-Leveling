package me.whizvox.worldleveling.common.inventory.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ForgeOutputSlot extends SlotItemHandler {

  public ForgeOutputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
    super(itemHandler, index, xPosition, yPosition);
  }

  @Override
  public boolean mayPlace(@NotNull ItemStack stack) {
    return false;
  }

  @Override
  public void onTake(Player player, ItemStack smelted) {
    super.onTake(player, smelted);
    smelted.onCraftedBy(player.level, player, smelted.getCount());
    ForgeEventFactory.firePlayerSmeltedEvent(player, smelted);
  }

}
