package me.whizvox.worldleveling.common.util;

import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.capability.PlayerSkills;
import me.whizvox.worldleveling.common.lib.WLCapabilities;
import me.whizvox.worldleveling.common.network.WLNetwork;
import me.whizvox.worldleveling.common.network.message.SyncPlayerSkillsMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public class SkillsHelper {

  public static void handle(Player player, Consumer<PlayerSkills> consumer) {
    player.getCapability(WLCapabilities.PLAYER_SKILLS).ifPresent(consumer::accept);
  }

  public static void increaseXpAndSync(Player player, PlayerSkills skills, SkillType skill, int amount) {
    updateXpAndSync(player, skills, skill, amount, true);
  }

  public static void setXpAndSync(Player player, PlayerSkills skills, SkillType skill, int xp) {
    updateXpAndSync(player, skills, skill, xp, false);
  }

  private static void updateXpAndSync(Player player, PlayerSkills skills, SkillType skill, int amount, boolean increase) {
    if (amount < 0) {
      return;
    }
    int oldLevel = skills.getLevel(skill);
    boolean levelUp;
    if (increase) {
      levelUp = skills.increaseExperience(skill, amount);
    } else {
      levelUp = skills.setExperience(skill, amount);
    }
    int newLevel = skills.getLevel(skill);
    if (levelUp) {
      player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
      skills.getSkill(skill).ifPresent(s -> s.setAbilityPoints(s.getAbilityPoints() + (newLevel - oldLevel)));
      skill.onLevelChange(player, skills.getLevel(skill));
    }
    var syncMsg = SyncPlayerSkillsMessage.create(player);
    if (syncMsg != null) {
      WLNetwork.sendToClient((ServerPlayer) player, syncMsg);
    }
  }

}
