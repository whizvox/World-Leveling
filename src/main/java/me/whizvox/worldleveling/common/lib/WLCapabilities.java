package me.whizvox.worldleveling.common.lib;

import me.whizvox.worldleveling.common.capability.PlayerSkills;
import me.whizvox.worldleveling.common.capability.ProspectedBlock;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class WLCapabilities {

  public static final Capability<PlayerSkills> PLAYER_SKILLS = CapabilityManager.get(new CapabilityToken<>() {});

  public static final Capability<ProspectedBlock> PROSPECTED_BLOCK = CapabilityManager.get(new CapabilityToken<>() {});

  public static void register(RegisterCapabilitiesEvent event) {
    event.register(PlayerSkills.class);
    event.register(ProspectedBlock.class);
  }

}
