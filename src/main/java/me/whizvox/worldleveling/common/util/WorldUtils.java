package me.whizvox.worldleveling.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WorldUtils {

  public static BlockHitResult rayTraceBlock(BlockGetter world, Player player, double reach) {
    Vec3 look = player.getLookAngle();
    Vec3 start = new Vec3(player.getX(), player.getEyeY(), player.getZ());
    Vec3 end = new Vec3(start.x + look.x * reach, start.y + look.y * reach, start.z + look.z * reach);
    return world.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
  }

  public static boolean setBlock(LevelAccessor world, BlockPos pos, BlockState state, @Nullable CompoundTag nbt, int updateFlags) {
    boolean ret = world.setBlock(pos, state, updateFlags);
    if (ret && nbt != null) {
      BlockEntity ent = world.getBlockEntity(pos);
      if (ent != null) {
        ent.load(nbt);
      }
    }
    return ret;
  }

  public static boolean setBlock(Level world, BlockPos pos, BlockState state, @Nullable CompoundTag nbt) {
    return setBlock(world, pos, state, nbt, Block.UPDATE_ALL);
  }

    public static Rotation getRotationFromDirection(Direction direction) {
    return switch (direction) {
      case EAST -> Rotation.CLOCKWISE_90;
      case SOUTH -> Rotation.CLOCKWISE_180;
      case WEST -> Rotation.COUNTERCLOCKWISE_90;
      default -> Rotation.NONE;
    };
  }

}
