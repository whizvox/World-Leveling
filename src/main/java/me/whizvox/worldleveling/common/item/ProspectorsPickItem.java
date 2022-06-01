package me.whizvox.worldleveling.common.item;

import me.whizvox.worldleveling.common.ability.ProspectorAbility;
import me.whizvox.worldleveling.common.capability.ProspectedBlock;
import me.whizvox.worldleveling.common.capability.ProspectedBlocksCapabilityProvider;
import me.whizvox.worldleveling.common.lib.WLCreativeTab;
import me.whizvox.worldleveling.common.lib.WLItems;
import me.whizvox.worldleveling.common.lib.WLSounds;
import me.whizvox.worldleveling.common.network.WLNetwork;
import me.whizvox.worldleveling.common.network.message.ShowOresMessage;
import me.whizvox.worldleveling.common.network.message.SyncProspectedBlockMessage;
import me.whizvox.worldleveling.common.util.CapabilityUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public class ProspectorsPickItem extends PickaxeItem {

  public ProspectorsPickItem() {
    super(
        new ForgeTier(2, 500, 6.0F, 2.0F, 1, BlockTags.NEEDS_IRON_TOOL, () -> Ingredient.of(WLItems.SOOTY_ALLOY_INGOT.get())),
        1, -2.8F,
        new Item.Properties()
            .tab(WLCreativeTab.TAB)
            .stacksTo(1)
            .durability(500)
    );
  }

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
    return new ProspectedBlocksCapabilityProvider();
  }

  // if sneaking:
  //   if looking at block:
  //     set block filter
  //   else:
  //     clear block filter
  // else:
  //   if looking at block:
  //     view ores
  @Override
  public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
    final ItemStack stack = player.getItemInHand(hand);
    if (!world.isClientSide) {
      AtomicReference<Boolean> actionTaken = new AtomicReference<>(false);
      Vec3 look = player.getLookAngle();
      Vec3 start = new Vec3(player.getX(), player.getEyeY(), player.getZ());
      Vec3 end = new Vec3(start.x + look.x * 5, start.y + look.y * 5, start.z + look.z * 5);
      ClipContext clip = new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
      BlockHitResult result = world.clip(clip);
      CapabilityUtils.handlePlayerSkills(player, skills -> {
        ProspectorAbility.highestLevel(skills).ifPresent(ability -> {
          if (player.isCrouching()) {
            if (ability.level > 1) {
              if (result.getType() == HitResult.Type.MISS) {
                CapabilityUtils.handleProspectedBlock(stack, ProspectedBlock::clear);
                player.displayClientMessage(new TranslatableComponent("message.worldleveling.ability.mining.prospector.ores"), true);
                WLNetwork.sendToClient((ServerPlayer) player, new SyncProspectedBlockMessage(hand, null));
                actionTaken.set(true);
              } else if (result.getType() == HitResult.Type.BLOCK) {
                BlockState blockState = world.getBlockState(result.getBlockPos());
                CapabilityUtils.handleProspectedBlock(stack, block -> block.set(blockState));
                player.displayClientMessage(new TranslatableComponent("message.worldleveling.ability.mining.prospector.singleBlock", blockState.getBlock().getName()), true);
                WLNetwork.sendToClient((ServerPlayer) player, new SyncProspectedBlockMessage(hand, blockState.getBlock()));
                actionTaken.set(true);
              }
            }
          } else {
            if (result.getType() == HitResult.Type.BLOCK) {
              int radius = 5;
              AtomicReference<Block> filter = new AtomicReference<>(null);
              switch (ability.level) {
                case 2 -> {
                  radius = 15;
                  CapabilityUtils.handleProspectedBlock(stack, block -> filter.set(block.get()));
                }
                case 3 -> {
                  radius = 30;
                  CapabilityUtils.handleProspectedBlock(stack, block -> filter.set(block.get()));
                }
              }
              stack.hurt(1, world.getRandom(), (ServerPlayer) player);
              WLNetwork.sendToClient((ServerPlayer) player, new ShowOresMessage(result.getBlockPos(), radius, filter.get()));
              player.playSound(WLSounds.PROSPECTORS_PICK.get(), 1.0F, world.random.nextFloat() * 0.4F + 0.8F);
              actionTaken.set(true);
            }
          }
        });
      });
      return actionTaken.get() ? InteractionResultHolder.success(stack) : InteractionResultHolder.fail(stack);
    }
    return InteractionResultHolder.pass(stack);
  }

}
