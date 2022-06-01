package me.whizvox.worldleveling.common.skill;

import me.whizvox.worldleveling.common.api.Cache;
import me.whizvox.worldleveling.common.util.BlockTagAndNameCache;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiningCache implements Cache {

  private final BlockTagAndNameCache<Integer> xpCache;
  private final BlockTagAndNameCache<Boolean> prospectingOresCache;

  public MiningCache(Map<String, Integer> rawXpValues, List<String> rawProspectingOresValues) {
    xpCache = new BlockTagAndNameCache<>(rawXpValues, 0);
    HashMap<String, Boolean> convertedProspectingOres = new HashMap<>();
    rawProspectingOresValues.forEach(s -> convertedProspectingOres.put(s, true));
    prospectingOresCache = new BlockTagAndNameCache<>(convertedProspectingOres, false);
  }

  public int getExperience(BlockState state) {
    return xpCache.getValue(state);
  }

  public boolean shouldProspectorHighlight(BlockState state) {
    return prospectingOresCache.getValue(state);
  }

  @Override
  public void clear() {
    xpCache.clear();
    prospectingOresCache.clear();
  }

}
