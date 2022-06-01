package me.whizvox.worldleveling.common.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Skill {

  private final SkillType type;
  private int experience;
  private int level;
  private int xpNeededForLevelUp;
  private int abilityPoints;

  public Skill(SkillType type, int experience) {
    this.type = type;
    this.experience = experience;
    abilityPoints = 0;
    calculateLevelUpExperience();
  }

  private void calculateLevelUpExperience() {
    level = calculateLevel(experience);
    if (level >= MAX_LEVEL) {
      xpNeededForLevelUp = 0;
    } else {
      xpNeededForLevelUp = calculateRequiredExperience(level + 1);
    }
  }

  public SkillType getType() {
    return type;
  }

  public int getExperience() {
    return experience;
  }

  public int getExperienceNeededForLevelUp() {
    return xpNeededForLevelUp;
  }

  public int getLevel() {
    return level;
  }

  public int getAbilityPoints() {
    return abilityPoints;
  }

  public boolean setExperience(int experience) {
    this.experience = experience;
    if (experience < calculateRequiredExperience(level - 1) || experience > xpNeededForLevelUp) {
      calculateLevelUpExperience();
      return true;
    }
    return false;
  }

  public boolean increaseExperience(int amount) {
    if (amount < 0) {
      return false;
    }
    experience += amount;
    if (xpNeededForLevelUp > 0 && experience >= xpNeededForLevelUp) {
      calculateLevelUpExperience();
      abilityPoints++;
      return true;
    }
    return false;
  }

  public void setAbilityPoints(int abilityPoints) {
    this.abilityPoints = abilityPoints;
  }

  public boolean spendPoints(int amount) {
    if (abilityPoints >= amount) {
      abilityPoints -= amount;
      return true;
    }
    return false;
  }

  @Nullable
  public Tag getExtraData() {
    return null;
  }

  /**
   * Get a list of passive effects that have been applied due to this skill's level.
   * @param player The player whose effects are to be described
   * @return A player-friendly description of a skill's passive effects
   */
  public List<Component> describePassiveEffects(Player player) {
    return Collections.emptyList();
  }

  /**
   * Get a player-friendly description of any extra data that this skill may have. If this skill doesn't utilize extra
   * data, don't override this method.
   * @param player The player
   * @return A player-friendly description of a skill's extra data, if necessary
   */
  public List<Component> describeExtraData(Player player) {
    return Collections.emptyList();
  }

  public CompoundTag serialize() {
    CompoundTag tag = new CompoundTag();
    tag.putString("type", type.getRegistryName().toString());
    tag.putInt("xp", experience);
    Tag extraData = getExtraData();
    if (extraData != null) {
      tag.put("extraData", extraData);
    }
    tag.putByte("ap", (byte) abilityPoints);
    return tag;
  }

  public static final int MAX_LEVEL = 50;

  private static final int[] LEVEL_XP_REQUIREMENTS = new int[MAX_LEVEL];
  static {
    for (int i = 0; i < LEVEL_XP_REQUIREMENTS.length; i++) {
      LEVEL_XP_REQUIREMENTS[i] = (int) (100 * Math.pow(i + 1, 2.6));
    }
  }

  public static int calculateRequiredExperience(int level) {
    if (level <= 0) {
      return 0;
    }
    if (level > MAX_LEVEL) {
      level = MAX_LEVEL;
    }
    return LEVEL_XP_REQUIREMENTS[level - 1];
  }

  public static int calculateLevel(int xp) {
    if (xp < LEVEL_XP_REQUIREMENTS[0]) {
      return 0;
    }
    // while this can be calculated directly, an *exact* result that corresponds to the elements of the array is needed
    for (int i = 0; i < LEVEL_XP_REQUIREMENTS.length; i++) {
      if (xp < LEVEL_XP_REQUIREMENTS[i]) {
        return i;
      }
    }
    return MAX_LEVEL;
  }

}
