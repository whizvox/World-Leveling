package me.whizvox.worldleveling.mixin;

import me.whizvox.worldleveling.common.event.ItemDamagedEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ItemStack.class)
public class ItemStackMixin {

  @Inject(
      method = "hurt",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/advancements/critereon/ItemDurabilityTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/ItemStack;I)V",
          shift = At.Shift.BY,
          by = -1
      ),
      cancellable = true
  )
  private void toolTakeDamage(int damage, Random rand, ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
    if (MinecraftForge.EVENT_BUS.post(new ItemDamagedEvent((ItemStack) (Object) this, damage, rand, player))) {
      cir.cancel();
    }
  }

}
