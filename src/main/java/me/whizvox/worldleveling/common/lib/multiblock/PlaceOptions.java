package me.whizvox.worldleveling.common.lib.multiblock;

import me.whizvox.worldleveling.common.util.BlockOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;

public record PlaceOptions(BlockPos centerPos,
                           BlockOffset pivotPoint,
                           Rotation rotation,
                           int updateFlags) {

  public static Builder create(BlockPos centerPos) {
    return new Builder(centerPos);
  }

  public static final class Builder {

    private final BlockPos centerPos;
    private BlockOffset pivotPoint;
    private Rotation rotation;
    private int updateFlags;

    private Builder(BlockPos centerPos) {
      this.centerPos = centerPos;
      pivotPoint = BlockOffset.ZERO;
      rotation = Rotation.NONE;
      updateFlags = Block.UPDATE_ALL;
    }

    public Builder pivotPoint(BlockOffset val) {
      pivotPoint = val;
      return this;
    }

    public Builder rotation(Rotation val) {
      rotation = val;
      return this;
    }

    public Builder updateFlags(int val) {
      updateFlags = val;
      return this;
    }

    public PlaceOptions build() {
      return new PlaceOptions(centerPos, pivotPoint, rotation, updateFlags);
    }

  }
}
