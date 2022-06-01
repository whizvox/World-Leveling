package me.whizvox.worldleveling.common.lib.config;

import me.whizvox.worldleveling.common.config.Config;
import me.whizvox.worldleveling.common.config.ConfigValue;
import me.whizvox.worldleveling.common.util.JsonUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class WLMiningSkillConfig {

  public final ConfigValue<Double> digSpeedMultiplier;
  public final ConfigValue<Map<String, Integer>> blocksXp;
  public final ConfigValue<List<String>> prospectingOres;

  public WLMiningSkillConfig(Config.Builder builder) {
    digSpeedMultiplier = builder.defineDouble("digSpeedMultiplier", 0.06F, 0.0F);
    blocksXp = builder.define("blocksXp", BLOCKS_XP_DEFAULT, value -> {}, JsonUtil.TYPE_STRING_INT_MAP);
    prospectingOres = builder.define("prospectingOres", PROSPECTING_ORES_DEFAULT, value -> {}, JsonUtil.TYPE_STRING_LIST);
  }

  private static final LinkedHashMap<String, Integer> BLOCKS_XP_DEFAULT;
  private static final List<String> PROSPECTING_ORES_DEFAULT;

  static {
    Map.Entry<?, ?>[] entries = new Map.Entry[]{
        // #forge:stone includes polished variants that I don't want to have any value
        Map.entry("minecraft:stone", 1),
        Map.entry("minecraft:granite", 1),
        Map.entry("minecraft:diorite", 1),
        Map.entry("minecraft:andesite", 1),
        Map.entry("minecraft:infested_stone", 1),
        Map.entry("minecraft:deepslate", 1),
        Map.entry("minecraft:tuff", 1),
        Map.entry("#forge:netherrack", 1),
        Map.entry("minecraft:soul_sand", 1),
        Map.entry("minecraft:soul_soil", 1),
        Map.entry("#forge:end_stones", 1),
        Map.entry("#forge:sandstone", 1),
        Map.entry("minecraft:blackstone", 1),
        Map.entry("#forge:sand", 1),
        Map.entry("#forge:gravel", 1),
        Map.entry("minecraft:clay", 1),
        Map.entry("minecraft:basalt", 1),
        Map.entry("#minecraft:dirt", 1),
        Map.entry("#minecraft:terracotta", 1),
        Map.entry("minecraft:ice", 1),
        Map.entry("minecraft:calcite", 1),
        Map.entry("minecraft:pointed_dripstone", 1),
        Map.entry("minecraft:dripstone_block", 1),
        Map.entry("minecraft:moss_block", 1),
        Map.entry("minecraft:packed_ice", 2),
        Map.entry("minecraft:magma_block", 2),
        Map.entry("minecraft:blue_ice", 3),
        Map.entry("minecraft:amethyst_block", 4),
        Map.entry("minecraft:budding_amethyst", 4),
        Map.entry("minecraft:small_amethyst_bud", 4),
        Map.entry("minecraft:medium_amethyst_bud", 4),
        Map.entry("minecraft:large_amethyst_bud", 4),
        Map.entry("#forge:ores/coal", 6),
        Map.entry("#forge:ores/copper", 8),
        Map.entry("#forge:ores/quartz", 12),
        Map.entry("minecraft:gilded_blackstone", 13),
        Map.entry("#forge:ores/iron", 15),
        Map.entry("minecraft:glowstone", 17),
        Map.entry("#forge:ores/redstone", 18),
        Map.entry("minecraft:amethyst_cluster", 19),
        Map.entry("#forge:ores/gold", 22),
        Map.entry("#forge:ores/lapis", 35),
        Map.entry("#forge:ores/diamond", 50),
        Map.entry("#forge:ores/emerald", 100),
        Map.entry("#forge:ores/netherite_scrap", 112),
        Map.entry("#forge:obsidian", 65),
        Map.entry("minecraft:crying_obsidian", 75),
        // non-vanilla
        Map.entry("#forge:ores/apatite", 13),
        Map.entry("#forge:ores/tin", 14),
        Map.entry("#forge:ores/sulfur", 20),
        Map.entry("#forge:ores/zinc", 22),
        Map.entry("#forge:ores/aluminum", 25),
        Map.entry("#forge:ores/fluorite", 28),
        Map.entry("#forge:ores/lead", 30),
        Map.entry("#forge:ores/nickel", 36),
        Map.entry("#forge:ores/uranium", 40),
        Map.entry("#forge:ores/silver", 40),
        Map.entry("#forge:ores/cinnabar", 45),
        Map.entry("#forge:ores/cobalt", 80),
        // default for ores
        Map.entry("#forge:ores", 5)
    };

    BLOCKS_XP_DEFAULT = new LinkedHashMap<>();
    for (Map.Entry<?, ?> entry : entries) {
      BLOCKS_XP_DEFAULT.put((String) entry.getKey(), (int) entry.getValue());
    }

    PROSPECTING_ORES_DEFAULT = List.of(
        "#forge:ores",
        "minecraft:glowstone"
    );
  }

}
