package me.whizvox.worldleveling.datagen.common.loot;

import me.whizvox.worldleveling.common.api.ability.mining.ForgeTypes;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.HashSet;
import java.util.Set;

public class WLBlockLoot extends BlockLoot {

  private final Set<Block> knownBlocks;

  public WLBlockLoot() {
    knownBlocks = new HashSet<>();
  }

  @Override
  protected void add(Block block, LootTable.Builder builder) {
    super.add(block, builder);
    knownBlocks.add(block);
  }

  // for some dumb reason the generic version of this method only accepts types that are StringRepresentable
  private LootTable.Builder createSinglePropConditionTable(Block block, BooleanProperty prop, boolean value) {
    return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(block).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(prop, value))))));
  }

  @Override
  protected Iterable<Block> getKnownBlocks() {
    return knownBlocks;
  }

  @Override
  protected void addTables() {
    for (ForgeTypes forgeType : ForgeTypes.values()) {
      dropSelf(forgeType.getBricksBlock().get());
      dropOther(forgeType.getInterfaceBlock().get(), forgeType.getStructureItem().get());
      dropOther(forgeType.getValveBlock().get(), forgeType.getBricksBlock().get());
    }
  }

}
