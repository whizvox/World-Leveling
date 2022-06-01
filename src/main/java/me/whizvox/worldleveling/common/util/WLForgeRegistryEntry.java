package me.whizvox.worldleveling.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class WLForgeRegistryEntry<V extends IForgeRegistryEntry<V>> implements IForgeRegistryEntry<V> {

  private ResourceLocation name;

  @Override
  @SuppressWarnings("unchecked")
  public final V setRegistryName(ResourceLocation name) {
    this.name = name;
    return (V) this;
  }

  @Nullable
  @Override
  public final ResourceLocation getRegistryName() {
    return name;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof IForgeRegistryEntry<?> entry) {
      return entry.getRegistryType().equals(getRegistryType()) && Objects.equals(entry.getRegistryName(), name);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

}
