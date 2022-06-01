package me.whizvox.worldleveling.common.skill;

import me.whizvox.worldleveling.common.api.Cache;
import me.whizvox.worldleveling.common.api.Skill;
import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.lib.WLConfigs;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CombatSkillType extends SkillType {

  public static final UUID
      MOD_ATTACK_DAMAGE_BUFF = UUID.fromString("22b5e134-12af-4605-a1cb-9ef943d9220f"),
      MOD_ATTACK_SPEED_BUFF = UUID.fromString("6781e67a-2ded-4a85-8ede-6dbb088ab3ba");

  @Override
  public Skill createSkill(int experience, @Nullable Tag extraData) {
    return new CombatSkill(this, experience);
  }

  @Override
  public void onLevelChange(Player player, int level) {
    AttributeInstance atkDmgAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
    if (atkDmgAttr != null) {
      if (atkDmgAttr.getModifier(MOD_ATTACK_DAMAGE_BUFF) != null) {
        atkDmgAttr.removeModifier(MOD_ATTACK_DAMAGE_BUFF);
      }
      atkDmgAttr.addPermanentModifier(new AttributeModifier(MOD_ATTACK_DAMAGE_BUFF, "worldleveling.combat.attack_damage_buff", level * WLConfigs.SKILL_COMBAT.damageLevelMultiplier.get(), AttributeModifier.Operation.ADDITION));
    }
    AttributeInstance atkSpdAttr = player.getAttribute(Attributes.ATTACK_SPEED);
    if (atkSpdAttr != null) {
      if (atkSpdAttr.getModifier(MOD_ATTACK_SPEED_BUFF) != null) {
        atkSpdAttr.removeModifier(MOD_ATTACK_SPEED_BUFF);
      }
      atkSpdAttr.addPermanentModifier(new AttributeModifier(MOD_ATTACK_SPEED_BUFF, "worldleveling.combat.attack_speed_buff", 1.0F - (level * WLConfigs.SKILL_COMBAT.attackSpeedMultiplier.get()), AttributeModifier.Operation.MULTIPLY_BASE));
    }
  }

  @Override
  public Cache createPlayerCache() {
    return new CombatSkillCache();
  }

}
