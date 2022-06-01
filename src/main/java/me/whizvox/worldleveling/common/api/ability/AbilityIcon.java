package me.whizvox.worldleveling.common.api.ability;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface AbilityIcon {

  void render(Screen screen, PoseStack stack, int x, int y);

  class ItemIcon implements AbilityIcon {
    private final Lazy<ItemStack> itemStackSup;
    public ItemIcon(Supplier<Item> itemSup, @Nullable CompoundTag tag) {
      itemStackSup = Lazy.of(() -> new ItemStack(itemSup.get(), 1, tag));
    }
    @Override
    public void render(Screen screen, PoseStack stack, int x, int y) {
      stack.pushPose();
      stack.translate(x + 8, y + 8, 0);
      stack.scale(16, -16, 0);
      MultiBufferSource.BufferSource buffer = screen.getMinecraft().renderBuffers().bufferSource();
      ItemStack itemStack = itemStackSup.get();
      BakedModel model = screen.getMinecraft().getItemRenderer().getModel(itemStack, null, null, 0);
      // FIXME Model being drawn darker than usual
      screen.getMinecraft().getItemRenderer().render(itemStack, ItemTransforms.TransformType.GUI, false, stack, buffer, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY, model);
      buffer.endBatch();
      stack.popPose();
    }
    public static final ItemIcon DEFAULT = new ItemIcon(() -> Items.BARRIER, null);
  }

  class TextureIcon implements AbilityIcon {
    private final ResourceLocation texture;
    private final int srcX, srcY, texWidth, texHeight;
    public TextureIcon(ResourceLocation texture, int srcX, int srcY, int texWidth, int texHeight) {
      this.texture = texture;
      this.srcX = srcX;
      this.srcY = srcY;
      this.texWidth = texWidth;
      this.texHeight = texHeight;
    }
    @Override
    public void render(Screen screen, PoseStack stack, int x, int y) {
      RenderSystem.setShaderTexture(0, texture);
      GuiComponent.blit(stack, x, y, srcX, srcY, 16, 16, texWidth, texHeight);
    }
  }

}
