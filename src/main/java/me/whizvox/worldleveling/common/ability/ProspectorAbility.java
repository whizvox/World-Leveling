package me.whizvox.worldleveling.common.ability;

import me.whizvox.worldleveling.common.api.ability.Ability;
import me.whizvox.worldleveling.common.api.ability.AbilityBuilder;
import me.whizvox.worldleveling.common.capability.PlayerSkills;

import java.util.Comparator;
import java.util.Optional;

public class ProspectorAbility extends Ability {

  public final int level;

  public ProspectorAbility(AbilityBuilder<?> builder, int level) {
    super(builder);
    this.level = level;
  }

  public static Optional<ProspectorAbility> highestLevel(PlayerSkills skills) {
    return skills
        .allAbilities()
        .filter(ability -> ability instanceof ProspectorAbility)
        .map(ability -> (ProspectorAbility) ability)
        .max(Comparator.comparingInt(ability -> ability.level));
  }

}
