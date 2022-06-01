package me.whizvox.worldleveling.common.skill;

import me.whizvox.worldleveling.common.api.Skill;
import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.lib.WLConfigs;
import me.whizvox.worldleveling.common.lib.WLSkills;
import me.whizvox.worldleveling.common.util.SkillsHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class MiningSkill extends Skill {

  public MiningSkill(SkillType type, int experience) {
    super(type, experience);
  }

  @Override
  public List<Component> describePassiveEffects(Player player) {
    ArrayList<Component> description = new ArrayList<>();
    SkillsHelper.handle(player, skills -> {
      float speedBuff = (float) (skills.getLevel(WLSkills.MINING.get()) * WLConfigs.SKILL_MINING.digSpeedMultiplier.get());
      if (speedBuff > 0.0F) {
        description.add(new TranslatableComponent(
           "wlskill.worldleveling.mining.description.miningSpeedBuff",
           String.format("%.1f", speedBuff * 100.0F)
        ));
      }
    });
    return description;
  }

}
