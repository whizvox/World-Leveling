package me.whizvox.worldleveling.common.block;

import me.whizvox.worldleveling.common.api.ability.mining.IForgeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ForgeValveBlock extends Block {

  public enum Type implements StringRepresentable {
    ITEM_IN,
    ITEM_OUT,
    FUEL_IN,
    AIR_IN;
    public final String serializedName;
    Type() {
      serializedName = name().toLowerCase();
    }
    @Override
    public String getSerializedName() {
      return serializedName;
    }
  }

  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
  public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

  private static final VoxelShape ITEM_INPUT_SHAPE = Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);

  public final IForgeType forgeType;

  public ForgeValveBlock(IForgeType forgeType) {
    super(BlockBehaviour.Properties.of(Material.STONE));
    this.forgeType = forgeType;
    registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(TYPE, Type.FUEL_IN));
  }

  private static boolean isItemInput(BlockState state) {
    return state.getValue(TYPE) == Type.ITEM_IN;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, TYPE);
  }

  @Override
  public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rotation) {
    return rotate(state, rotation);
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.getValue(TYPE) == Type.ITEM_IN || state.getValue(TYPE) == Type.ITEM_OUT ? state : state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
  }

  @Override
  public boolean useShapeForLightOcclusion(BlockState state) {
    return isItemInput(state);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    if (isItemInput(state)) {
      return ITEM_INPUT_SHAPE;
    }
    return super.getShape(state, world, pos, context);
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    if (isItemInput(state)) {
      return RenderShape.MODEL;
    }
    return super.getRenderShape(state);
  }

}
