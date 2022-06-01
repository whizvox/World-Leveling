package me.whizvox.worldleveling.common.item;

import me.whizvox.worldleveling.common.ability.ForgeTierAbility;
import me.whizvox.worldleveling.common.api.ability.mining.IForgeType;
import me.whizvox.worldleveling.common.lib.WLCreativeTab;
import me.whizvox.worldleveling.common.lib.internal.WLStrings;
import me.whizvox.worldleveling.common.lib.multiblock.MultiBlockStructure;
import me.whizvox.worldleveling.common.lib.multiblock.PlaceOptions;
import me.whizvox.worldleveling.common.util.BlockOffset;
import me.whizvox.worldleveling.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;

public class ForgeStructureItem extends Item {

  public final IForgeType forgeType;

  public ForgeStructureItem(IForgeType forgeType) {
    super(new Properties().tab(WLCreativeTab.TAB));
    this.forgeType = forgeType;
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    Player player = context.getPlayer();
    if (level.isClientSide || context.getClickedFace() != Direction.UP) {
      return InteractionResult.SUCCESS;
    }
    if (!player.isCreative()) {
      if (!ForgeTierAbility.hasUnlocked(player, forgeType)) {
        player.displayClientMessage(WLStrings.DO_NOT_KNOW, true);
        return InteractionResult.PASS;
      }
      int bricksCount = 0;
      Item bricksItem = forgeType.getBricksItem().get();
      for (ItemStack stack : context.getPlayer().getInventory().items) {
        if (stack.is(bricksItem)) {
          bricksCount += stack.getCount();
          if (bricksCount >= 24) {
            break;
          }
        }
      }
      if (bricksCount < 24) {
        player.displayClientMessage(WLStrings.formatMessage_forgeNeedMaterials(24, new ItemStack(bricksItem)), true);
        return InteractionResult.PASS;
      }
    }
    Rotation rotation = WorldUtils.getRotationFromDirection(player.getDirection());
    BlockPos centerPos = context.getClickedPos().above();
    MultiBlockStructure multiBlock = forgeType.getMultiBlock().get();
    if (multiBlock.placeWholeStructure(level, PlaceOptions.create(centerPos)
        .pivotPoint(new BlockOffset(1, 0, 1))
        .rotation(rotation)
        .build())) {
      level.playLocalSound(centerPos.getX() + 0.5, centerPos.getY() + 0.5, centerPos.getZ() + 0.5, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F, true);
      if (!player.isCreative()) {
        Item bricksItem = forgeType.getBricksItem().get();
        int remainingBricks = 24;
        for (ItemStack stack : context.getPlayer().getInventory().items) {
          if (stack.is(bricksItem)) {
            int count = stack.getCount();
            stack.shrink(remainingBricks);
            remainingBricks -= count;
            if (remainingBricks <= 0) {
              break;
            }
          }
        }
        context.getItemInHand().shrink(1);
      }
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

}
