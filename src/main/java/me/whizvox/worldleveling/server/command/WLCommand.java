package me.whizvox.worldleveling.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.whizvox.worldleveling.common.api.Skill;
import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.api.ability.Ability;
import me.whizvox.worldleveling.common.capability.PlayerSkills;
import me.whizvox.worldleveling.common.lib.WLCaches;
import me.whizvox.worldleveling.common.lib.internal.WLStrings;
import me.whizvox.worldleveling.common.network.WLNetwork;
import me.whizvox.worldleveling.common.network.message.SyncPlayerSkillsMessage;
import me.whizvox.worldleveling.common.util.SkillsHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Comparator;
import java.util.List;

public class WLCommand {

  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("worldleveling")
        .then(Commands.literal("xp")
            .requires(src -> src.hasPermission(2))
            .then(Commands.literal("add")
                .then(Commands.argument("skill", SkillArgumentType.skill())
                    .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                        .executes(ctx -> addXp(
                            ctx.getSource().getPlayerOrException(),
                            IntegerArgumentType.getInteger(ctx, "amount"),
                            SkillArgumentType.getSkill(ctx, "skill")
                        ))
                        .then(Commands.argument("target", EntityArgument.player())
                            .executes(ctx -> addXp(
                                EntityArgument.getPlayer(ctx, "target"),
                                IntegerArgumentType.getInteger(ctx, "amount"),
                                SkillArgumentType.getSkill(ctx, "skill")
                            ))
                        )
                    )
                )
            )
            .then(Commands.literal("set")
                .then(Commands.argument("skill", SkillArgumentType.skill())
                    .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                        .executes(ctx -> setXp(
                            ctx.getSource().getPlayerOrException(),
                            IntegerArgumentType.getInteger(ctx, "amount"),
                            SkillArgumentType.getSkill(ctx, "skill")
                        ))
                        .then(Commands.argument("target", EntityArgument.player())
                            .executes(ctx -> setXp(
                                EntityArgument.getPlayer(ctx, "target"),
                                IntegerArgumentType.getInteger(ctx, "amount"),
                                SkillArgumentType.getSkill(ctx, "skill")
                            ))
                        )
                    )
                )
            )
        )
        .then(Commands.literal("ap")
            .requires(src -> src.hasPermission(2))
            .then(Commands.literal("set")
                .then(Commands.argument("skill", SkillArgumentType.skill())
                    .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                        .executes(ctx -> setAbilityPoints(
                            ctx.getSource().getPlayerOrException(),
                            IntegerArgumentType.getInteger(ctx, "amount"),
                            SkillArgumentType.getSkill(ctx, "skill")
                        ))
                        .then(Commands.argument("target", EntityArgument.player())
                            .executes(ctx -> setAbilityPoints(
                              EntityArgument.getPlayer(ctx, "target"),
                                IntegerArgumentType.getInteger(ctx, "amount"),
                                SkillArgumentType.getSkill(ctx, "skill")
                            ))
                        )
                    )
                )
            )
            .then(Commands.literal("add")
                .then(Commands.argument("skill", SkillArgumentType.skill())
                    .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                        .executes(ctx -> addAbilityPoints(
                            ctx.getSource().getPlayerOrException(),
                            IntegerArgumentType.getInteger(ctx, "amount"),
                            SkillArgumentType.getSkill(ctx, "skill")
                        ))
                        .then(Commands.argument("target", EntityArgument.player())
                            .executes(ctx -> addAbilityPoints(
                                EntityArgument.getPlayer(ctx, "target"),
                                IntegerArgumentType.getInteger(ctx, "amount"),
                                SkillArgumentType.getSkill(ctx, "skill")
                            ))
                        )
                    )
                )
            )
        )
        .then(Commands.literal("ability")
            .requires(src -> src.hasPermission(2))
            .then(Commands.literal("add")
                .then(Commands.argument("ability", AbilityArgumentType.ability())
                    .executes(ctx -> addAbility(
                        ctx.getSource().getPlayerOrException(),
                        AbilityArgumentType.getAbility(ctx, "ability")
                    ))
                    .then(Commands.argument("target", EntityArgument.player())
                        .executes(ctx -> addAbility(
                            EntityArgument.getPlayer(ctx, "target"),
                            AbilityArgumentType.getAbility(ctx, "ability")
                        ))
                    )
                )
            )
            .then(Commands.literal("remove")
                .then(Commands.argument("ability", AbilityArgumentType.ability())
                    .executes(ctx -> removeAbility(
                        ctx.getSource().getPlayerOrException(),
                        AbilityArgumentType.getAbility(ctx, "ability")
                    ))
                    .then(Commands.argument("target", EntityArgument.player())
                        .executes(ctx -> removeAbility(
                            EntityArgument.getPlayer(ctx, "target"),
                            AbilityArgumentType.getAbility(ctx, "ability")
                        ))
                    )
                )
            )
        )
        .then(Commands.literal("clearcache")
            .requires(src -> src.hasPermission(2))
            .executes(ctx -> clearCaches(ctx.getSource().getServer(), true, true))
            .then(Commands.literal("world")
                .executes(ctx -> clearCaches(ctx.getSource().getServer(), true, false))
            )
            .then(Commands.literal("players")
                .executes(ctx -> clearCaches(ctx.getSource().getServer(), false, true))
            )
            .then(Commands.literal("all")
                .executes(ctx -> clearCaches(ctx.getSource().getServer(), true, true))
            )
        )
        .then(Commands.literal("view")
            .executes(ctx -> viewAllSkills(ctx.getSource().getPlayerOrException()))
            .then(Commands.argument("target", EntityArgument.player())
                .executes(ctx -> viewAllSkills(EntityArgument.getPlayer(ctx, "target")))
                .then(Commands.argument("skill", SkillArgumentType.skill())
                    .executes(ctx -> viewSkill(
                        EntityArgument.getPlayer(ctx, "target"),
                        SkillArgumentType.getSkill(ctx, "skill")
                    ))
                )
            )
        );
    dispatcher.register(builder);
  }

  private static int addXp(ServerPlayer target, int amount, SkillType skill) {
    SkillsHelper.handle(target, skills -> {
      SkillsHelper.increaseXpAndSync(target, skills, skill, amount);
      target.displayClientMessage(new TranslatableComponent(
          "message.worldleveling.command.add.result",
          WLStrings.formatGain(amount),
          WLStrings.formatSkill(skill),
          WLStrings.formatLevel(skills.getLevel(skill)),
          WLStrings.formatExperience(skills.getExperience(skill))
      ), false);
    });
    return 1;
  }

  private static int setXp(ServerPlayer target, int amount, SkillType skill) {
    SkillsHelper.handle(target, skills -> {
      SkillsHelper.setXpAndSync(target, skills, skill, amount);
      target.displayClientMessage(new TranslatableComponent(
          "message.worldleveling.command.set.result",
          WLStrings.formatSkill(skill),
          WLStrings.formatExperience(amount),
          WLStrings.formatLevel(skills.getLevel(skill))
      ), false);
    });
    return 1;
  }

  private static void updateAbilityPoints(ServerPlayer target, int amount, SkillType skill, boolean add) {
    SkillsHelper.handle(target, skills -> {
      // initialize the skill with 0 XP if the player hasn't collected any XP
      skills.increaseExperience(skill, 0);
      skills.getSkill(skill).ifPresent(s -> {
        if (add) {
          s.setAbilityPoints(s.getAbilityPoints() + amount);
          target.displayClientMessage(new TranslatableComponent(
              "message.worldleveling.command.ap.add",
              WLStrings.formatGain(amount),
              WLStrings.formatSkill(skill)
          ), false);
        } else {
          s.setAbilityPoints(amount);
          target.displayClientMessage(new TranslatableComponent(
              "message.worldleveling.command.ap.set",
              WLStrings.formatSkill(skill),
              WLStrings.formatGain(amount)
              ), false);
        }
        WLNetwork.sendToClient(target, SyncPlayerSkillsMessage.create(target));
      });
    });
  }

  private static int addAbilityPoints(ServerPlayer target, int amount, SkillType skill) {
    updateAbilityPoints(target, amount, skill, true);
    return 1;
  }

  private static int setAbilityPoints(ServerPlayer target, int amount, SkillType skill) {
    updateAbilityPoints(target, amount, skill, false);
    return 1;
  }

  private static int addAbility(ServerPlayer target, Ability ability) {
    SkillsHelper.handle(target, skills -> {
      if (skills.addAbility(ability)) {
        target.displayClientMessage(new TranslatableComponent(
            "message.worldleveling.command.ability.add.success",
            ability.getSkill().getTranslatedName(),
            ability.getTranslatedName()
        ), false);
        WLNetwork.sendToClient(target, SyncPlayerSkillsMessage.create(target));
      } else {
        target.displayClientMessage(WLStrings.CMD_ABILITY_ADD_EXISTS, false);
      }
    });
    return 1;
  }

  private static int removeAbility(ServerPlayer target, Ability ability) {
    SkillsHelper.handle(target, skills -> {
      if (skills.removeAbility(ability)) {
        target.displayClientMessage(new TranslatableComponent(
            "message.worldleveling.command.ability.remove.success",
            ability.getSkill().getTranslatedName(),
            ability.getTranslatedName()
        ), false);
        WLNetwork.sendToClient(target, SyncPlayerSkillsMessage.create(target));
      } else {
        target.displayClientMessage(WLStrings.CMD_ABILITY_REMOVE_NONE, false);
      }
    });
    return 1;
  }

  private static int clearCaches(MinecraftServer server, boolean clearWorldCache, boolean clearPlayerCaches) {
    if (clearWorldCache) {
      WLCaches.clear();
    }
    if (clearPlayerCaches) {
      server.getPlayerList().getPlayers().forEach(player -> SkillsHelper.handle(player, PlayerSkills::clearCache));
    }
    return 1;
  }

  private static int viewSkill(ServerPlayer target, SkillType skill) {
    SkillsHelper.handle(target, skills -> {
      skills.getSkill(skill).ifPresent(s -> {
        target.displayClientMessage(new TranslatableComponent(
            "message.worldleveling.command.view.entry",
            WLStrings.formatSkill(s.getType()),
            WLStrings.formatLevel(s.getLevel()),
            WLStrings.formatExperience(s.getExperience()),
            WLStrings.formatGain(s.getExperienceNeededForLevelUp() - s.getExperience())
        ), false);
        List<Component> passiveEffectsDesc = s.describePassiveEffects(target);
        if (!passiveEffectsDesc.isEmpty()) {
          target.displayClientMessage(WLStrings.CMD_VIEW_PASSIVE_EFFECTS.plainCopy().append(":"), false);
          passiveEffectsDesc.forEach(line -> {
            target.displayClientMessage(new TextComponent("- ").append(line), false);
          });
        }
        List<Component> extraDataDesc = s.describeExtraData(target);
        if (!extraDataDesc.isEmpty()) {
          target.displayClientMessage(WLStrings.CMD_VIEW_EXTRA_DATA.plainCopy().append(":"), false);
          extraDataDesc.forEach(line -> {
            target.displayClientMessage(new TextComponent("- ").append(line), false);
          });
        }
      });
    });
    return 1;
  }

  private static int viewAllSkills(ServerPlayer target) {
    SkillsHelper.handle(target, skills -> {
      target.displayClientMessage(WLStrings.CMD_VIEW_HEADER, false);
      List<Skill> allSkills = skills.allSkills()
          .filter(skill -> skill.getExperience() > 0)
          .sorted(Comparator.comparingInt(Skill::getExperience))
          .toList();
      List<Skill> zeroXpSkills = skills.allSkills()
          .filter(skill -> skill.getExperience() == 0)
          .toList();
      if (allSkills.isEmpty()) {
        target.displayClientMessage(WLStrings.CMD_VIEW_NO_SKILLS, false);
      } else {
        allSkills.forEach(skill -> target.displayClientMessage(
            new TextComponent("- ")
                .append(new TranslatableComponent("message.worldleveling.command.view.entry",
                    WLStrings.formatSkill(skill.getType()),
                    WLStrings.formatLevel(skill.getLevel()),
                    WLStrings.formatExperience(skill.getExperience()),
                    WLStrings.formatGain(skill.getExperienceNeededForLevelUp() - skill.getExperience())
                )), false
        ));
        if (!zeroXpSkills.isEmpty()) {
          MutableComponent zeroXpComp = WLStrings.formatSkill(zeroXpSkills.get(0).getType());
          for (int i = 1; i < zeroXpSkills.size(); i++) {
            zeroXpComp = zeroXpComp
                .append(", ")
                .withStyle(ChatFormatting.RESET)
                .append(WLStrings.formatSkill(zeroXpSkills.get(i).getType()));
          }
          target.displayClientMessage(new TranslatableComponent("message.worldleveling.command.view.noXp", zeroXpComp), false);
        }
      }
    });
    return 1;
  }

}
