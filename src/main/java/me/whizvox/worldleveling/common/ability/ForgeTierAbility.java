package me.whizvox.worldleveling.common.ability;

import me.whizvox.worldleveling.common.api.ability.Ability;
import me.whizvox.worldleveling.common.api.ability.AbilityBuilder;
import me.whizvox.worldleveling.common.api.ability.mining.IForgeType;
import me.whizvox.worldleveling.common.capability.PlayerSkills;
import me.whizvox.worldleveling.common.util.CapabilityUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class ForgeTierAbility extends Ability {

  public final int tier;

  public ForgeTierAbility(AbilityBuilder<?> builder, int tier) {
    super(builder);
    this.tier = tier;
  }

  @Override
  public void addListeners(IEventBus bus) {
  }

  public static Optional<ForgeTierAbility> highestLevel(PlayerSkills skills) {
    return skills.allAbilities()
        .filter(ability -> ability instanceof ForgeTierAbility)
        .map(ability -> (ForgeTierAbility) ability)
        .max(Comparator.comparingInt(ability -> ability.tier));
  }

  public static boolean hasUnlocked(Player player, IForgeType forgeType) {
    AtomicBoolean hasUnlocked = new AtomicBoolean(false);
    CapabilityUtils.handlePlayerSkills(player, skills -> {
      highestLevel(skills).ifPresent(ability -> {
        if (ability.tier >= forgeType.getTier()) {
          hasUnlocked.set(true);
        }
      });
    });
    return hasUnlocked.get();
  }

}
