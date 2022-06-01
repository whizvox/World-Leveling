package me.whizvox.worldleveling.common.api.ability;

import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.lib.WLRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class AbilityBuilder<T extends Ability> {

  public final int cost;
  public final int xpos, ypos;

  private Supplier<SkillType> skillTypeSup;
  private final Collection<Supplier<? extends Ability>> dependencies;
  private final Collection<Supplier<? extends Ability>> toReplace;
  private final List<AbilityIcon> icons;
  private Function<AbilityBuilder<?>, T> constructor;

  public AbilityBuilder(int cost, int xpos, int ypos) {
    this.cost = cost;
    this.xpos = xpos;
    this.ypos = ypos;

    skillTypeSup = null;
    dependencies = new ArrayList<>();
    toReplace = new ArrayList<>();
    icons = new ArrayList<>();
    constructor = null;
  }

  public AbilityBuilder<T> skill(Supplier<SkillType> skillTypeSup) {
    this.skillTypeSup = skillTypeSup;
    return this;
  }

  public AbilityBuilder<T> skill(ResourceLocation skillTypeName) {
    return skill(Lazy.of(() -> WLRegistries.SKILLS.get().getValue(skillTypeName)));
  }

  public AbilityBuilder<T> dependency(Supplier<? extends Ability> dependency) {
    dependencies.add(dependency);
    return this;
  }

  public AbilityBuilder<T> dependency(ResourceLocation dependencyName) {
    return dependency(Lazy.of(() -> WLRegistries.ABILITIES.get().getValue(dependencyName)));
  }

  public AbilityBuilder<T> replace(Supplier<? extends Ability> toReplace) {
    this.toReplace.add(toReplace);
    return this;
  }

  public AbilityBuilder<T> replace(ResourceLocation abilityName) {
    return replace(Lazy.of(() -> WLRegistries.ABILITIES.get().getValue(abilityName)));
  }

  public AbilityBuilder<T> icon(AbilityIcon icon) {
    icons.add(icon);
    return this;
  }

  public AbilityBuilder<T> itemIcon(Supplier<Item> itemSup, @Nullable CompoundTag nbtData) {
    return icon(new AbilityIcon.ItemIcon(itemSup, nbtData));
  }

  public AbilityBuilder<T> itemIcon(Supplier<Item> itemSup) {
    return itemIcon(itemSup, null);
  }

  public AbilityBuilder<T> itemIcon(ResourceLocation itemName, @Nullable CompoundTag nbtData) {
    return itemIcon(() -> ForgeRegistries.ITEMS.getValue(itemName), nbtData);
  }

  public AbilityBuilder<T> itemIcon(ResourceLocation itemName) {
    return itemIcon(itemName, null);
  }

  public AbilityBuilder<T> textureIcon(ResourceLocation texturePath, int srcX, int srcY, int texWidth, int texHeight) {
    icons.add(new AbilityIcon.TextureIcon(texturePath, srcX, srcY, texWidth, texHeight));
    return this;
  }

  public AbilityBuilder<T> textureIcon(ResourceLocation texturePath) {
    icons.add(new AbilityIcon.TextureIcon(texturePath, 0, 0, 16, 16));
    return this;
  }

  public AbilityBuilder<T> defaultTextureIcon(ResourceLocation registryName) {
    return textureIcon(new ResourceLocation(registryName.getNamespace(), "textures/wlability/" + registryName.getPath() + ".png"));
  }

  public AbilityBuilder<T> unknownIcon() {
    return itemIcon(() -> Items.BARRIER);
  }

  public AbilityBuilder<T> constructor(Function<AbilityBuilder<?>, T> constructor) {
    this.constructor = constructor;
    return this;
  }

  public Collection<Supplier<? extends Ability>> getAllToReplace() {
    return Collections.unmodifiableCollection(toReplace);
  }

  public Supplier<SkillType> getSkillTypeSupplier() {
    return skillTypeSup;
  }

  public Collection<Supplier<? extends Ability>> getDependencies() {
    return Collections.unmodifiableCollection(dependencies);
  }

  public List<AbilityIcon> getIcons() {
    return Collections.unmodifiableList(icons);
  }

  public boolean hasNoIcons() {
    return icons.isEmpty();
  }

  public T build() {
    if (skillTypeSup == null) {
      throw new IllegalStateException("Unknown skill type");
    }
    if (constructor == null) {
      throw new IllegalStateException("Unknown ability constructor");
    }
    if (icons.isEmpty()) {
      unknownIcon();
    }
    return constructor.apply(this);
  }

}
