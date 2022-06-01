package me.whizvox.worldleveling.common.capability;

import me.whizvox.worldleveling.common.lib.WLCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerSkillsCapabilityProvider implements ICapabilitySerializable<CompoundTag> {

  private final PlayerSkills playerSkills;
  private final LazyOptional<PlayerSkills> capability;

  public PlayerSkillsCapabilityProvider() {
    playerSkills = new PlayerSkills();
    capability = LazyOptional.of(() -> playerSkills);
  }

  @NotNull
  @Override
  public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    return cap == WLCapabilities.PLAYER_SKILLS ? capability.cast() : LazyOptional.empty();
  }

  @Override
  public CompoundTag serializeNBT() {
    return playerSkills.serializeNBT();
  }

  @Override
  public void deserializeNBT(CompoundTag nbt) {
    playerSkills.deserializeNBT(nbt);
  }

}
