package me.whizvox.worldleveling.common.lib.multiblock;

import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Lazy;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public class MultiBlockStructureEntry implements Supplier<MultiBlockStructure> {

  public final ResourceLocation name;

  private final Lazy<MultiBlockStructure> structureLazy;

  public MultiBlockStructureEntry(ResourceLocation key) {
    this.name = key;
    this.structureLazy = () -> {
      try (InputStream in = MultiBlockStructureEntry.class.getClassLoader().getResourceAsStream("data/%s/structures/%s.nbt".formatted(key.getNamespace(), key.getPath()))) {
        return MultiBlockStructure.load(NbtIo.readCompressed(in));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
  }

  @Override
  public MultiBlockStructure get() {
    return structureLazy.get();
  }

}
