package me.whizvox.worldleveling.common.api;

import me.whizvox.worldleveling.common.lib.internal.WLStrings;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SkillType implements IForgeRegistryEntry<SkillType> {

  private ResourceLocation name;

  public SkillType() {
    name = null;
  }

  /**
   * Construct a new skill instance. By default, an instance of {@link Skill} is created. If a skill requires storage
   * of extra data or needs extra functionality, then subclasses are encouraged to override the default implementation.
   * @param experience The amount of initial XP
   * @param extraData Any extra data needed to construct the skill
   * @return A skill instance
   */
  public Skill createSkill(int experience, @Nullable Tag extraData) {
    return new Skill(this, experience);
  }

  /**
   * Triggered when a skill's experience is updated to an amount outside the bounds of its current level. By default,
   * this does nothing. Subclasses are encouraged to override this if, for example, an attribute needs to be added to
   * achieve the level-tied passive buff.
   * @param player The player whose skill level has changed
   * @param level The final level the skill is at
   */
  public void onLevelChange(Player player, int level) {
  }

  /**
   * Create a once-per-world cache pertaining to this skill type. Only one of these will exist per registered skill
   * type. It is created when a world loads for the first time, cleared when quitting out of a world (SP) or when a
   * server stops (MP), and fully discarded when the game window is closed. These all can be accessed via
   * {@link me.whizvox.worldleveling.common.lib.WLCaches#get(SkillType)}. By default, this creates a dummy cache that
   * isn't registered. Subclasses are encouraged to override this if a world-bound cache would prove to be beneficial.
   * @return A world-specific static cache
   */
  public Cache createWorldCache() {
    return Cache.DUMMY;
  }

  /**
   * Create a once-per-player cache pertaining to this skill type. Each player will have one of these per registered
   * skill type. It is created when a player joins a world, and discarded when a player leaves a world. These all can
   * be accessed via {@link me.whizvox.worldleveling.common.capability.PlayerSkills#getCache(SkillType)}. By default,
   * this creates a dummy cache that isn't registered. Subclasses are encouraged to override this if a player-bound
   * cache would prove to be beneficial.
   * @return A player-specific cache that is discarded
   */
  public Cache createPlayerCache() {
    return Cache.DUMMY;
  }

  public void addListeners(IEventBus bus) {
  }

  public Component getTranslatedName() {
    if (name == null) {
      return WLStrings.NO_NAME;
    }
    return new TranslatableComponent("wlskill." + name.getNamespace() + "." + name.getPath().replaceAll("/", "."));
  }

  @Override
  public SkillType setRegistryName(ResourceLocation name) {
    this.name = name;
    return this;
  }

  @Nullable
  @Override
  public ResourceLocation getRegistryName() {
    return name;
  }

  @Override
  public Class<SkillType> getRegistryType() {
    return SkillType.class;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return o instanceof SkillType other && Objects.equals(other.name, name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

}
