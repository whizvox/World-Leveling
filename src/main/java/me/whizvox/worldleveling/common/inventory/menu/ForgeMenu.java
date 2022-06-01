package me.whizvox.worldleveling.common.inventory.menu;

import me.whizvox.worldleveling.common.block.entity.ForgeInterfaceBlockEntity;
import me.whizvox.worldleveling.common.inventory.slot.ForgeFuelSlot;
import me.whizvox.worldleveling.common.inventory.slot.ForgeOutputSlot;
import me.whizvox.worldleveling.common.util.InventoryUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class ForgeMenu extends AbstractContainerMenu {

  protected final ForgeInterfaceBlockEntity forge;
  protected final ContainerData data;

  public ForgeMenu(@Nullable MenuType<?> type, int windowId, Inventory playerInv, @Nullable ForgeInterfaceBlockEntity forge, IItemHandler inventory, ContainerData data) {
    super(type, windowId);
    this.forge = forge;
    this.data = data;

    addDataSlots(data);

    for (int x = 0; x < 3; x++) {
      addSlot(new SlotItemHandler(inventory, x, 20 + x * 18, 17));
    }
    for (int x = 0; x < 3; x++) {
      addSlot(new ForgeFuelSlot(inventory, 3 + x, 20 + x * 18, 53));
    }
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 2; x++) {
        addSlot(new ForgeOutputSlot(inventory, 6 + y * 2 + x, 116 + x * 18, 16 + y * 18));
      }
    }

    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 9; x++) {
        addSlot(new Slot(playerInv, 9 + y * 9 + x, 8 + x * 18, 84 + y * 18));
      }
    }
    for (int x = 0; x < 9; x++) {
      addSlot(new Slot(playerInv, x, 8 + x * 18, 142));
    }
  }

  public ForgeMenu(@Nullable MenuType<?> type, int windowId, Inventory playerInv) {
    this(type, windowId, playerInv, null, new ItemStackHandler(ForgeInterfaceBlockEntity.TOTAL_SLOTS), new SimpleContainerData(ForgeInterfaceBlockEntity.COUNT_DATA));
  }

  public ForgeInterfaceBlockEntity getForge() {
    return forge;
  }

  public int getTemperature() {
    return data.get(ForgeInterfaceBlockEntity.DATA_TEMPERATURE);
  }

  public int getMaxTemperature() {
    return data.get(ForgeInterfaceBlockEntity.DATA_MAX_TEMPERATURE);
  }

  public float getSmeltProgress() {
    return (float) data.get(ForgeInterfaceBlockEntity.DATA_SMELT_PROGRESS) / data.get(ForgeInterfaceBlockEntity.DATA_SMELT_TIME);
  }

  public float getBurnProgress() {
    return (float) data.get(ForgeInterfaceBlockEntity.DATA_FUEL_PROGRESS) / data.get(ForgeInterfaceBlockEntity.DATA_FUEL_TIME);
  }

  @Override
  public ItemStack quickMoveStack(Player player, int slotIndex) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.slots.get(slotIndex);

    if (slot.hasItem()) {
      ItemStack itemstack1 = slot.getItem();
      itemstack = itemstack1.copy();

      // clicked in forge -> move to player inventory
      if (slotIndex >= 0 && slotIndex < ForgeInterfaceBlockEntity.TOTAL_SLOTS) {
        if (!this.moveItemStackTo(itemstack1, ForgeInterfaceBlockEntity.TOTAL_SLOTS, this.slots.size(), true)) {
          return ItemStack.EMPTY;
        }
      // clicked in player inventory -> move to forge
      } else if (!this.moveItemStackTo(itemstack1, 0, ForgeInterfaceBlockEntity.TOTAL_SLOTS, false)) {
        return ItemStack.EMPTY;
      }

      if (itemstack1.isEmpty()) {
        slot.set(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }
    }

    return itemstack;
  }

  @Override
  public boolean stillValid(Player player) {
    return InventoryUtils.isMenuValid(forge, player);
  }

}
