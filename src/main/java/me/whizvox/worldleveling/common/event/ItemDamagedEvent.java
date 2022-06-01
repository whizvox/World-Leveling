package me.whizvox.worldleveling.common.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

@Cancelable
public class ItemDamagedEvent extends Event {

  private final ItemStack stack;
  private int damage;
  private final Random rand;
  private final ServerPlayer player;

  public ItemDamagedEvent(ItemStack stack, int damage, Random rand, @Nullable ServerPlayer player) {
    this.stack = stack;
    this.damage = damage;
    this.rand = rand;
    this.player = player;
  }

  public ItemStack getStack() {
    return stack;
  }

  public int getDamage() {
    return damage;
  }

  public Random getRandom() {
    return rand;
  }

  @Nullable
  public ServerPlayer getPlayer() {
    return player;
  }

  public void setDamage(int damage) {
    this.damage = damage;
    if (this.damage <= 0) {
      setCanceled(true);
    }
  }

}
