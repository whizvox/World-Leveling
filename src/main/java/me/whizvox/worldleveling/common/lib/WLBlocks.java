package me.whizvox.worldleveling.common.lib;

import me.whizvox.worldleveling.WorldLeveling;
import me.whizvox.worldleveling.common.api.ability.mining.ForgeTypes;
import me.whizvox.worldleveling.common.block.ForgeInterfaceBlock;
import me.whizvox.worldleveling.common.block.ForgeValveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WLBlocks {

  private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, WorldLeveling.MOD_ID);

  public static void register(IEventBus bus) {
    BLOCKS.register(bus);
  }

  public static final RegistryObject<Block>
      SOOTY_BRICKS = BLOCKS.register("sooty_bricks", () -> new Block(BlockBehaviour.Properties.of(Material.STONE))),
      DARK_BRICKS = BLOCKS.register("dark_bricks", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));

  public static final RegistryObject<ForgeInterfaceBlock>
      SOOTY_FORGE_INTERFACE = BLOCKS.register("sooty_forge_interface", () -> new ForgeInterfaceBlock(ForgeTypes.SOOTY)),
      DARK_FORGE_INTERFACE = BLOCKS.register("dark_forge_interface", () -> new ForgeInterfaceBlock(ForgeTypes.DARK));

  public static final RegistryObject<ForgeValveBlock>
      SOOTY_FORGE_VALVE = BLOCKS.register("sooty_forge_valve", () -> new ForgeValveBlock(ForgeTypes.SOOTY)),
      DARK_FORGE_VALVE = BLOCKS.register("dark_forge_valve", () -> new ForgeValveBlock(ForgeTypes.DARK));


}
