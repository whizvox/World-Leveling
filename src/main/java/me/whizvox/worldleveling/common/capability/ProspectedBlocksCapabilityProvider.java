package me.whizvox.worldleveling.common.capability;

import me.whizvox.worldleveling.common.lib.WLCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.StringTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProspectedBlocksCapabilityProvider implements ICapabilitySerializable<StringTag> {

  private final ProspectedBlock value;
  private final LazyOptional<ProspectedBlock> capability;

  public ProspectedBlocksCapabilityProvider() {
    value = new ProspectedBlock();
    capability = LazyOptional.of(() -> value);
  }

  @NotNull
  @Override
  public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    return cap == WLCapabilities.PROSPECTED_BLOCK ? capability.cast() : LazyOptional.empty();
  }

  @Override
  public StringTag serializeNBT() {
    return value.serializeNBT();
  }

  @Override
  public void deserializeNBT(StringTag tag) {
    value.deserializeNBT(tag);
  }

}
