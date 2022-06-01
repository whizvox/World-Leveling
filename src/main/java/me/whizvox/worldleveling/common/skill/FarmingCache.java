package me.whizvox.worldleveling.common.skill;

import me.whizvox.worldleveling.common.api.Cache;
import me.whizvox.worldleveling.common.util.BlockTagAndNameCache;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class FarmingCache implements Cache {

  private final BlockTagAndNameCache<Integer> blockBreakXpCache;
  private final BlockTagAndNameCache<Integer> seedPlaceXpCache;

  public FarmingCache(Map<String, Integer> rawBlockBreakValues, Map<String, Integer> rawSeedPlaceValues) {
    blockBreakXpCache = new BlockTagAndNameCache<>(rawBlockBreakValues, 0);
    seedPlaceXpCache = new BlockTagAndNameCache<>(rawSeedPlaceValues, 0);
  }

  public int getExperienceFromBlockBreak(BlockState state) {
    return blockBreakXpCache.getValue(state);
  }

  public int getExperienceFromBlockPlace(BlockState state) {
    return seedPlaceXpCache.getValue(state);
  }

  @Override
  public void clear() {
    blockBreakXpCache.clear();
  }

}
