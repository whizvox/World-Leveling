package me.whizvox.worldleveling;

import me.whizvox.worldleveling.client.WLKeyBindings;
import me.whizvox.worldleveling.client.event.ClientEventListeners;
import me.whizvox.worldleveling.client.render.ExposedBlocksRenderManager;
import me.whizvox.worldleveling.client.render.ForgeOutlineRenderManager;
import me.whizvox.worldleveling.client.screen.menu.ForgeScreen;
import me.whizvox.worldleveling.common.capability.PlayerSkillsCapabilityProvider;
import me.whizvox.worldleveling.common.event.WorldEventListeners;
import me.whizvox.worldleveling.common.lib.*;
import me.whizvox.worldleveling.common.network.WLNetwork;
import me.whizvox.worldleveling.datagen.WLBlockStateProvider;
import me.whizvox.worldleveling.datagen.WLItemModelProvider;
import me.whizvox.worldleveling.server.command.WLCommand;
import me.whizvox.worldleveling.server.event.ServerEventListeners;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod(WorldLeveling.MOD_ID)
public class WorldLeveling {

  public static final String MOD_ID = "worldleveling";

  private static IEventBus modBus() {
    return FMLJavaModLoadingContext.get().getModEventBus();
  }

  public WorldLeveling() {
    IEventBus modBus = modBus();
    WLRegistries.register(modBus);
    WLSkills.register(modBus);
    WLAbilities.register(modBus);
    WLBlocks.register(modBus);
    WLItems.register(modBus);
    WLBlockEntities.register(modBus);
    WLSounds.register(modBus);
    WLMenus.register(modBus);
    modBus.addListener(this::onCommonSetup);
    modBus.addListener(this::onClientSetup);
    modBus.addListener(this::registerCapabilities);
    modBus.addListener(this::gatherData);

    IEventBus forgeBus = MinecraftForge.EVENT_BUS;
    forgeBus.addGenericListener(Entity.class, this::attachEntityCapabilities);
    forgeBus.addListener(this::registerCommands);
    WorldEventListeners.register(forgeBus);
    ServerEventListeners.register(forgeBus);
    ClientEventListeners.register(forgeBus);
    WLCaches.register(forgeBus);
    WLConfigs.register(modBus, forgeBus);
  }

  private void onCommonSetup(final FMLCommonSetupEvent event) {
    final IEventBus forgeBus = MinecraftForge.EVENT_BUS;
    WLRegistries.SKILLS.get().forEach(skillType -> skillType.addListeners(forgeBus));
    WLRegistries.ABILITIES.get().forEach(ability -> ability.addListeners(forgeBus));
    WLNetwork.registerPackets();
  }

  private void onClientSetup(final FMLClientSetupEvent event) {
    WLKeyBindings.register();
    ExposedBlocksRenderManager.register(MinecraftForge.EVENT_BUS);
    ForgeOutlineRenderManager.register(MinecraftForge.EVENT_BUS);

    MenuScreens.register(WLMenus.SOOTY_FORGE.get(), ForgeScreen::new);
    MenuScreens.register(WLMenus.DARK_FORGE.get(), ForgeScreen::new);
  }

  private void registerCapabilities(final RegisterCapabilitiesEvent event) {
    WLCapabilities.register(event);
  }

  private void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    ExistingFileHelper fileHelper = event.getExistingFileHelper();
    gen.addProvider(new WLBlockStateProvider(gen, fileHelper));
    gen.addProvider(new WLItemModelProvider(gen, fileHelper));
  }

  private void attachEntityCapabilities(final AttachCapabilitiesEvent<Entity> event) {
    Entity entity = event.getObject();
    if (entity instanceof Player && !(entity instanceof FakePlayer)) {
      event.addCapability(new ResourceLocation(MOD_ID, "player_level"), new PlayerSkillsCapabilityProvider());
    }
  }

  private void registerCommands(final RegisterCommandsEvent event) {
    WLCommand.register(event.getDispatcher());
  }

}
