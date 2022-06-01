package me.whizvox.worldleveling.common.skill;

import me.whizvox.worldleveling.common.api.Cache;
import me.whizvox.worldleveling.common.api.Skill;
import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.lib.WLCaches;
import me.whizvox.worldleveling.common.lib.WLConfigs;
import me.whizvox.worldleveling.common.lib.WLSkills;
import me.whizvox.worldleveling.common.util.SkillsHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.Nullable;

public class MiningSkillType extends SkillType {

  @Override
  public Skill createSkill(int experience, @Nullable Tag extraData) {
    return new MiningSkill(this, experience);
  }

  @Override
  public Cache createWorldCache() {
    return new MiningCache(
        WLConfigs.SKILL_MINING.blocksXp.get(),
        WLConfigs.SKILL_MINING.prospectingOres.get()
    );
  }

  @Override
  public void addListeners(IEventBus bus) {
    bus.addListener(this::increaseXpOnBlockBreak);
    bus.addListener(this::applyPassiveDigSpeedBuff);
  }

  private void increaseXpOnBlockBreak(final BlockEvent.BreakEvent event) {
    Player player = event.getPlayer();
    SkillsHelper.handle(player, skills -> {
      int xp = WLCaches.<MiningCache>get(WLSkills.MINING.get()).getExperience(event.getState());
      SkillsHelper.increaseXpAndSync(player, skills, WLSkills.MINING.get(), xp);
    });
  }

  private void applyPassiveDigSpeedBuff(final PlayerEvent.BreakSpeed event) {
    SkillsHelper.handle(event.getPlayer(), skills -> {
      int level = skills.getLevel(WLSkills.MINING.get());
      if (level > 0) {
        event.setNewSpeed((float) (event.getOriginalSpeed() + (event.getOriginalSpeed() * WLConfigs.SKILL_MINING.digSpeedMultiplier.get() * level)));
      }
    });
  }

}
