package me.whizvox.worldleveling.common.skill;

import me.whizvox.worldleveling.common.api.Skill;
import me.whizvox.worldleveling.common.api.SkillType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class CombatSkill extends Skill {

  public CombatSkill(SkillType type, int experience) {
    super(type, experience);
  }

  @Override
  public List<Component> describePassiveEffects(Player player) {
    ArrayList<Component> description = new ArrayList<>();
    AttributeInstance dmgAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
    if (dmgAttr != null) {
      AttributeModifier mod = dmgAttr.getModifier(CombatSkillType.MOD_ATTACK_DAMAGE_BUFF);
      if (mod != null) {
        description.add(new TranslatableComponent(
           "wlskill.worldleveling.combat.description.attackDamageBuff",
           String.format("%.1f", mod.getAmount())
        ));
      }
    }
    AttributeInstance spdAttr = player.getAttribute(Attributes.ATTACK_SPEED);
    if (spdAttr != null) {
      AttributeModifier mod = spdAttr.getModifier(CombatSkillType.MOD_ATTACK_SPEED_BUFF);
      if (mod != null) {
        description.add(new TranslatableComponent(
            "wlskill.worldleveling.combat.description.attackSpeedBuff",
            String.format("%.1f", (1.0 - mod.getAmount()) * 100.0)
        ));
      }
    }
    return description;
  }

}
