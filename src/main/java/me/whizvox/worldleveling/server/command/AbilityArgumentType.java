package me.whizvox.worldleveling.server.command;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.whizvox.worldleveling.common.api.ability.Ability;
import me.whizvox.worldleveling.common.lib.WLRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class AbilityArgumentType implements ArgumentType<Ability> {

  private static final DynamicCommandExceptionType IMPROPERLY_FORMATTED =
      new DynamicCommandExceptionType(nameStr -> new LiteralMessage("Improperly formatted ability name: " + nameStr));

  private static final DynamicCommandExceptionType UNKNOWN =
      new DynamicCommandExceptionType(name -> new LiteralMessage("Unknown ability: " + name));

  @Override
  public Ability parse(StringReader reader) throws CommandSyntaxException {
    ResourceLocation name = ResourceLocation.read(reader);
    Ability ability = WLRegistries.ABILITIES.get().getValue(name);
    if (ability == null) {
      throw UNKNOWN.createWithContext(reader, name);
    }
    return ability;
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    return SharedSuggestionProvider.suggestResource(WLRegistries.ABILITIES.get().getKeys(), builder);
  }

  public static AbilityArgumentType ability() {
    return new AbilityArgumentType();
  }

  public static Ability getAbility(CommandContext<CommandSourceStack> ctx, String name) {
    return ctx.getArgument(name, Ability.class);
  }

}
