package me.whizvox.worldleveling.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ExposedBlocksRenderManager {

  public static final ExposedBlocksRenderManager INSTANCE = new ExposedBlocksRenderManager();

  public static void register(IEventBus bus) {
    bus.register(INSTANCE);
  }

  private static final int
      LIFESPAN_START = 200,
      LIFESPAN_UNTIL_FADE_OUT = 80,
      OPACITY_START = 0x60;
  private static final float
      SIZE_START = 0.85F;

  private final List<BlockPos> blocks;
  private int lifespan;
  private int color;
  private float size;

  public ExposedBlocksRenderManager() {
    blocks = new ArrayList<>();
    lifespan = 0;
    color = 0;
    size = 0;
  }

  public void add(int color, Stream<BlockPos> positions) {
    lifespan = LIFESPAN_START;
    this.color = color | (OPACITY_START << 24);
    size = SIZE_START;
    positions.forEach(blocks::add);
  }

  public void clear() {
    blocks.clear();
  }

  @SubscribeEvent
  public void onRenderWorld(final RenderLevelLastEvent event) {
    if (lifespan <= 0) {
      return;
    }

    PoseStack stack = event.getPoseStack();
    Vec3 projView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
    stack.pushPose();
    stack.translate(-projView.x, -projView.y, -projView.z);
    RenderSystem.setShader(GameRenderer::getPositionColorShader);
    RenderSystem.disableDepthTest();
    RenderSystem.disableCull();
    RenderSystem.enableBlend();
    blocks.forEach(pos -> {
      final float c1 = 0.5F - (size / 2.0F);
      final float c2 = 0.5F + (size / 2.0F);
      stack.pushPose();
      stack.translate(pos.getX(), pos.getY(), pos.getZ());
      Matrix4f pose = stack.last().pose();
      Tesselator t = Tesselator.getInstance();
      BufferBuilder builder = t.getBuilder();
      builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      builder.vertex(pose, c1, c1, c1).color(color).endVertex();
      builder.vertex(pose, c2, c1, c1).color(color).endVertex();
      builder.vertex(pose, c2, c2, c1).color(color).endVertex();
      builder.vertex(pose, c1, c2, c1).color(color).endVertex();

      builder.vertex(pose, c1, c1, c2).color(color).endVertex();
      builder.vertex(pose, c2, c1, c2).color(color).endVertex();
      builder.vertex(pose, c2, c2, c2).color(color).endVertex();
      builder.vertex(pose, c1, c2, c2).color(color).endVertex();

      builder.vertex(pose, c1, c1, c1).color(color).endVertex();
      builder.vertex(pose, c1, c1, c2).color(color).endVertex();
      builder.vertex(pose, c1, c2, c2).color(color).endVertex();
      builder.vertex(pose, c1, c2, c1).color(color).endVertex();

      builder.vertex(pose, c2, c1, c1).color(color).endVertex();
      builder.vertex(pose, c2, c1, c2).color(color).endVertex();
      builder.vertex(pose, c2, c2, c2).color(color).endVertex();
      builder.vertex(pose, c2, c2, c1).color(color).endVertex();

      builder.vertex(pose, c1, c1, c1).color(color).endVertex();
      builder.vertex(pose, c1, c1, c2).color(color).endVertex();
      builder.vertex(pose, c2, c1, c2).color(color).endVertex();
      builder.vertex(pose, c2, c1, c1).color(color).endVertex();

      builder.vertex(pose, c1, c2, c1).color(color).endVertex();
      builder.vertex(pose, c1, c2, c2).color(color).endVertex();
      builder.vertex(pose, c2, c2, c2).color(color).endVertex();
      builder.vertex(pose, c2, c2, c1).color(color).endVertex();
      t.end();
      stack.popPose();
    });
    stack.popPose();
    RenderSystem.disableBlend();
    RenderSystem.enableDepthTest();
    RenderSystem.enableCull();

    lifespan--;
    if (lifespan < LIFESPAN_UNTIL_FADE_OUT) {
      color = (color & 0xFFFFFF) | ((int) ((OPACITY_START * ((float) lifespan / LIFESPAN_UNTIL_FADE_OUT))) << 24);
      size = SIZE_START * ((float) lifespan / LIFESPAN_UNTIL_FADE_OUT);
    }
    if (lifespan <= 0) {
      blocks.clear();
    }
  }

}
