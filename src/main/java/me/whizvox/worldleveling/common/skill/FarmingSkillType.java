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

public class FarmingSkillType extends SkillType {

  public static final UUID MODIFIER_HEALTH_BUFF = UUID.fromString("00f5ee92-df47-4cfa-a283-28236747836c");

  @Override
  public Skill createSkill(int experience, @Nullable Tag extraData) {
    return new FarmingSkill(this, experience);
  }

  @Override
  public void onLevelChange(Player player, int level) {
    AttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);
    if (attr != null) {
      if (attr.getModifier(MODIFIER_HEALTH_BUFF) != null) {
        attr.removeModifier(MODIFIER_HEALTH_BUFF);
      }
      attr.addPermanentModifier(new AttributeModifier(
          MODIFIER_HEALTH_BUFF,
          "worldleveling.farming_health_buff",
          WLConfigs.SKILL_FARMING.healthLevelMultiplier.get() * level,
          AttributeModifier.Operation.ADDITION
      ));
    }
  }

  @Override
  public Cache createWorldCache() {
    return new FarmingCache(
        WLConfigs.SKILL_FARMING.breakBlocksXpValues.get(),
        WLConfigs.SKILL_FARMING.plantCropsXpValues.get()
    );
  }

}
