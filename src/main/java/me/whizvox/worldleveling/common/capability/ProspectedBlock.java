package me.whizvox.worldleveling.common.capability;

import me.whizvox.worldleveling.common.util.RegistryUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public class ProspectedBlock implements INBTSerializable<StringTag> {

  private Block block;

  public ProspectedBlock() {
    block = null;
  }

  public boolean isSelected() {
    return block != null;
  }

  @Nullable
  public Block get() {
    return block;
  }

  public void set(BlockState selected) {
    block = selected.getBlock();
  }

  public void clear() {
    block = null;
  }

  @Override
  public StringTag serializeNBT() {
    return StringTag.valueOf(block == null ? "" : block.getRegistryName().toString());
  }

  @Override
  public void deserializeNBT(StringTag tag) {
    block = RegistryUtils.getBlock(tag.getAsString());
  }

}
