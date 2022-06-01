package me.whizvox.worldleveling.common.event;

import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.capability.PlayerSkills;
import me.whizvox.worldleveling.common.lib.WLCaches;
import me.whizvox.worldleveling.common.lib.WLConfigs;
import me.whizvox.worldleveling.common.lib.WLSkills;
import me.whizvox.worldleveling.common.lib.internal.WLStrings;
import me.whizvox.worldleveling.common.skill.CombatSkillCache;
import me.whizvox.worldleveling.common.skill.FarmingCache;
import me.whizvox.worldleveling.common.skill.MiningCache;
import me.whizvox.worldleveling.common.util.SkillsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.List;
import java.util.Optional;

public class WorldEventListeners {

  public static void register(IEventBus bus) {
    bus.addListener(WorldEventListeners::onBlockBreak);
    bus.addListener(WorldEventListeners::onLivingEntityHurt);
    bus.addListener(WorldEventListeners::onLivingEntityDied);
    //bus.addListener(WorldEventListeners::onItemFished);
    //bus.addListener(WorldEventListeners::onBreakSpeed);
    bus.addListener(WorldEventListeners::onCropGrow);
    bus.addListener(WorldEventListeners::onTreeGrow);
    bus.addListener(WorldEventListeners::onBlockPlace);
  }

  private static boolean canPlayerGainExperience(Player player) {
    return !player.isCreative();
  }

  private static void increaseExperience(PlayerSkills skills, SkillType skill, int amount, Player player) {
    SkillsHelper.increaseXpAndSync(player, skills, skill, amount);
    player.displayClientMessage(new TranslatableComponent("message.worldleveling.xpGained",
        WLStrings.formatSkill(skill),
        WLStrings.formatGain(amount)
    ), true);
  }

  private static void onBlockBreak(final BlockEvent.BreakEvent event) {
    if (canPlayerGainExperience(event.getPlayer())) {
      SkillsHelper.handle(event.getPlayer(), skills -> {
        int miningXp = WLCaches.<MiningCache>get(WLSkills.MINING.get()).getExperience(event.getState());
        if (miningXp > 0) {
          increaseExperience(skills, WLSkills.MINING.get(), miningXp, event.getPlayer());
        }
        int farmingXp = WLCaches.<FarmingCache>get(WLSkills.FARMING.get()).getExperienceFromBlockBreak(event.getState());
        if (farmingXp > 0) {
          increaseExperience(skills, WLSkills.FARMING.get(), farmingXp, event.getPlayer());
        }
      });
    }
  }

  private static void onGrowEvent(BlockPos pos, LevelAccessor world, int radius, int xp) {
    List<Player> nearbyPlayers = world.getEntitiesOfClass(Player.class, new AABB(
        pos.offset(-radius, -radius, -radius),
        pos.offset(radius, radius, radius)
    ));
    nearbyPlayers.forEach(player -> {
      if (canPlayerGainExperience(player)) {
        SkillsHelper.handle(player, skills -> increaseExperience(skills, WLSkills.FARMING.get(), xp, player));
      }
    });
  }

  private static void onCropGrow(final BlockEvent.CropGrowEvent event) {
    final int radius = WLConfigs.SKILL_FARMING.cropGrowthPlayerRadius.get();
    final int xp = WLConfigs.SKILL_FARMING.cropGrowthXp.get();
    if (radius > 0 && xp > 0) {
      onGrowEvent(event.getPos(), event.getWorld(), radius, xp);
    }
  }

  private static void onTreeGrow(final SaplingGrowTreeEvent event) {
    final int radius = WLConfigs.SKILL_FARMING.treeGrowthPlayerRadius.get();
    final int xp = WLConfigs.SKILL_FARMING.treeGrowthXp.get();
    if (radius > 0 && xp > 0) {
      onGrowEvent(event.getPos(), event.getWorld(), radius, xp);
    }
  }

  private static void onLivingEntityHurt(final LivingHurtEvent event) {
    if (event.getSource().getEntity() instanceof Player player && canPlayerGainExperience(player)) {
      SkillsHelper.handle(player, skills ->
          // TODO damage dealt doesnt take mob health into account
          skills.<CombatSkillCache>getCache(WLSkills.COMBAT.get()).damage(player, event.getEntityLiving(), event.getAmount())
      );
    }
  }

  private static void onLivingEntityDied(final LivingDeathEvent event) {
    if (event.getSource().getEntity() instanceof Player player && canPlayerGainExperience(player)) {
      SkillsHelper.handle(player, skills -> {
        double damage = skills.<CombatSkillCache>getCache(WLSkills.COMBAT.get()).finish(player, event.getEntityLiving());
        int xp = Math.max((int) (WLConfigs.SKILL_COMBAT.healthXpMultiplier.get() * damage), 1);
        increaseExperience(skills, WLSkills.COMBAT.get(), xp, player);
      });
    }
  }

  private static void onItemFished(final ItemFishedEvent event) {
    if (canPlayerGainExperience(event.getPlayer())) {
      SkillsHelper.handle(event.getPlayer(), skills -> {
        Optional<Integer> amtOp = event.getDrops().stream().map(stack -> {
          if (stack.is(ItemTags.FISHES)) {
            return 5;
          } else if (stack.is(Items.ENCHANTED_BOOK)) {
            return 20;
          }
          return 2;
        }).reduce(Integer::sum);
        amtOp.ifPresent(amount -> {
          increaseExperience(skills, WLSkills.FISHING.get(), amount, event.getPlayer());
        });
      });
    }
  }

  /*private static void onBreakSpeed(final PlayerEvent.BreakSpeed event) {
    if (canPlayerGainExperience(event.getPlayer())) {
      SkillsHelper.handle(event.getPlayer(), skills ->
          event.setNewSpeed(SkillsHelper.getNewMiningSpeed(event.getOriginalSpeed(), skills, event.getPlayer().getUseItem()))
      );
    }
  }*/

  private static void onBlockPlace(final BlockEvent.EntityPlaceEvent event) {
    if (event.getEntity() instanceof Player player && canPlayerGainExperience(player)) {
      int farmPlantXp = WLCaches.<FarmingCache>get(WLSkills.FARMING.get()).getExperienceFromBlockPlace(event.getState());
      if (farmPlantXp > 0) {
        SkillsHelper.handle(player, skills -> increaseExperience(skills, WLSkills.FARMING.get(), farmPlantXp, player));
      }
    }
  }

}
