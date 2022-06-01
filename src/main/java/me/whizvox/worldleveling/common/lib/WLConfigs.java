package me.whizvox.worldleveling.common.lib;

import me.whizvox.worldleveling.WorldLeveling;
import me.whizvox.worldleveling.common.config.Config;
import me.whizvox.worldleveling.common.lib.config.WLCombatSkillConfig;
import me.whizvox.worldleveling.common.lib.config.WLFarmingSkillConfig;
import me.whizvox.worldleveling.common.lib.config.WLMiningSkillConfig;
import me.whizvox.worldleveling.common.lib.internal.WLFiles;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class WLConfigs {

  private static final Config.Builder
      builderSkillMining = new Config.Builder(),
      builderSkillCombat = new Config.Builder(),
      builderSkillFarming = new Config.Builder();

  public static final WLMiningSkillConfig SKILL_MINING = new WLMiningSkillConfig(builderSkillMining);
  public static final WLCombatSkillConfig SKILL_COMBAT = new WLCombatSkillConfig(builderSkillCombat);
  public static final WLFarmingSkillConfig SKILL_FARMING = new WLFarmingSkillConfig(builderSkillFarming);

  private record ConfigInit(Config.Builder builder, ModConfig.Type type, String path) {}

  private static final Collection<ConfigInit> configs;

  static {
    ArrayList<ConfigInit> configsTemp = new ArrayList<>();
    configsTemp.add(new ConfigInit(builderSkillMining, ModConfig.Type.SERVER, "skills/mining"));
    configsTemp.add(new ConfigInit(builderSkillCombat, ModConfig.Type.SERVER, "skills/combat"));
    configsTemp.add(new ConfigInit(builderSkillFarming, ModConfig.Type.SERVER, "skills/farming"));
    configs = Collections.unmodifiableCollection(configsTemp);
  }

  private static String path(String baseName) {
    return WorldLeveling.MOD_ID + "/" + baseName + ".toml";
  }

  public static void register(IEventBus modBus, IEventBus forgeBus) {
    modBus.addListener(WLConfigs::registerClientAndCommonConfigs);
    forgeBus.addListener(WLConfigs::registerServerConfigs);
  }

  private static void loadAndSaveConfig(Config config, Path path) {
    Path parent = path.getParent();
    if (parent != null) {
      try {
        Files.createDirectories(parent);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    if (Files.exists(path)) {
      try (InputStream in = Files.newInputStream(path)) {
        config.load(in);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    try (OutputStream out = Files.newOutputStream(path)) {
      config.save(out);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void registerClientAndCommonConfigs(final FMLCommonSetupEvent event) {
    configs.stream().filter(init -> init.type == ModConfig.Type.CLIENT || init.type == ModConfig.Type.COMMON).forEach(init -> {
      Config config = init.builder.build();
      Path path = WLFiles.commonOrClientConfig(init.path + ".json");
      loadAndSaveConfig(config, path);
    });
  }

  private static void registerServerConfigs(final ServerStartingEvent event) {
    configs.stream().filter(init -> init.type == ModConfig.Type.SERVER).forEach(init -> {
      Config config = init.builder.build();
      Path path = event.getServer().getWorldPath(WLFiles.serverConfig(init.path + ".json"));
      loadAndSaveConfig(config, path);
    });
  }

}
