package me.whizvox.worldleveling.common.lib.multiblock;

import me.whizvox.worldleveling.common.util.BlockOffset;
import me.whizvox.worldleveling.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class MultiBlockStructure {

  private final List<BlockEntry> blocks;
  private final List<BlockState> palette;
  private final Vec3i size;

  private final BlockOffset corner1, corner2;

  public MultiBlockStructure(List<BlockEntry> blocks, List<BlockState> palette, Vec3i size) {
    this.blocks = Collections.unmodifiableList(blocks);
    this.palette = Collections.unmodifiableList(palette);
    this.size = size;

    int c1x = Integer.MAX_VALUE;
    int c1y = Integer.MAX_VALUE;
    int c1z = Integer.MAX_VALUE;
    int c2x = Integer.MIN_VALUE;
    int c2y = Integer.MIN_VALUE;
    int c2z = Integer.MIN_VALUE;
    for (BlockEntry entry : this.blocks) {
      if (entry.offset.x() < c1x) {
        c1x = entry.offset.x();
      }
      if (entry.offset.y() < c1y) {
        c1y = entry.offset.y();
      }
      if (entry.offset.z() < c1z) {
        c1z = entry.offset.z();
      }
      if (entry.offset.x() > c2x) {
        c2x = entry.offset.x();
      }
      if (entry.offset.y() > c2y) {
        c2y = entry.offset.y();
      }
      if (entry.offset.z() > c2z) {
        c2z = entry.offset.z();
      }
    }
    corner1 = new BlockOffset(c1x, c1y, c1z);
    corner2 = new BlockOffset(c2x, c2y, c2z);
  }

  private static boolean blockObstructionExists(LevelAccessor world, BlockPos pos) {
    return !world.hasChunkAt(pos) || pos.getY() > world.getMaxBuildHeight() ||
        pos.getY() < world.getMinBuildHeight() || !world.getBlockState(pos).isAir();
  }

  private static List<? extends Entity> findEntityObstructions(LevelAccessor world, BlockPos pos) {
    return world.getEntitiesOfClass(LivingEntity.class, new AABB(pos));
  }

  private void forEachResolvedBlock(LevelAccessor world, PlaceOptions options, Collection<ResolvedBlockEntry> resolvedBlocks, Collection<BlockPos> blockObstructions, Collection<Entity> entityObstructions) {
    blocks.forEach(entry -> {
      BlockPos pos = entry.offset.sub(options.pivotPoint()).from(options.centerPos(), options.rotation());
      if (resolvedBlocks != null) {
        resolvedBlocks.add(new ResolvedBlockEntry(pos, palette.get(entry.state).rotate(world, pos, options.rotation()), entry.nbt));
      }
      if (blockObstructions != null) {
        if (blockObstructionExists(world, pos)) {
          blockObstructions.add(pos);
        }
      }
      if (entityObstructions != null) {
        entityObstructions.addAll(findEntityObstructions(world, pos));
      }
    });
  }

  public ResolvedStructure resolveStructure(LevelAccessor world, PlaceOptions options) {
    Collection<ResolvedBlockEntry> resolvedBlocks = new ArrayList<>(blocks.size());
    forEachResolvedBlock(world, options, resolvedBlocks, null, null);
    return new ResolvedStructure(resolvedBlocks);
  }

  public ObstructionsReport findObstructions(LevelAccessor world, PlaceOptions options) {
    Collection<BlockPos> blockObstructions = new ArrayList<>();
    Collection<Entity> entityObstructions = new ArrayList<>();
    forEachResolvedBlock(world, options, null, blockObstructions, entityObstructions);
    return new ObstructionsReport(blockObstructions, entityObstructions);
  }

  public StructurePlaceResults calculatePlaceResults(LevelAccessor world, PlaceOptions options) {
    Collection<ResolvedBlockEntry> resolvedBlocks = new ArrayList<>(blocks.size());
    Collection<BlockPos> blockObstructions = new ArrayList<>();
    Collection<Entity> entityObstructions = new ArrayList<>();
    forEachResolvedBlock(world, options, resolvedBlocks, blockObstructions, entityObstructions);
    return new StructurePlaceResults(
        new ResolvedStructure(resolvedBlocks),
        new ObstructionsReport(blockObstructions, entityObstructions)
    );
  }

  public Vec3i getSize() {
    return size;
  }

  public BlockOffset getFirstCorner() {
    return corner1;
  }

  public BlockPos resolveFirstCorner(PlaceOptions options) {
    return corner1.sub(options.pivotPoint()).from(options.centerPos(), options.rotation());
  }

  public BlockOffset getSecondCorner() {
    return corner2;
  }

  public BlockPos resolveSecondCorner(PlaceOptions options) {
    return corner2.sub(options.pivotPoint()).from(options.centerPos(), options.rotation());
  }

  public AABB resolveBoundingBox(PlaceOptions options) {
    BlockPos p1 = resolveFirstCorner(options);
    BlockPos p2 = resolveSecondCorner(options);
    double x1 = p1.getX();
    double y1 = p1.getY();
    double z1 = p1.getZ();
    double x2 = p2.getX() + (p2.getX() > p1.getX() ? 1 : -1);
    double y2 = p2.getY() + 1;
    double z2 = p2.getZ() + (p2.getZ() > p1.getZ() ? 1 : -1);
    return new AABB(x1, y1, z1, x2, y2, z2);
  }

  public static MultiBlockStructure load(CompoundTag rootTag) {
    List<BlockEntry> blocks = new ArrayList<>();
    List<BlockState> palette = new ArrayList<>();
    Vec3i size;
    int dataVersion;

    ListTag sizeTag = rootTag.getList("size", Tag.TAG_INT);
    size = new Vec3i(sizeTag.getInt(0), sizeTag.getInt(1), sizeTag.getInt(2));
    ListTag blocksTag = rootTag.getList("blocks", Tag.TAG_COMPOUND);
    blocksTag.forEach(tag -> {
      CompoundTag blockTag = (CompoundTag) tag;
      ListTag posTag = blockTag.getList("pos", Tag.TAG_INT);
      BlockOffset offset = new BlockOffset(posTag.getInt(0), posTag.getInt(1), posTag.getInt(2));
      int state = blockTag.getInt("state");
      CompoundTag nbt;
      if (blockTag.contains("nbt")) {
        nbt = blockTag.getCompound("nbt");
      } else {
        nbt = null;
      }
      blocks.add(new BlockEntry(state, offset, nbt));
    });
    ListTag paletteTag = rootTag.getList("palette", Tag.TAG_COMPOUND);
    paletteTag.forEach(tag -> {
      palette.add(NbtUtils.readBlockState((CompoundTag) tag));
    });
    dataVersion = rootTag.getInt("DataVersion");

    return new MultiBlockStructure(blocks, palette, size);
  }

  public boolean canPlace(LevelAccessor world, PlaceOptions options) {
    return calculatePlaceResults(world, options).obstructions.isEmpty();
  }

  public boolean placeWholeStructure(LevelAccessor world, PlaceOptions options) {
    StructurePlaceResults res = calculatePlaceResults(world, options);
    if (!res.obstructions.isEmpty()) {
      return false;
    }
    res.structure.blocks.forEach(entry -> {
      WorldUtils.setBlock(world, entry.pos, entry.state, entry.nbt, options.updateFlags());
    });
    return true;
  }

  public record BlockEntry(int state, BlockOffset offset, @Nullable CompoundTag nbt) {
  }

  public record ResolvedBlockEntry(BlockPos pos, BlockState state, @Nullable CompoundTag nbt) {
  }

  public record ResolvedStructure(Collection<ResolvedBlockEntry> blocks) {

    public ResolvedStructure(Collection<ResolvedBlockEntry> blocks) {
      this.blocks = Collections.unmodifiableCollection(blocks);
    }

    public ObstructionsReport findObstructions(LevelAccessor world) {
      Collection<BlockPos> blockObstructions = new ArrayList<>();
      Collection<Entity> entityObstructions = new ArrayList<>();
      blocks.forEach(entry -> {
        if (blockObstructionExists(world, entry.pos)) {
          blockObstructions.add(entry.pos);
        }
        entityObstructions.addAll(findEntityObstructions(world, entry.pos));
      });
      return new ObstructionsReport(blockObstructions, entityObstructions);
    }

    public Collection<BlockPos> findMismatches(LevelAccessor world) {
      Collection<BlockPos> mismatches = new ArrayList<>();
      for (ResolvedBlockEntry entry : blocks) {
        if (world.getBlockState(entry.pos) != entry.state) {
          mismatches.add(entry.pos);
        }
      }
      return Collections.unmodifiableCollection(mismatches);
    }

  }

  public record ObstructionsReport(Collection<BlockPos> blocks, Collection<Entity> entities) {

    public ObstructionsReport(Collection<BlockPos> blocks, Collection<Entity> entities) {
      this.blocks = Collections.unmodifiableCollection(blocks);
      this.entities = Collections.unmodifiableCollection(entities);
    }

    public boolean isEmpty() {
      return blocks.isEmpty() && entities.isEmpty();
    }

  }

  public record StructurePlaceResults(ResolvedStructure structure, ObstructionsReport obstructions) {
  }

}
