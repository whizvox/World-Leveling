package me.whizvox.worldleveling.common.lib.internal;

import me.whizvox.worldleveling.WorldLeveling;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class WLFiles {

  private static Path getAndCreateCommon(Path path) {
    return FMLPaths.getOrCreateGameRelativePath(path, WorldLeveling.MOD_ID);
  }

  private static final String
      PATH_SERVER_CONFIG = "serverconfig/" + WorldLeveling.MOD_ID;

  public static final LevelResource
      DIR_SERVER_CONFIG = new LevelResource(PATH_SERVER_CONFIG);

  public static LevelResource serverConfig(String path) {
    return new LevelResource(PATH_SERVER_CONFIG + "/" + path);
  }

  public static final Path
      DIR_CONFIG = FMLPaths.CONFIGDIR.get();

  public static Path commonOrClientConfig(String path) {
    return DIR_CONFIG.resolve(path);
  }

}
