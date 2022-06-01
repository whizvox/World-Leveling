package me.whizvox.worldleveling.common.lib.config;

import me.whizvox.worldleveling.common.config.Config;
import me.whizvox.worldleveling.common.config.ConfigValue;
import me.whizvox.worldleveling.common.util.JsonUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public class WLFarmingSkillConfig {

  public final ConfigValue<Map<String, Integer>> breakBlocksXpValues;
  public final ConfigValue<Map<String, Integer>> plantCropsXpValues;
  public final ConfigValue<Integer> cropGrowthXp;
  public final ConfigValue<Integer> cropGrowthPlayerRadius;
  public final ConfigValue<Integer> tillSoilXp;
  public final ConfigValue<Integer> plantSeedXp;
  public final ConfigValue<Integer> treeGrowthXp;
  public final ConfigValue<Integer> treeGrowthPlayerRadius;
  public final ConfigValue<Double> healthLevelMultiplier;

  public WLFarmingSkillConfig(Config.Builder builder) {
    breakBlocksXpValues = builder.define("breakBlocksXpValues", DEFAULT_BREAK_BLOCKS_VALUES, value -> {}, JsonUtil.TYPE_STRING_INT_MAP);
    plantCropsXpValues = builder.define("plantCropsXpValues", DEFAULT_PLANT_CROPS_VALUES, value -> {}, JsonUtil.TYPE_STRING_INT_MAP);
    cropGrowthXp = builder.defineInt("cropsGrowthXp", 8, 0);
    cropGrowthPlayerRadius = builder.defineInt("cropsGrowthPlayerRadius", 10, 0, 500);
    tillSoilXp = builder.defineInt("tillSoilXp", 2, 0);
    plantSeedXp = builder.defineInt("plantSeedXp", 2, 0);
    treeGrowthXp = builder.defineInt("treeGrowthXp", 40, 0);
    treeGrowthPlayerRadius = builder.defineInt("treeGrowthPlayerRadius", 10, 0, 500);
    healthLevelMultiplier = builder.defineDouble("healthLevelMultiplier", 0.5, 0.0);
  }

  private static final LinkedHashMap<String, Integer> DEFAULT_BREAK_BLOCKS_VALUES;
  private static final LinkedHashMap<String, Integer> DEFAULT_PLANT_CROPS_VALUES;

  static {
    Map.Entry<?, ?>[] e1 = new Map.Entry[] {
        Map.entry("#mineraft:crops", 1),
        Map.entry("minecraft:pumpkin", 1),
        Map.entry("minecraft:melon", 1),
        Map.entry("minecraft:cocoa", 1),
        Map.entry("minecraft:brown_mushroom", 1),
        Map.entry("minecraft:red_mushroom", 1),
        Map.entry("minecraft:warped_fungus", 1),
        Map.entry("minecraft:crimson_fungus", 1),
        Map.entry("minecraft:bamboo", 1),
        Map.entry("minecraft:bamboo_sapling", 1),
        Map.entry("minecraft:brown_mushroom_block", 1),
        Map.entry("minecraft:red_mushroom_block", 1),
        Map.entry("minecraft:mushroom_stem", 1),
        Map.entry("minecraft:sugar_cane", 1)
    };
    DEFAULT_BREAK_BLOCKS_VALUES = new LinkedHashMap<>();
    for (Map.Entry<?, ?> entry : e1) {
      DEFAULT_BREAK_BLOCKS_VALUES.put((String) entry.getKey(), (int) entry.getValue());
    }

    Map.Entry<?, ?>[] e2 = new Map.Entry[] {
        Map.entry("#minecraft:crops", 1),
        Map.entry("minecraft:cocoa", 1),
        Map.entry("minecraft:bamboo_sapling", 1),
        Map.entry("minecraft:brown_mushroom", 1),
        Map.entry("minecraft:red_mushroom", 1),
        Map.entry("minecraft:warped_fungus", 1),
        Map.entry("minecraft:crimson_fungus", 1),
        Map.entry("minecraft:sugar_cane", 1),
        Map.entry("#minecraft:saplings", 1),
        Map.entry("#minecraft:flowers", 1),
    };
    DEFAULT_PLANT_CROPS_VALUES = new LinkedHashMap<>();
    for (Map.Entry<?, ?> entry : e2) {
      DEFAULT_PLANT_CROPS_VALUES.put((String) entry.getKey(), (Integer) entry.getValue());
    }
  }

}
