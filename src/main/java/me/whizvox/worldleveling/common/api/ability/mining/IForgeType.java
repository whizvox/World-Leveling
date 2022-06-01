package me.whizvox.worldleveling.common.api.ability.mining;

import me.whizvox.worldleveling.common.block.ForgeInterfaceBlock;
import me.whizvox.worldleveling.common.block.ForgeValveBlock;
import me.whizvox.worldleveling.common.block.entity.ForgeInterfaceBlockEntity;
import me.whizvox.worldleveling.common.inventory.menu.ForgeMenu;
import me.whizvox.worldleveling.common.lib.multiblock.MultiBlockStructure;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public interface IForgeType extends StringRepresentable {

  int getTier();

  Component getDisplayName();

  int getMaxTemperature();

  double getTemperatureChange();

  Supplier<? extends Block> getBricksBlock();

  Supplier<? extends Item> getBricksItem();

  Supplier<ForgeInterfaceBlock> getInterfaceBlock();

  Supplier<? extends Item> getStructureItem();

  Supplier<ForgeValveBlock> getValveBlock();

  Supplier<MultiBlockStructure> getMultiBlock();

  Supplier<BlockEntityType<ForgeInterfaceBlockEntity>> getBlockEntityType();

  Supplier<MenuType<ForgeMenu>> getMenuType();

}
