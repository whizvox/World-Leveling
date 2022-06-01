package me.whizvox.worldleveling.common.util;

import me.whizvox.worldleveling.common.api.Cache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

public class TagAndNameCache<T extends IForgeRegistryEntry<T>, V extends Comparable<V>> implements Cache {

  private final HashMap<ResourceLocation, V> cache;
  private final HashMap<ResourceLocation, V> namesLookup;
  private final HashMap<TagKey<T>, V> tagsLookup;
  private final V zeroValue;

  public TagAndNameCache(Map<String, V> rawValues, Function<ResourceLocation, TagKey<T>> tagCreator, V zeroValue) {
    this.zeroValue = zeroValue;
    cache = new HashMap<>();
    namesLookup = new HashMap<>();
    tagsLookup = new HashMap<>();

    rawValues.forEach((key, value) -> {
      if (!key.isBlank()) {
        if (key.charAt(0) == '#') {
          ResourceLocation name = new ResourceLocation(key.substring(1));
          tagsLookup.put(tagCreator.apply(name), value);
        } else {
          ResourceLocation name = new ResourceLocation(key);
          namesLookup.put(name, value);
        }
      }
    });
  }

  @Override
  public void clear() {
    cache.clear();
  }

  public V getValue(ResourceLocation name, Stream<TagKey<T>> tags) {
    return cache.computeIfAbsent(name, rl -> {
      AtomicReference<V> tagValue = new AtomicReference<>(zeroValue);
      tags.forEach(tag -> {
        V value = tagsLookup.getOrDefault(tag, zeroValue);
        if (value.compareTo(tagValue.get()) > 0) {
          tagValue.set(value);
        }
      });
      V nameValue = namesLookup.getOrDefault(name, zeroValue);
      if (nameValue.compareTo(tagValue.get()) > 0) {
        return nameValue;
      }
      return tagValue.get();
    });
  }

}
