package me.whizvox.worldleveling.common.skill;

import me.whizvox.worldleveling.common.api.Skill;
import me.whizvox.worldleveling.common.api.SkillType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class FarmingSkill extends Skill {

  public FarmingSkill(SkillType type, int experience) {
    super(type, experience);
  }

  @Override
  public List<Component> describePassiveEffects(Player player) {
    AttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);
    if (attr != null) {
      AttributeModifier mod = attr.getModifier(FarmingSkillType.MODIFIER_HEALTH_BUFF);
      if (mod != null) {
        return List.of(new TranslatableComponent(
            "wlskill.worldleveling.farming.description.maxHealthBuff",
            mod.getAmount()
        ));
      }
    }
    return super.describePassiveEffects(player);
  }

}
