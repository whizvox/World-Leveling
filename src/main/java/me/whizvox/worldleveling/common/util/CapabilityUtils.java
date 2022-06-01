package me.whizvox.worldleveling.common.util;

import me.whizvox.worldleveling.common.capability.PlayerSkills;
import me.whizvox.worldleveling.common.capability.ProspectedBlock;
import me.whizvox.worldleveling.common.lib.WLCapabilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.function.Consumer;

public class CapabilityUtils {

  public static <T> void handle(Capability<T> capability, ICapabilityProvider provider, Consumer<T> consumer) {
    provider.getCapability(capability).ifPresent(consumer::accept);
  }

  public static void handlePlayerSkills(Player player, Consumer<PlayerSkills> consumer) {
    handle(WLCapabilities.PLAYER_SKILLS, player, consumer);
  }

  public static void handleProspectedBlock(ItemStack stack, Consumer<ProspectedBlock> consumer) {
    handle(WLCapabilities.PROSPECTED_BLOCK, stack, consumer);
  }

}
