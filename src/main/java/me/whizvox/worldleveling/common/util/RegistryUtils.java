package me.whizvox.worldleveling.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class RegistryUtils {

  @Nullable
  public static Block getBlock(String nameStr) {
    ResourceLocation name = ResourceLocation.tryParse(nameStr);
    if (name == null) {
      return null;
    }
    return getBlock(name);
  }

  @Nullable
  public static Block getBlock(ResourceLocation name) {
    return ForgeRegistries.BLOCKS.getValue(name);
  }

}
