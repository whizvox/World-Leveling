package me.whizvox.worldleveling.common.network.message;

import me.whizvox.worldleveling.common.item.ProspectorsPickItem;
import me.whizvox.worldleveling.common.lib.WLCapabilities;
import me.whizvox.worldleveling.common.network.MessageHandler;
import me.whizvox.worldleveling.common.util.CapabilityUtils;
import me.whizvox.worldleveling.common.util.RegistryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public record SyncProspectedBlockMessage(InteractionHand hand, @Nullable Block block) {

  public static final MessageHandler<SyncProspectedBlockMessage> HANDLER = new MessageHandler<>() {

    @Override
    public Class<SyncProspectedBlockMessage> getType() {
      return SyncProspectedBlockMessage.class;
    }

    @Override
    public void encode(SyncProspectedBlockMessage msg, FriendlyByteBuf buffer) {
      buffer.writeEnum(msg.hand);
      buffer.writeBoolean(msg.block != null);
      if (msg.block != null) {
        buffer.writeResourceLocation(msg.block.getRegistryName());
      }
    }

    @Override
    public SyncProspectedBlockMessage decode(FriendlyByteBuf buffer) {
      final InteractionHand hand = buffer.readEnum(InteractionHand.class);
      final Block block;
      if (buffer.readBoolean()) {
        block = RegistryUtils.getBlock(buffer.readResourceLocation());
      } else {
        block = null;
      }
      return new SyncProspectedBlockMessage(hand, block);
    }

    @Override
    public void handle(SyncProspectedBlockMessage msg, @Nullable ServerPlayer sender) {
      ItemStack stack = Minecraft.getInstance().player.getItemInHand(msg.hand);
      if (!stack.isEmpty() && stack.getItem() instanceof ProspectorsPickItem) {
        CapabilityUtils.handle(WLCapabilities.PROSPECTED_BLOCK, stack, cap -> {
          if (msg.block == null) {
            cap.clear();
          } else {
            cap.set(msg.block.defaultBlockState());
          }
        });
      }
    }

  };

}
