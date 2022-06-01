package me.whizvox.worldleveling.server.command;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.lib.WLRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class SkillArgumentType implements ArgumentType<SkillType> {

  private static final DynamicCommandExceptionType IMPROPERLY_FORMATTED =
      new DynamicCommandExceptionType(nameStr -> new LiteralMessage("Improperly formatted skill name: " + nameStr));

  private static final DynamicCommandExceptionType UNKNOWN =
      new DynamicCommandExceptionType(nameStr -> new LiteralMessage("Unknown skill: " + nameStr));

  @Override
  public SkillType parse(StringReader reader) throws CommandSyntaxException {
    ResourceLocation name = ResourceLocation.read(reader);
    SkillType skill = WLRegistries.SKILLS.get().getValue(name);
    if (skill == null) {
      throw UNKNOWN.createWithContext(reader, name);
    }
    return skill;
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    return SharedSuggestionProvider.suggestResource(WLRegistries.SKILLS.get().getKeys(), builder);
  }

  public static SkillArgumentType skill() {
    return new SkillArgumentType();
  }

  public static SkillType getSkill(CommandContext<CommandSourceStack> ctx, String name) {
    return ctx.getArgument(name, SkillType.class);
  }

}
