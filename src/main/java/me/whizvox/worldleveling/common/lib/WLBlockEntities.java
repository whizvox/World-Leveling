package me.whizvox.worldleveling.common.lib;

import me.whizvox.worldleveling.WorldLeveling;
import me.whizvox.worldleveling.common.api.ability.mining.ForgeTypes;
import me.whizvox.worldleveling.common.block.entity.ForgeInterfaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WLBlockEntities {

  private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, WorldLeveling.MOD_ID);

  public static void register(IEventBus bus) {
    BLOCK_ENTITIES.register(bus);
  }

  public static final RegistryObject<BlockEntityType<ForgeInterfaceBlockEntity>>
      SOOTY_FORGE = BLOCK_ENTITIES.register("sooty_forge", () -> BlockEntityType.Builder.of((pos, state) ->
              new ForgeInterfaceBlockEntity(ForgeTypes.SOOTY, pos, state), WLBlocks.SOOTY_FORGE_INTERFACE.get()
          ).build(null)),
      DARK_FORGE = BLOCK_ENTITIES.register("dark_forge", () -> BlockEntityType.Builder.of((pos, state) ->
              new ForgeInterfaceBlockEntity(ForgeTypes.DARK, pos, state), WLBlocks.DARK_FORGE_INTERFACE.get()
          ).build(null));
}
