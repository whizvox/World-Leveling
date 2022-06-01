package me.whizvox.worldleveling.common.lib;

import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.skill.CombatSkillType;
import me.whizvox.worldleveling.common.skill.FarmingSkillType;
import me.whizvox.worldleveling.common.skill.MiningSkillType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;

import static me.whizvox.worldleveling.common.lib.WLRegistries.REG_SKILLS;

public class WLSkills {

  public static final RegistryObject<SkillType>
      // mining speed
      MINING = REG_SKILLS.register("mining", MiningSkillType::new),
      // attack damage and speed
      COMBAT = REG_SKILLS.register("combat", CombatSkillType::new),
      // saturation potency and longevity
      FARMING = REG_SKILLS.register("farming", FarmingSkillType::new),
      // healing rate
      FISHING = REG_SKILLS.register("fishing", SkillType::new),
      // movement speed
      EXPLORATION = REG_SKILLS.register("exploration", SkillType::new),
      // max health
      MAGIC = REG_SKILLS.register("magic", SkillType::new);

  public static void register(IEventBus bus) {
    REG_SKILLS.register(bus);
  }

}
