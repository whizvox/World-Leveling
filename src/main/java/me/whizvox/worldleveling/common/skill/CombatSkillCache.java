package me.whizvox.worldleveling.common.skill;

import me.whizvox.worldleveling.common.api.Cache;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CombatSkillCache implements Cache {
  
  private final Map<Integer, AttackedEntities> cache;

  public CombatSkillCache() {
    cache = new HashMap<>();
  }

  public void damage(Player attacker, LivingEntity target, double damageDealt) {
    cache.computeIfAbsent(attacker.getId(), id -> new AttackedEntities()).damage(target, damageDealt);
  }

  public double finish(Player attacker, LivingEntity target) {
    AttackedEntities attackedEntities = cache.get(attacker.getId());
    if (attackedEntities == null) {
      return 0.0;
    }
    return attackedEntities.finish(target);
  }

  public double damageAndFinish(Player attacker, LivingEntity target, double damageDealt) {
    damage(attacker, target, damageDealt);
    return finish(attacker, target);
  }

  @Override
  public void clear() {
    cache.clear();
  }

  private static class AttackedEntityEntry {
    final Instant time;
    double totalDamage;
    AttackedEntityEntry(Instant time, double totalDamage) {
      this.time = time;
      this.totalDamage = totalDamage;
    }
  }

  private static class AttackedEntities {
    final ArrayList<Integer> entriesToDelete;
    final Map<Integer, AttackedEntityEntry> damageEntries;
    AttackedEntities() {
      entriesToDelete = new ArrayList<>();
      damageEntries = new HashMap<>();
    }
    private void clearOldEntries() {
      final Instant now = Instant.now();
      entriesToDelete.clear();
      damageEntries.forEach((id, entry) -> {
        if (Duration.between(entry.time, now).getSeconds() > 60) {
          entriesToDelete.add(id);
        }
      });
      entriesToDelete.forEach(damageEntries::remove);
    }
    public AttackedEntities damage(LivingEntity target, double damageDealt) {
      clearOldEntries();
      final int id = target.getId();
      AttackedEntityEntry entry = damageEntries.get(id);
      if (entry == null) {
        damageEntries.put(id, new AttackedEntityEntry(Instant.now(), damageDealt));
      } else {
        entry.totalDamage += damageDealt;
      }
      return this;
    }
    public double finish(LivingEntity target) {
      Double totalDamage = this.damageEntries.remove(target.getId()).totalDamage;
      return Objects.requireNonNullElse(totalDamage, 0.0);
    }
  }

}
