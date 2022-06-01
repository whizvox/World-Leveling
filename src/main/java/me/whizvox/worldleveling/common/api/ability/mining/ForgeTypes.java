package me.whizvox.worldleveling.common.api.ability.mining;

import me.whizvox.worldleveling.common.block.ForgeInterfaceBlock;
import me.whizvox.worldleveling.common.block.ForgeValveBlock;
import me.whizvox.worldleveling.common.block.entity.ForgeInterfaceBlockEntity;
import me.whizvox.worldleveling.common.inventory.menu.ForgeMenu;
import me.whizvox.worldleveling.common.lib.*;
import me.whizvox.worldleveling.common.lib.multiblock.MultiBlockStructure;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public enum ForgeTypes implements IForgeType {

  SOOTY(1, 2000, 1.0) {
    @Override
    public Supplier<? extends Block> getBricksBlock() {
      return WLBlocks.SOOTY_BRICKS;
    }
    @Override
    public Supplier<? extends Item> getBricksItem() {
      return WLItems.SOOTY_BRICKS;
    }
    @Override
    public Supplier<ForgeInterfaceBlock> getInterfaceBlock() {
      return WLBlocks.SOOTY_FORGE_INTERFACE;
    }
    @Override
    public Supplier<? extends Item> getStructureItem() {
      return WLItems.SOOTY_FORGE;
    }
    @Override
    public Supplier<ForgeValveBlock> getValveBlock() {
      return WLBlocks.SOOTY_FORGE_VALVE;
    }
    @Override
    public Supplier<BlockEntityType<ForgeInterfaceBlockEntity>> getBlockEntityType() {
      return WLBlockEntities.SOOTY_FORGE;
    }
    @Override
    public Supplier<MultiBlockStructure> getMultiBlock() {
      return WLMultiBlocks.SOOTY_FORGE;
    }

    @Override
    public Supplier<MenuType<ForgeMenu>> getMenuType() {
      return WLMenus.SOOTY_FORGE;
    }
  },
  DARK(2, 4000, 2.0) {
    @Override
    public Supplier<? extends Block> getBricksBlock() {
      return WLBlocks.DARK_BRICKS;
    }
    @Override
    public Supplier<? extends Item> getBricksItem() {
      return WLItems.DARK_BRICKS;
    }
    @Override
    public Supplier<ForgeInterfaceBlock> getInterfaceBlock() {
      return WLBlocks.DARK_FORGE_INTERFACE;
    }
    @Override
    public Supplier<? extends Item> getStructureItem() {
      return WLItems.DARK_FORGE;
    }
    @Override
    public Supplier<ForgeValveBlock> getValveBlock() {
      return WLBlocks.DARK_FORGE_VALVE;
    }
    @Override
    public Supplier<BlockEntityType<ForgeInterfaceBlockEntity>> getBlockEntityType() {
      return WLBlockEntities.DARK_FORGE;
    }
    @Override
    public Supplier<MenuType<ForgeMenu>> getMenuType() {
      return WLMenus.DARK_FORGE;
    }
    @Override
    public Supplier<MultiBlockStructure> getMultiBlock() {
      return WLMultiBlocks.DARK_FORGE;
    }
  };

  private final int tier;
  private final String serializedName;
  private final Component displayName;
  private final int maxTemperature;
  private final double temperatureChange;

  ForgeTypes(int tier, int maxTemperature, double temperatureChange) {
    this.tier = tier;
    displayName = new TranslatableComponent("menu.worldleveling.forge." + name().toLowerCase());
    this.maxTemperature = maxTemperature;
    this.temperatureChange = temperatureChange;
    serializedName = name().toLowerCase();
  }

  @Override
  public int getTier() {
    return tier;
  }

  @Override
  public String getSerializedName() {
    return serializedName;
  }

  @Override
  public Component getDisplayName() {
    return displayName;
  }

  @Override
  public int getMaxTemperature() {
    return maxTemperature;
  }

  @Override
  public double getTemperatureChange() {
    return temperatureChange;
  }

}
