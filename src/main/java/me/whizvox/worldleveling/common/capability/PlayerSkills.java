package me.whizvox.worldleveling.common.capability;

import me.whizvox.worldleveling.common.api.Cache;
import me.whizvox.worldleveling.common.api.Skill;
import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.api.ability.Ability;
import me.whizvox.worldleveling.common.lib.WLCaches;
import me.whizvox.worldleveling.common.lib.WLRegistries;
import me.whizvox.worldleveling.common.lib.internal.WLLog;
import me.whizvox.worldleveling.common.network.message.SyncPlayerSkillsMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class PlayerSkills implements INBTSerializable<CompoundTag> {

  private final Map<SkillType, Skill> skills;
  private final Set<Ability> abilities;
  private final Set<Ability> activeAbilities;
  private final Map<SkillType, Cache> caches;

  public PlayerSkills() {
    skills = new HashMap<>();
    abilities = new HashSet<>();
    activeAbilities = new HashSet<>();
    caches = new HashMap<>();
  }

  public Stream<Skill> allSkills() {
    return skills.values().parallelStream();
  }

  public Stream<Ability> allAbilities() {
    return abilities.stream();
  }

  public Stream<Ability> activeAbilities() {
    return activeAbilities.stream();
  }

  public void clearCache() {
    caches.forEach((skillType, cache) -> cache.clear());
  }

  public Optional<Skill> getSkill(SkillType skill) {
    return Optional.ofNullable(skills.getOrDefault(skill, null));
  }

  public boolean hasAbility(Ability ability) {
    return abilities.contains(ability);
  }

  public Stream<Ability> allAbilities(Ability... filters) {
    return Arrays.stream(filters).filter(this::hasAbility);
  }

  public boolean isAbilityActive(Ability ability) {
    return activeAbilities.contains(ability);
  }

  public Stream<Ability> activeAbilities(Ability... filters) {
    return Arrays.stream(filters).filter(this::isAbilityActive);
  }

  public int getExperience(SkillType skillType) {
    Skill skill = skills.get(skillType);
    if (skill != null) {
      return skill.getExperience();
    }
    return -1;
  }

  public int getLevel(SkillType skillType) {
    Skill skill = skills.get(skillType);
    if (skill != null) {
      return skill.getLevel();
    }
    return -1;
  }

  public boolean canPurchaseAbility(Ability ability) {
    return !hasAbility(ability) &&
        ability.getDependencies().stream().allMatch(this::hasAbility) &&
        getSkill(ability.getSkill()).map(skill -> skill.getAbilityPoints() >= ability.getCost())
            .orElse(false);
  }

  public boolean canRefundAbility(Ability ability) {
    return hasAbility(ability) && WLCaches.getDependents(ability).stream().noneMatch(abilities::contains);
  }

  public boolean setExperience(SkillType skill, int exp) {
    return skills.computeIfAbsent(skill, skillType -> skillType.createSkill(0, null)).setExperience(exp);
  }

  public boolean increaseExperience(SkillType skill, int amount) {
    return skills.computeIfAbsent(skill, skillType -> skillType.createSkill(0, null)).increaseExperience(amount);
  }

  public boolean purchaseAbility(Ability ability) {
    if (canPurchaseAbility(ability)) {
      getSkill(ability.getSkill()).ifPresent(skill -> skill.spendPoints(ability.getCost()));
      addAbility(ability);
      return true;
    }
    return false;
  }

  public boolean refundAbility(Ability ability) {
    if (canRefundAbility(ability)) {
      getSkill(ability.getSkill()).ifPresent(skill -> skill.setAbilityPoints(skill.getAbilityPoints() + ability.getCost()));
      removeAbility(ability);
      return true;
    }
    return false;
  }

  public boolean addAbility(Ability ability) {
    boolean added = abilities.add(ability);
    if (added) {
      ability.getAllToReplace().forEach(activeAbilities::remove);
      activeAbilities.add(ability);
    }
    return added;
  }

  public boolean removeAbility(Ability ability) {
    boolean removed = abilities.remove(ability);
    if (removed) {
      activeAbilities.addAll(ability.getAllToReplace());
      activeAbilities.remove(ability);
    }
    return removed;
  }

  public void sync(SyncPlayerSkillsMessage msg) {
    skills.clear();
    abilities.clear();
    caches.clear();
    msg.skills().forEach(skill -> skills.put(skill.getType(), skill));
    msg.abilities().forEach(this::addAbility);
  }

  @SuppressWarnings("unchecked")
  @Nullable
  public <T extends Cache> T getCache(SkillType type) {
    return (T) caches.get(type);
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag root = new CompoundTag();
    ListTag skillsTag = new ListTag();
    allSkills().forEach(skill -> skillsTag.add(skill.serialize()));
    root.put("skills", skillsTag);
    ListTag abilitiesTag = new ListTag();
    allAbilities().forEach(ability -> {
      ResourceLocation name = ability.getRegistryName();
      if (name != null) {
        abilitiesTag.add(StringTag.valueOf(name.toString()));
      }
    });
    root.put("abilities", abilitiesTag);
    return root;
  }

  @Override
  public void deserializeNBT(CompoundTag root) {
    skills.clear();
    caches.clear();
    ListTag skillsTag = root.getList("skills", ListTag.TAG_COMPOUND);
    skillsTag.forEach(tag -> {
      CompoundTag skillTag = (CompoundTag) tag;
      String skillNameStr = skillTag.getString("type");
      ResourceLocation skillName = ResourceLocation.tryParse(skillNameStr);
      if (skillName != null) {
        SkillType type = WLRegistries.SKILLS.get().getValue(skillName);
        if (type != null) {
          int experience = skillTag.getInt("xp");
          int abilityPoints = skillTag.getByte("ap");
          Tag extraData = null;
          if (skillTag.contains("extraData")) {
            extraData = skillTag.get("extraData");
          }
          Skill skill = type.createSkill(experience, extraData);
          skill.setAbilityPoints(abilityPoints);
          skills.put(type, skill);
          Cache cache = type.createPlayerCache();
          if (cache != Cache.DUMMY) {
            caches.put(type, cache);
          }
        } else {
          WLLog.LOGGER.warn("Unknown skill name while deserializing: {}", skillNameStr);
        }
      } else {
        WLLog.LOGGER.warn("Malformed skill name while deserializing: {}", skillNameStr);
      }
    });
    ListTag abilitiesTag = root.getList("abilities", ListTag.TAG_STRING);
    abilitiesTag.forEach(tag -> {
      String abilityNameStr = tag.getAsString();
      ResourceLocation abilityName = ResourceLocation.tryParse(abilityNameStr);
      if (abilityName != null) {
        Ability ability = WLRegistries.ABILITIES.get().getValue(abilityName);
        if (ability != null) {
          addAbility(ability);
        } else {
          WLLog.LOGGER.warn("Unknown ability name while deserializing: {}", abilityNameStr);
        }
      } else {
        WLLog.LOGGER.warn("Malformed ability name while deserializing: {}", abilityNameStr);
      }
    });
  }

}
