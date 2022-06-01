package me.whizvox.worldleveling.common.lib;

import me.whizvox.worldleveling.WorldLeveling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WLSounds {

  private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, WorldLeveling.MOD_ID);

  public static void register(IEventBus bus) {
    SOUNDS.register(bus);
  }

  private static RegistryObject<SoundEvent> register(String name) {
    return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(WorldLeveling.MOD_ID, name)));
  }

  public static final RegistryObject<SoundEvent>
      PROSPECTORS_PICK = register("misc.prospectors_pick");

}
