package me.whizvox.worldleveling.common.util;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;

@FieldsAreNonnullByDefault
public record BlockOffset(int x, int y, int z) {

  public BlockOffset inv() {
    return this.equals(ZERO) ? this : new BlockOffset(-x, -y, -z);
  }

  public BlockOffset add(int xOff, int yOff, int zOff) {
    return xOff == 0 && yOff == 0 && zOff == 0 ? this : new BlockOffset(x + xOff, y + yOff, z + zOff);
  }

  public BlockOffset add(BlockOffset other) {
    return ZERO.equals(other) ? this : add(other.x, other.y, other.z);
  }

  public BlockOffset sub(int xOff, int yOff, int zOff) {
    return add(-xOff, -yOff, -zOff);
  }

  public BlockOffset sub(BlockOffset other) {
    return add(other.inv());
  }

  public BlockOffset rotate(Rotation rotation) {
    return switch (rotation) {
      case NONE -> this;
      case CLOCKWISE_90 -> new BlockOffset(x, y, -z);
      case CLOCKWISE_180 -> new BlockOffset(-x, y, -z);
      case COUNTERCLOCKWISE_90 -> new BlockOffset(-x, y, z);
    };
  }

  public BlockPos from(BlockPos pos, Rotation rotation) {
    return switch (rotation) {
      // clockwise 90 and counterclockwise 90 seem wrong, but they are correct, apparently
      case NONE -> pos.offset(x, y, z);
      case CLOCKWISE_90 -> pos.offset(-z, y, x);
      case CLOCKWISE_180 -> pos.offset(-x, y, -z);
      case COUNTERCLOCKWISE_90 -> pos.offset(z, y, -x);
    };
  }

  public Vec3i toVec() {
    return new Vec3i(x, y, z);
  }

  public static final BlockOffset ZERO = new BlockOffset(0, 0, 0);

  public static BlockOffset of(int x, int y, int z) {
    return new BlockOffset(x, y, z);
  }

}
