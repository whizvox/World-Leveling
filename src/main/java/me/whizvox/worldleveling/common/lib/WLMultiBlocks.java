package me.whizvox.worldleveling.common.lib;

import me.whizvox.worldleveling.WorldLeveling;
import me.whizvox.worldleveling.common.lib.multiblock.MultiBlockStructureEntry;
import net.minecraft.resources.ResourceLocation;

public class WLMultiBlocks {

  private static MultiBlockStructureEntry create(String name) {
    return new MultiBlockStructureEntry(new ResourceLocation(WorldLeveling.MOD_ID, name));
  }

  public static final MultiBlockStructureEntry
      SOOTY_FORGE = create("forge/sooty"),
      DARK_FORGE = create("forge/dark");

}
