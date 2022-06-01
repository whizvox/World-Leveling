package me.whizvox.worldleveling.common.lib;

import me.whizvox.worldleveling.common.api.Cache;
import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.api.ability.Ability;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WLCaches {

  public static void register(IEventBus bus) {
    bus.addListener(WLCaches::onServerStarting);
    bus.addListener(WLCaches::onServerStopping);
  }

  private static final HashMap<SkillType, Cache> worldCaches = new HashMap<>();
  private static final Map<Ability, Collection<Ability>> dependentsLookup = new HashMap<>();

  public static void clear() {
    worldCaches.forEach((skillType, cache) -> cache.clear());
    dependentsLookup.forEach((ability, dependants) -> dependants.clear());
  }

  private static void onServerStarting(final ServerStartingEvent event) {
    worldCaches.clear();
    WLRegistries.SKILLS.get().getEntries().forEach(entry -> {
      SkillType type = entry.getValue();
      Cache cache = type.createWorldCache();
      if (cache != Cache.DUMMY) {
        worldCaches.put(type, cache);
      }
    });
    dependentsLookup.clear();
    WLRegistries.ABILITIES.get().forEach(ability -> {
      List<Ability> dependents = WLRegistries.ABILITIES.get().getValues().stream().filter(a -> a.getDependencies().contains(ability)).toList();
      dependentsLookup.put(ability, dependents);
    });
  }

  private static void onServerStopping(final ServerStoppingEvent event) {
    worldCaches.clear();
    dependentsLookup.clear();
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public static <T extends Cache> T get(SkillType type) {
    return (T) worldCaches.get(type);
  }

  public static Collection<Ability> getDependents(Ability ability) {
    return dependentsLookup.getOrDefault(ability, Collections.emptyList());
  }

}
