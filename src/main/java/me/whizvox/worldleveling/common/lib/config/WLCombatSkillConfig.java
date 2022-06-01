package me.whizvox.worldleveling.common.lib.config;

import me.whizvox.worldleveling.common.config.Config;
import me.whizvox.worldleveling.common.config.ConfigValue;

public class WLCombatSkillConfig {

  public final ConfigValue<Double> healthXpMultiplier;
  public final ConfigValue<Double> damageLevelMultiplier;
  public final ConfigValue<Double> attackSpeedMultiplier;

  public WLCombatSkillConfig(Config.Builder builder) {
    healthXpMultiplier = builder.defineDouble("healthXpMultiplier", 0.6, 0.0);
    damageLevelMultiplier = builder.defineDouble("damageLevelMultiplier", 0.5, 0.0);
    attackSpeedMultiplier = builder.defineDouble("attackSpeedMultiplier", 0.05, 0.0);
  }

}
