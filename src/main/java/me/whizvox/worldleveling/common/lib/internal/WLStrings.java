package me.whizvox.worldleveling.common.lib.internal;

import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.api.ability.Ability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public class WLStrings {

  public static final Component
      NO_NAME = new TranslatableComponent("tooltip.worldleveling.generic.noName"),
      DO_NOT_KNOW = new TranslatableComponent("message.worldleveling.doNotKnow"),
      CMD_VIEW_HEADER = new TranslatableComponent("message.worldleveling.command.view.header"),
      CMD_VIEW_NO_SKILLS = new TranslatableComponent("message.worldleveling.command.view.noSkills"),
      CMD_VIEW_PASSIVE_EFFECTS = new TranslatableComponent("message.worldleveling.command.view.passiveEffects.header"),
      CMD_VIEW_EXTRA_DATA = new TranslatableComponent("message.worldleveling.command.view.extraData.header"),
      CMD_ABILITY_ADD_EXISTS = new TranslatableComponent("message.worldleveling.command.ability.add.exists"),
      CMD_ABILITY_REMOVE_NONE = new TranslatableComponent("message.worldleveling.command.ability.remove.none"),
      GUI_SKILLS_OVERVIEW = new TranslatableComponent("gui.worldleveling.skills.overview"),
      GUI_SKILLS_PURCHASE = new TranslatableComponent("gui.worldleveling.skills.purchase"),
      GUI_SKILLS_REFUND = new TranslatableComponent("gui.worldleveling.skills.refund"),
      GUI_SKILLS_CANCEL = new TranslatableComponent("gui.worldleveling.skills.cancel"),
      MENU_SOOTY_FORGE = new TranslatableComponent("menu.worldleveling.sooty_forge"),
      MENU_DARK_FORGE = new TranslatableComponent("menu.worldleveling.dark_forge");

  public static Component formatMessage_forgeNeedMaterials(int count, ItemStack item) {
    return new TranslatableComponent(
        "message.worldleveling.ability.mining.forge.needMaterials",
        formatInteger(count, ChatFormatting.AQUA),
        item.getDisplayName().copy().withStyle(ChatFormatting.DARK_AQUA)
    );
  }

  private static final ChatFormatting
      FORMAT_XP = ChatFormatting.YELLOW,
      FORMAT_LEVEL = ChatFormatting.AQUA,
      FORMAT_GAIN = ChatFormatting.GREEN,
      FORMAT_SKILL = ChatFormatting.RED,
      FORMAT_ABILITY = ChatFormatting.LIGHT_PURPLE;

  public static MutableComponent formatExperience(int xp) {
    return new TextComponent(Integer.toString(xp)).withStyle(FORMAT_XP);
  }

  public static MutableComponent formatLevel(int level) {
    return new TextComponent(Integer.toString(level)).withStyle(FORMAT_LEVEL);
  }

  public static MutableComponent formatGain(int delta) {
    return new TextComponent(Integer.toString(delta)).withStyle(FORMAT_GAIN);
  }

  public static MutableComponent formatSkill(SkillType skill) {
    return skill.getTranslatedName().copy().withStyle(FORMAT_SKILL);
  }

  public static MutableComponent formatAbility(Ability ability) {
    return ability.getTranslatedName().copy().withStyle(FORMAT_ABILITY);
  }

  public static MutableComponent formatInteger(int value, ChatFormatting format) {
    return new TextComponent(Integer.toString(value)).withStyle(format);
  }

}
