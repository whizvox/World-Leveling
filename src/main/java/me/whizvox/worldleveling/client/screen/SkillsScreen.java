package me.whizvox.worldleveling.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import me.whizvox.worldleveling.WorldLeveling;
import me.whizvox.worldleveling.common.api.Skill;
import me.whizvox.worldleveling.common.api.ability.Ability;
import me.whizvox.worldleveling.common.api.ability.AbilityIcon;
import me.whizvox.worldleveling.common.capability.PlayerSkills;
import me.whizvox.worldleveling.common.lib.WLRegistries;
import me.whizvox.worldleveling.common.lib.internal.WLStrings;
import me.whizvox.worldleveling.common.network.WLNetwork;
import me.whizvox.worldleveling.common.network.message.UpdateAbilityMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class SkillsScreen extends Screen {

  private static final int
      TAB_START_Y = 20,
      TAB_SPACING = 22,
      TAB_MAX_DISPLAY = 11,
      PAGE_START_X = 27,
      PAGE_START_Y = 19,
      PAGE_WIDTH = 216,
      PAGE_HEIGHT = 229,
      PAGE_END_X = PAGE_START_X + PAGE_WIDTH,
      PAGE_END_Y = PAGE_START_Y + PAGE_HEIGHT,
      PAGE_CENTER_X = PAGE_START_X + PAGE_WIDTH / 2,
      PAGE_CENTER_Y = PAGE_START_Y + PAGE_HEIGHT / 2,
      DRAW_UPDATE_INTERVAL = 40;

  private static final ResourceLocation
      TEX_SCREEN = new ResourceLocation(WorldLeveling.MOD_ID, "textures/gui/skills/screen.png");

  private final PlayerSkills playerSkills;
  private int selectedSkillIndex;
  private ResourceLocation selectedSkillName;
  private int tabOffset;
  private Tab[] tabs;

  private int guiLeft, guiTop;
  private boolean isScrolling;

  public SkillsScreen(PlayerSkills playerSkills, @Nullable ResourceLocation selectedSkillName) {
    super(TextComponent.EMPTY);
    this.playerSkills = playerSkills;
    this.selectedSkillName = selectedSkillName;
    selectedSkillIndex = 0;
    tabOffset = 0;
    isScrolling = false;
  }

  private void setSelectedTab(Tab tab) {
    ResourceLocation skillName = tab.skill == null ? null : tab.skill.getType().getRegistryName();
    if (Objects.equals(skillName, selectedSkillName)) {
      return;
    }
    selectedSkillName = skillName;
    for (int i = 0; i < tabs.length; i++) {
      if (tabs[i] == tab) {
        selectedSkillIndex = i;
        tabs[i].selected = true;
      } else {
        tabs[i].selected = false;
      }
    }
  }

  private void scroll(boolean down) {
    if (down) {
      if (tabOffset + TAB_MAX_DISPLAY < tabs.length) {
        tabOffset++;
      } else {
        return;
      }
    } else {
      if (tabOffset > 0) {
        tabOffset--;
      } else {
        return;
      }
    }
    for (Tab tab : tabs) {
      removeWidget(tab);
    }
    for (int i = tabOffset; i < tabOffset + TAB_MAX_DISPLAY; i++) {
      tabs[i].y = TAB_START_Y + TAB_SPACING * i;
      addWidget(tabs[i]);
    }
  }

  @Override
  protected void init() {
    guiLeft = (width - 252) / 2;
    guiTop = (height - 256) / 2;

    var skills = playerSkills.allSkills().sorted(Comparator.comparing(skill -> skill.getType().getRegistryName())).toList();
    tabs = new Tab[skills.size() + 1];
    tabs[0] = new Tab(guiLeft + 1, guiTop + TAB_START_Y, null);
    if (selectedSkillName == null) {
      tabs[0].selected = true;
    }
    addRenderableWidget(tabs[0]);
    for (int i = 1; i < tabs.length; i++) {
      Skill skill = skills.get(i - 1);
      tabs[i] = new Tab(guiLeft + 1, guiTop + TAB_START_Y + TAB_SPACING * i, skill);
      if (Objects.equals(tabs[i].skill.getType().getRegistryName(), selectedSkillName)) {
        tabs[i].selected = true;
      }
      if (i < TAB_MAX_DISPLAY) {
        addRenderableWidget(tabs[i]);
      }
    }
  }

  @Override
  public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
    super.renderBackground(stack);
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, TEX_SCREEN);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    tabs[selectedSkillIndex].renderPage(stack, mouseX, mouseY, partialTicks);
    RenderSystem.enableBlend();
    RenderSystem.setShaderTexture(0, TEX_SCREEN);
    blit(stack, guiLeft + 20, guiTop, 0, 0, 232, 256);
    drawString(stack, font, tabs[selectedSkillIndex].getMessage(), guiLeft + 30, guiTop + 6, 0xFFFFFF);
    super.render(stack, mouseX, mouseY, partialTicks);
    if (GLFW.glfwGetKey(minecraft.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL) != GLFW.GLFW_PRESS && mouseX >= guiLeft + PAGE_START_X && mouseX <= guiLeft + PAGE_END_X && mouseY >= guiTop + PAGE_START_Y && mouseY <= guiTop + PAGE_END_Y) {
      Tab selectedTab = tabs[selectedSkillIndex];
      stack.pushPose();
      stack.translate(guiLeft + selectedTab.scrollX + PAGE_CENTER_X, guiTop + selectedTab.scrollY + PAGE_CENTER_Y, 0);
      selectedTab.abilities.forEach(ability -> ability.renderToolTip(stack, mouseX, mouseY));
      if (selectedTab.selectedAbility != null) {
        selectedTab.btnConfirm.renderToolTip(stack, mouseX, mouseY);
        selectedTab.btnCancel.renderToolTip(stack, mouseX, mouseY);
      }
      stack.popPose();
    }
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    if (button == 1) {
      if (!isScrolling) {
        isScrolling = true;
      }
      tabs[selectedSkillIndex].scroll(deltaX, deltaY);
      return true;
    }
    return false;
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (button == 1) {
      isScrolling = false;
      return true;
    }
    return false;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (mouseX > guiLeft + PAGE_START_X && mouseX < guiLeft + PAGE_END_X && mouseY > guiTop + PAGE_START_Y && mouseY < guiTop + PAGE_END_Y) {
      if (button == 2) {
        tabs[selectedSkillIndex].resetScroll();
      } else if (button == 0 && !isScrolling) {
        Tab tab = tabs[selectedSkillIndex];
        int adjMouseX = (int) (mouseX - (guiLeft + tab.scrollX + PAGE_CENTER_X));
        int adjMouseY = (int) (mouseY - (guiTop + tab.scrollY + PAGE_CENTER_Y));
        tab.abilities.forEach(sticker -> {
          sticker.updateIsHovered(adjMouseX, adjMouseY);
          if (sticker.getIsHovered()) {
            sticker.onPress();
          }
        });
        if (tab.btnConfirm != null) {
          tab.btnConfirm.updateIsHovered(adjMouseX, adjMouseY);
          if (tab.btnConfirm.getIsHovered()) {
            tab.btnConfirm.onPress();
          }
        }
        if (tab.btnCancel != null) {
          tab.btnCancel.updateIsHovered(adjMouseX, adjMouseY);
          if (tab.btnCancel.getIsHovered()) {
            tab.btnCancel.onPress();
          }
        }
      }
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  private class Tab extends AbstractButton {

    final Skill skill;
    final ResourceLocation icon;
    final List<AbilitySticker> abilities;
    double scrollX, scrollY;
    Ability selectedAbility;
    boolean purchaseSelectedAbility;
    AbilityButton btnConfirm, btnCancel;
    int timer;

    boolean selected;

    public Tab(int x, int y, @Nullable Skill skill) {
      super(x, y, 20, 20, skill == null ? WLStrings.GUI_SKILLS_OVERVIEW : skill.getType().getTranslatedName());
      this.skill = skill;

      if (skill == null) {
        icon = null;
      } else {
        ResourceLocation name = skill.getType().getRegistryName();
        icon = new ResourceLocation(name.getNamespace(), "textures/wlskill/" + name.getPath() + ".png");
      }
      if (skill != null) {
        abilities = WLRegistries.ABILITIES.get().getValues().stream()
            .filter(ability -> Objects.equals(ability.getSkill(), skill.getType()))
            .map(ability -> new AbilitySticker(this, ability)).toList();
        calculateStates();
      } else {
        abilities = Collections.emptyList();
      }
      resetScroll();
      selected = false;
      selectedAbility = null;
      purchaseSelectedAbility = false;
      btnConfirm = null;
      btnCancel = null;
      timer = 0;
    }

    private void calculateStates() {
      abilities.forEach(sticker -> {
        Ability ability = sticker.ability;
        if (playerSkills.hasAbility(ability)) {
          sticker.state = AbilityStickerState.PURCHASED;
        } else {
          boolean missingDependency = ability.getDependencies().stream().anyMatch(dep -> !playerSkills.hasAbility(dep));
          boolean tooExpensive = skill.getAbilityPoints() < ability.getCost();
          if (missingDependency) {
            sticker.state = AbilityStickerState.LOCKED;
          } else if (tooExpensive) {
            sticker.state = AbilityStickerState.TOO_EXPENSIVE;
          } else {
            sticker.state = AbilityStickerState.AVAILABLE;
          }
        }
      });
    }

    @Override
    public void onPress() {
      setSelectedTab(this);
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, TEX_SCREEN);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      blit(stack, x, y, 232, selected ? 36 : 16, width, height);
      if (icon == null) {
        blit(stack, x + 2, y + 2, 232, 0, 16, 16);
      } else {
        RenderSystem.setShaderTexture(0, icon);
        blit(stack, x + 2, y + 2, 0, 0, 16, 16, 16, 16);
      }
      if (isHovered) {
        renderToolTip(stack, mouseX, mouseY);
      }
    }

    @Override
    public void renderToolTip(PoseStack stack, int mouseX, int mouseY) {
      SkillsScreen.this.renderTooltip(stack, getMessage(), mouseX, mouseY);
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
      output.add(NarratedElementType.TITLE, createNarrationMessage());
    }

    public void renderPage(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
      timer++;
      Minecraft mc = Minecraft.getInstance();
      double scale = mc.getWindow().getGuiScale();
      RenderSystem.enableScissor(
          (int) (scale * (guiLeft + PAGE_START_X - 2)),
          (int) (mc.getWindow().getHeight() - scale * (guiTop + PAGE_END_Y + 2)),
          (int) (scale * (PAGE_WIDTH + 4)),
          (int) (scale * (PAGE_HEIGHT + 4))
      );
      int xOff = Mth.floor(scrollX) % 16;
      int yOff = Mth.floor(scrollY) % 16;
      for (int i = -1; i < 15; i++) {
        for (int j = -1; j < 16; j++) {
          blit(stack, guiLeft + (PAGE_START_X - 2) + 16 * i + xOff, guiTop + (PAGE_START_Y - 2) + 16 * j + yOff, 232, 188, 16, 16);
        }
      }
      int adjMouseX = (int) (mouseX - (guiLeft + scrollX + PAGE_CENTER_X));
      int adjMouseY = (int) (mouseY - (guiTop + scrollY + PAGE_CENTER_Y));
      if (selectedAbility != null) {
        btnConfirm.updateIsHovered(adjMouseX, adjMouseY);
        btnCancel.updateIsHovered(adjMouseX, adjMouseY);
      }
      if (selectedAbility == null || (!btnConfirm.getIsHovered() && !btnCancel.getIsHovered())) {
        abilities.forEach(ability -> ability.updateIsHovered(adjMouseX, adjMouseY));
      }
      stack.pushPose();
      stack.translate(guiLeft + scrollX + PAGE_CENTER_X, guiTop + scrollY + PAGE_CENTER_Y, 0);
      RenderSystem.disableTexture();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      abilities.forEach(ability -> ability.renderDependantLines(stack));
      RenderSystem.enableTexture();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      abilities.forEach(ability -> ability.render(stack, mouseX, mouseY, partialTicks));
      if (selectedAbility != null) {
        btnConfirm.render(stack, mouseX, mouseY, partialTicks);
        btnCancel.render(stack, mouseX, mouseY, partialTicks);
      }
      stack.popPose();
      if (skill != null) {
        FormattedCharSequence fcs = new TranslatableComponent(
            "gui.worldleveling.skills.abilityPoints",
            skill.getAbilityPoints()
        ).getVisualOrderText();
        int width = font.width(fcs);
        int sections = Math.min(Mth.ceil(width / 16.0F), 13);
        int startX = PAGE_CENTER_X - (sections * 16) / 2;
        RenderSystem.setShaderTexture(0, TEX_SCREEN);
        RenderSystem.enableBlend();
        for (int i = 0; i < sections; i++) {
          blit(stack, guiLeft + startX + i * 16, guiTop + 22, 232, 204, 16, 16);
        }
        drawString(stack, font, fcs, guiLeft + PAGE_CENTER_X - width / 2, guiTop + 26, 0xFFFFFFFF);
      }
      RenderSystem.disableScissor();
    }

    public void scroll(double x, double y) {
      scrollX += x;
      scrollY += y;
    }

    public void resetScroll() {
      scrollX = scrollY = 0.0;
    }

    public void unselectAbility() {
      selectedAbility = null;
      abilities.forEach(a -> a.selected = false);
      btnConfirm = null;
      btnCancel = null;
    }

    public void selectAbility(AbilitySticker ability, boolean purchase) {
      selectedAbility = ability.ability;
      abilities.forEach(a -> a.selected = false);
      ability.selected = true;
      btnConfirm = new AbilityButton(this, ability.x - 28, ability.y - 18, ability.ability, purchase ? AbilityButtonType.PURCHASE : AbilityButtonType.REFUND);
      btnCancel = new AbilityButton(this, ability.x - 28, ability.y + 2, ability.ability, AbilityButtonType.CANCEL);
    }

  }

  private class AbilitySticker extends AbstractButton {

    final Tab parent;
    final Ability ability;
    final List<Component> tooltip;
    final List<Vec2> dependants;
    AbilityStickerState state;
    boolean selected;

    public AbilitySticker(Tab parent, Ability ability) {
      super(ability.getX(), -ability.getY(), 20, 20, ability.getTranslatedName());
      this.parent = parent;
      this.ability = ability;
      tooltip = null;
      dependants = ability.getDependencies().stream().map(a -> new Vec2(a.getX(), a.getY())).toList();
      state = AbilityStickerState.LOCKED;
      selected = false;
    }

    @Override
    public void onPress() {
      if (selected) {
        parent.unselectAbility();
      } else {
        switch (state) {
          case AVAILABLE -> {
            if (playerSkills.canPurchaseAbility(ability)) {
              parent.selectAbility(this, true);
              parent.purchaseSelectedAbility = true;
            }
          }
          case PURCHASED -> {
            if (playerSkills.canRefundAbility(ability)) {
              parent.selectAbility(this, false);
              parent.purchaseSelectedAbility = false;
            }
          }
        }
      }
    }

    public void renderDependantLines(PoseStack stack) {
      if (!dependants.isEmpty()) {
        Matrix4f pose = stack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        RenderSystem.enableBlend();
        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        dependants.forEach(pos -> {
          float theta = (float) Math.atan((-pos.y - y) / (pos.x - x));
          final float T = 2F;
          final int color = isHovered ? 0xFF00FF00 : 0xDD000000;
          float x1 = x + T * Mth.cos(theta + Mth.PI / 2.0F);
          float y1 = y + T * Mth.sin(theta + Mth.PI / 2.0F);
          float x2 = x + T * Mth.cos(theta - Mth.PI / 2.0F);
          float y2 = y + T * Mth.sin(theta - Mth.PI / 2.0F);
          float x3 = pos.x + T * Mth.cos(theta + Mth.PI / 2.0F);
          float y3 = -pos.y + T * Mth.sin(theta + Mth.PI / 2.0F);
          float x4 = pos.x + T * Mth.cos(theta - Mth.PI / 2.0F);
          float y4 = -pos.y + T * Mth.sin(theta - Mth.PI / 2.0F);
          // drawing triangles clockwise won't render anything
          if (x > pos.x) {
            builder.vertex(pose, x1, y1, 0.0F).color(color).endVertex();
            builder.vertex(pose, x2, y2, 0.0F).color(color).endVertex();
            builder.vertex(pose, x3, y3, 0.0F).color(color).endVertex();
            builder.vertex(pose, x2, y2, 0.0F).color(color).endVertex();
            builder.vertex(pose, x4, y4, 0.0F).color(color).endVertex();
            builder.vertex(pose, x3, y3, 0.0F).color(color).endVertex();
          } else {
            builder.vertex(pose, x1, y1, 0.0F).color(color).endVertex();
            builder.vertex(pose, x3, y3, 0.0F).color(color).endVertex();
            builder.vertex(pose, x2, y2, 0.0F).color(color).endVertex();
            builder.vertex(pose, x2, y2, 0.0F).color(color).endVertex();
            builder.vertex(pose, x3, y3, 0.0F).color(color).endVertex();
            builder.vertex(pose, x4, y4, 0.0F).color(color).endVertex();
          }
        });
        tesselator.end();
        RenderSystem.disableBlend();
      }
    }

    public boolean getIsHovered() {
      return isHovered;
    }

    public void updateIsHovered(int adjMouseX, int adjMouseY) {
      isHovered = adjMouseX >= x - width / 2 && adjMouseX <= x + width / 2 && adjMouseY >= y - height / 2 && adjMouseY <= y + height / 2;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
      renderButton(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
      int v;
      if (selected) {
        v = 148;
      } else {
        v = switch (state) {
          case AVAILABLE -> {
            if ((parent.timer % (DRAW_UPDATE_INTERVAL * 2)) < DRAW_UPDATE_INTERVAL) {
              yield 108;
            }
            yield 88;
          }
          case PURCHASED -> 128;
          case SELECTED -> 148;
          case LOCKED -> 168;
          default -> 88;
        };
      }
      RenderSystem.setShaderTexture(0, TEX_SCREEN);
      blit(stack, x - width / 2, y - height / 2, 232, v, width, height);

      AbilityIcon icon;
      if (ability.getIcons().isEmpty()) {
        icon = AbilityIcon.ItemIcon.DEFAULT;
      } else if (ability.getIcons().size() == 1) {
        icon = ability.getIcons().get(0);
      } else {
        icon = ability.getIcons().get(((parent.timer % (ability.getIcons().size() * DRAW_UPDATE_INTERVAL))) / DRAW_UPDATE_INTERVAL);
      }
      /*if (icon instanceof AbilityIcon.ItemIcon) {
        icon.render(SkillsScreen.this, stack, (int) (parent.scrollX + guiLeft + x + PAGE_CENTER_X - 8), (int) (parent.scrollY + guiTop + y + PAGE_CENTER_Y - 8));
      } else {
        icon.render(SkillsScreen.this, stack, x - 8, y - 8);
      }*/
      icon.render(SkillsScreen.this, stack, x - 8, y - 8);
    }

    @Override
    public void renderToolTip(PoseStack stack, int mouseX, int mouseY) {
      if (isHovered) {
        ChatFormatting costStyle;
        if (state == AbilityStickerState.AVAILABLE) {
          costStyle = ChatFormatting.GREEN;
        } else if (state == AbilityStickerState.TOO_EXPENSIVE) {
          costStyle = ChatFormatting.RED;
        } else {
          costStyle = ChatFormatting.GRAY;
        }
        List<Component> tooltip = List.of(
            getMessage().plainCopy().withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
            ability.getTranslatedDescription(),
            new TranslatableComponent("gui.worldleveling.skills.cost", new TextComponent(String.valueOf(ability.getCost()))
                .withStyle(costStyle)
            )
        );
        SkillsScreen.this.renderTooltip(stack, tooltip, Optional.empty(), x + 2, y);
      }
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
      output.add(NarratedElementType.TITLE, this.getMessage());
    }

  }

  private enum AbilityStickerState {
    PURCHASED,
    AVAILABLE,
    TOO_EXPENSIVE,
    SELECTED,
    LOCKED
  }

  private class AbilityButton extends AbstractButton {

    final Ability ability;
    final AbilityButtonType type;
    final Tab parent;

    public AbilityButton(Tab parent, int x, int y, Ability ability, AbilityButtonType type) {
      super(x, y, 16, 16, switch (type) {
        case PURCHASE -> WLStrings.GUI_SKILLS_PURCHASE;
        case REFUND -> WLStrings.GUI_SKILLS_REFUND;
        case CANCEL -> WLStrings.GUI_SKILLS_CANCEL;
      });
      this.ability = ability;
      this.parent = parent;
      this.type = type;
    }

    @Override
    public void onPress() {
      LocalPlayer player = Minecraft.getInstance().player;
      switch (type) {
        case PURCHASE -> {
          if (playerSkills.purchaseAbility(ability)) {
            player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
            WLNetwork.sendToServer(player, new UpdateAbilityMessage(ability, true));
            parent.calculateStates();
          }
        }
        case REFUND -> {
          if (playerSkills.refundAbility(ability)) {
            player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1.0F, 0.5F);
            WLNetwork.sendToServer(player, new UpdateAbilityMessage(ability, false));
            parent.calculateStates();
          }
        }
      }
      parent.unselectAbility();
    }

    public boolean getIsHovered() {
      return isHovered;
    }

    public void updateIsHovered(int mouseX, int mouseY) {
      isHovered = mouseX >= x && mouseX <= x + width && mouseY > y && mouseY <= y + height;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
      renderButton(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
      RenderSystem.setShaderTexture(0, TEX_SCREEN);
      blit(stack, x, y, 232, type == AbilityButtonType.CANCEL ? 220 : 236, width, height);
    }

    @Override
    public void renderToolTip(PoseStack stack, int mouseX, int mouseY) {
      if (isHovered) {
        SkillsScreen.this.renderTooltip(stack, getMessage(), x - 8, y);
      }
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
      output.add(NarratedElementType.TITLE, getMessage());
    }

  }

  private enum AbilityButtonType {
    PURCHASE,
    REFUND,
    CANCEL
  }

}
