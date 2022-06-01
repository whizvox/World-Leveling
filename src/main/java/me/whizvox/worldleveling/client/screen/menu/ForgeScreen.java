package me.whizvox.worldleveling.client.screen.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.whizvox.worldleveling.common.block.entity.ForgeInterfaceBlockEntity;
import me.whizvox.worldleveling.common.inventory.menu.ForgeMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class ForgeScreen extends AbstractContainerScreen<ForgeMenu> {

  private static final ResourceLocation TEXTURE = new ResourceLocation("worldleveling", "textures/gui/forge/screen.png");

  public ForgeScreen(ForgeMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
  }

  @Override
  protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, TEXTURE);
    blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    if (menu.getBurnProgress() > 0.0F) {
      int h = Mth.ceil(14 * menu.getBurnProgress());
      blit(stack, leftPos + 39, topPos + 50 - h, 176, 14 - h, 14, h);
    }
    if (menu.getSmeltProgress() > 0.0F) {
      int w = Mth.ceil(24 * menu.getSmeltProgress());
      blit(stack, leftPos + 79, topPos + 35, 176, 14, w, 17);
    }
    if (menu.getTemperature() > 0) {
      int h = Mth.ceil(60 * ((float) menu.getTemperature() / menu.getMaxTemperature()));
      int srcX = menu.getTemperature() < ForgeInterfaceBlockEntity.MIN_SMELTING_TEMPERATURE ? 176 : 180;
      blit(stack, leftPos + 156, topPos + 73 - h, srcX, 105 - h, 4, h);
    }
  }

  @Override
  public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
    super.renderBackground(stack);
    super.render(stack, mouseX, mouseY, partialTicks);
    renderTooltip(stack, mouseX, mouseY);
  }

  @Override
  protected void renderTooltip(PoseStack stack, int mouseX, int mouseY) {
    super.renderTooltip(stack, mouseX, mouseY);
    if (mouseX >= leftPos + 156 && mouseX <= leftPos + 160 && mouseY >= topPos + 13 && mouseY <= topPos + 73) {
      renderTooltip(stack, new TranslatableComponent("gui.worldleveling.forge.temperature", menu.getTemperature()), mouseX, mouseY);
    }
  }

}
