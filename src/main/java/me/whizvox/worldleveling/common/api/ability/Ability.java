package me.whizvox.worldleveling.common.api.ability;

import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.lib.internal.WLStrings;
import me.whizvox.worldleveling.common.util.WLForgeRegistryEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Ability extends WLForgeRegistryEntry<Ability> {

  private final Supplier<SkillType> skill;
  private final int cost;
  private final Collection<Supplier<? extends Ability>> dependencies;
  private final Collection<Supplier<? extends Ability>> toReplace;
  private final int xpos, ypos;
  private final List<AbilityIcon> icons;

  public Ability(AbilityBuilder<?> builder) {
    this.skill = builder.getSkillTypeSupplier();
    this.cost = builder.cost;
    this.dependencies = builder.getDependencies();
    this.toReplace = builder.getAllToReplace();
    this.xpos = builder.xpos;
    this.ypos = builder.ypos;
    this.icons = builder.getIcons();
  }

  public void addListeners(IEventBus bus) {
  }

  public void onAcquire(Player player) {
  }

  public void onRevoke(Player player) {
  }

  public Component getTranslatedName() {
    final ResourceLocation name = getRegistryName();
    if (name == null) {
      return WLStrings.NO_NAME;
    }
    return new TranslatableComponent("wlability." + name.getNamespace() + "." + name.getPath().replaceAll("/", "."));
  }

  public Component getTranslatedDescription() {
    final ResourceLocation name = getRegistryName();
    if (name == null) {
      return TextComponent.EMPTY;
    }
    return new TranslatableComponent("wlability." + name.getNamespace() + "." + name.getPath().replaceAll("/", ".") + ".description");
  }

  public SkillType getSkill() {
    return skill.get();
  }

  public int getCost() {
    return cost;
  }

  public Collection<Ability> getDependencies() {
    return dependencies.stream().map(Supplier::get).collect(Collectors.toList());
  }

  public Collection<Ability> getAllToReplace() {
    return toReplace.stream().map(Supplier::get).collect(Collectors.toList());
  }

  public int getX() {
    return xpos;
  }

  public int getY() {
    return ypos;
  }

  public List<AbilityIcon> getIcons() {
    return icons;
  }

  @Override
  public Class<Ability> getRegistryType() {
    return Ability.class;
  }

}
