package me.whizvox.worldleveling.common.lib.internal;

import me.whizvox.worldleveling.WorldLeveling;
import net.minecraft.resources.ResourceLocation;

public class WLKeys {

  private static ResourceLocation rl(String path) {
    return new ResourceLocation(WorldLeveling.MOD_ID, path);
  }
  
  public static final ResourceLocation
      REGISTRY_SKILLS = rl("skills"),
      REGISTRY_ABILITIES = rl("abilities"),
      REGISTRY_MODIFIERS = rl("modifiers"),
      NETWORK_CHANNEL_MAIN = rl("main");
  
}
