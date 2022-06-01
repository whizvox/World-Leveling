package me.whizvox.worldleveling.common.network.message;

import me.whizvox.worldleveling.client.render.ExposedBlocksRenderManager;
import me.whizvox.worldleveling.common.lib.WLCaches;
import me.whizvox.worldleveling.common.lib.WLSkills;
import me.whizvox.worldleveling.common.network.MessageHandler;
import me.whizvox.worldleveling.common.skill.MiningCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Stream;

public record ShowOresMessage(BlockPos center, int radius, @Nullable Block filter) {

  public static final MessageHandler<ShowOresMessage> HANDLER = new MessageHandler<>() {

    @Override
    public Class<ShowOresMessage> getType() {
      return ShowOresMessage.class;
    }

    @Override
    public void encode(ShowOresMessage msg, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(msg.center);
      buffer.writeShort(msg.radius);
      boolean hasFilter = msg.filter != null;
      buffer.writeBoolean(hasFilter);
      if (hasFilter) {
        buffer.writeResourceLocation(msg.filter.getRegistryName());
      }
    }

    @Override
    public ShowOresMessage decode(FriendlyByteBuf buffer) {
      BlockPos center = buffer.readBlockPos();
      int radius = buffer.readShort();
      Block filter = null;
      if (buffer.readBoolean()) {
        filter = ForgeRegistries.BLOCKS.getValue(buffer.readResourceLocation());
      }
      return new ShowOresMessage(center, radius, filter);
    }

    @Override
    public void handle(ShowOresMessage msg, @Nullable ServerPlayer sender) {
      ExposedBlocksRenderManager.INSTANCE.clear();
      LocalPlayer player = Minecraft.getInstance().player;
      final int r = msg.radius;
      HashSet<BlockPos> blocks = new HashSet<>();
      for (int xOff = -r; xOff <= r; xOff++) {
        for (int yOff = -r; yOff <= r; yOff++) {
          for (int zOff = -r; zOff <= r; zOff++) {
            BlockPos pos = msg.center.offset(xOff, yOff, zOff);
            BlockState state = player.getLevel().getBlockState(pos);
            if (msg.filter != null) {
              if (state.is(msg.filter)) {
                blocks.add(pos);
              }
            } else if (WLCaches.<MiningCache>get(WLSkills.MINING.get()).shouldProspectorHighlight(state)) {
              blocks.add(pos);
            }
          }
        }
      }
      Stream<BlockPos> stream;
      if (blocks.size() <= 200) {
        stream = blocks.stream();
      } else {
        // limit to 200 closest blocks if necessary (lag prevention)
        stream = blocks.stream()
            .sorted(Comparator.comparingDouble(o -> o.distSqr(msg.center)))
            .limit(200);
      }
      ExposedBlocksRenderManager.INSTANCE.add(msg.filter != null ? 0x0000FF : 0x00FF00, stream);
    }

  };

}
