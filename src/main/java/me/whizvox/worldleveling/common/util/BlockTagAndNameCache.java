package me.whizvox.worldleveling.common.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class BlockTagAndNameCache<V extends Comparable<V>> extends TagAndNameCache<Block, V> {

  public BlockTagAndNameCache(Map<String, V> rawValues, V zeroValue) {
    super(rawValues, BlockTags::create, zeroValue);
  }

  public V getValue(BlockState state) {
    return getValue(state.getBlock().getRegistryName(), state.getTags());
  }

}
