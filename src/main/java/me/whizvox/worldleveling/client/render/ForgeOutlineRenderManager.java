package me.whizvox.worldleveling.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import me.whizvox.worldleveling.common.ability.ForgeTierAbility;
import me.whizvox.worldleveling.common.api.ability.mining.IForgeType;
import me.whizvox.worldleveling.common.item.ForgeStructureItem;
import me.whizvox.worldleveling.common.lib.multiblock.MultiBlockStructure;
import me.whizvox.worldleveling.common.lib.multiblock.PlaceOptions;
import me.whizvox.worldleveling.common.util.BlockOffset;
import me.whizvox.worldleveling.common.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeOutlineRenderManager {

  public static final ForgeOutlineRenderManager INSTANCE = new ForgeOutlineRenderManager();

  public static void register(IEventBus bus) {
    bus.register(INSTANCE);
  }

  public ForgeOutlineRenderManager() {
  }

  @SubscribeEvent
  public void onRenderWorld(final RenderLevelLastEvent event) {
    Player player = Minecraft.getInstance().player;
    Level world = player.level;
    ItemStack item = ItemStack.EMPTY;
    if (player.getMainHandItem().getItem() instanceof ForgeStructureItem) {
      item = player.getMainHandItem();
    } else if (player.getOffhandItem().getItem() instanceof ForgeStructureItem) {
      item = player.getOffhandItem();
    }
    if (!item.isEmpty()) {
      IForgeType forgeType = ((ForgeStructureItem) item.getItem()).forgeType;
      if (player.isCreative() || ForgeTierAbility.hasUnlocked(player, forgeType)) {
        BlockHitResult hitRes = WorldUtils.rayTraceBlock(world, player, 5.0);
        if (hitRes.getType() != HitResult.Type.MISS && hitRes.getDirection() == Direction.UP) {
          BlockPos centerPos = hitRes.getBlockPos().above();
          Rotation rotation = WorldUtils.getRotationFromDirection(player.getDirection());
          MultiBlockStructure multiBlock = forgeType.getMultiBlock().get();
          PlaceOptions options = PlaceOptions.create(centerPos).pivotPoint(new BlockOffset(1, 0, 1)).rotation(rotation).build();
          MultiBlockStructure.StructurePlaceResults placeRes = multiBlock.calculatePlaceResults(world, options);
          int mainColor = placeRes.obstructions().isEmpty() ? 0xFFFFFFFF : 0xFF000000;
          PoseStack stack = event.getPoseStack();
          Vec3 projView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
          stack.pushPose();
          stack.translate(-projView.x, -projView.y, -projView.z);
          RenderSystem.setShader(GameRenderer::getPositionColorShader);
          RenderSystem.disableDepthTest();
          RenderSystem.disableCull();

          Matrix4f pose = stack.last().pose();
          Tesselator tess = Tesselator.getInstance();
          BufferBuilder builder = tess.getBuilder();
          builder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

          placeRes.structure().blocks().forEach(entry -> {
            drawBoxWireframe(pose, builder, entry.pos().getX(), entry.pos().getY(), entry.pos().getZ(), 1.0F, 1.0F, 1.0F, mainColor);
          });

          tess.end();

          if (!placeRes.obstructions().blocks().isEmpty()) {
            builder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
            placeRes.obstructions().blocks().forEach(pos -> {
              drawBoxWireframe(pose, builder, pos.getX(), pos.getY(), pos.getZ(), 1.0F, 1.0F, 1.0F, 0xFFFF0000);
            });
            tess.end();
          }
          if (!placeRes.obstructions().entities().isEmpty()) {
            builder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
            placeRes.structure().blocks().forEach(entry -> {
              boolean intersects = false;
              AABB blockBoundingBox = new AABB(entry.pos());
              for (Entity entity : placeRes.obstructions().entities()) {
                if (entity.getBoundingBox().intersects(blockBoundingBox)) {
                  intersects = true;
                  break;
                }
              }
              if (intersects) {
                drawBoxWireframe(pose, builder, entry.pos().getX(), entry.pos().getY(), entry.pos().getZ(), 1.0F, 1.0F, 1.0F, 0xFFFF0000);
              }
            });
            tess.end();
          }

          stack.popPose();
          RenderSystem.enableDepthTest();
          RenderSystem.enableCull();
        }
      }
    }
  }

  private static void drawBoxWireframe(Matrix4f pose, BufferBuilder builder, float x, float y, float z, float width, float height, float depth, int color) {
    final float x2 = x + 1.0F;
    final float y2 = y + 1.0F;
    final float z2 = z + 1.0F;

    // bottom
    builder.vertex(pose, x, y, z).color(color).endVertex();
    builder.vertex(pose, x2, y, z).color(color).endVertex();
    builder.vertex(pose, x, y, z2).color(color).endVertex();
    builder.vertex(pose, x2, y, z2).color(color).endVertex();
    builder.vertex(pose, x, y, z).color(color).endVertex();
    builder.vertex(pose, x, y, z2).color(color).endVertex();
    builder.vertex(pose, x2, y, z).color(color).endVertex();
    builder.vertex(pose, x2, y, z2).color(color).endVertex();

    // sides
    builder.vertex(pose, x, y, z).color(color).endVertex();
    builder.vertex(pose, x, y2, z).color(color).endVertex();
    builder.vertex(pose, x2, y, z).color(color).endVertex();
    builder.vertex(pose, x2, y2, z).color(color).endVertex();
    builder.vertex(pose, x, y, z2).color(color).endVertex();
    builder.vertex(pose, x, y2, z2).color(color).endVertex();
    builder.vertex(pose, x2, y, z2).color(color).endVertex();
    builder.vertex(pose, x2, y2, z2).color(color).endVertex();

    // top
    builder.vertex(pose, x, y2, z).color(color).endVertex();
    builder.vertex(pose, x2, y2, z).color(color).endVertex();
    builder.vertex(pose, x, y2, z2).color(color).endVertex();
    builder.vertex(pose, x2, y2, z2).color(color).endVertex();
    builder.vertex(pose, x, y2, z).color(color).endVertex();
    builder.vertex(pose, x, y2, z2).color(color).endVertex();
    builder.vertex(pose, x2, y2, z).color(color).endVertex();
    builder.vertex(pose, x2, y2, z2).color(color).endVertex();
  }

}
