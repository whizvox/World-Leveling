package me.whizvox.worldleveling.common.block.entity;

import me.whizvox.worldleveling.common.api.ability.mining.IForgeType;
import me.whizvox.worldleveling.common.block.ForgeInterfaceBlock;
import me.whizvox.worldleveling.common.inventory.menu.ForgeMenu;
import me.whizvox.worldleveling.common.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public class ForgeInterfaceBlockEntity extends BlockEntity implements MenuProvider {

  public static final int
      SLOTS_INPUT = 3,
      SLOTS_FUEL = 3,
      SLOTS_OUTPUT = 6,
      TOTAL_SLOTS = SLOTS_INPUT + SLOTS_FUEL + SLOTS_OUTPUT,

      SLOT_INPUT_START = 0,
      SLOT_INPUT_END = SLOT_INPUT_START + SLOTS_INPUT - 1,
      SLOT_FUEL_START = SLOT_INPUT_END + 1,
      SLOT_FUEL_END = SLOT_FUEL_START + SLOTS_FUEL - 1,
      SLOT_OUTPUT_START = SLOT_FUEL_END + 1,
      SLOT_OUTPUT_END = SLOT_OUTPUT_START + SLOTS_OUTPUT - 1;

  public static final double
      MIN_SMELTING_TEMPERATURE = 1000.0,
      COOLING_RATE = 0.5;

  public static final int
      COUNT_DATA = 6,
      DATA_TEMPERATURE = 0,
      DATA_MAX_TEMPERATURE = 1,
      DATA_SMELT_TIME = 2,
      DATA_SMELT_PROGRESS = 3,
      DATA_FUEL_TIME = 4,
      DATA_FUEL_PROGRESS = 5;

  private final IForgeType forgeType;

  private boolean formed;
  private final Inventory inventory;
  private final RangedWrapper inputInventory;
  private final RangedWrapper fuelInventory;
  private final RangedWrapper outputInventory;
  private int totalSmeltTime;
  private double temperature;
  private int totalFuelTime;
  private int fuelProgress;
  private double smeltProgress;
  private double processRate;

  // processing caches
  private boolean shouldProcess;
  private SmeltingRecipe[] cachedRecipes;
  private ItemStackHandler mockOutputInventory;

  // fuel caches
  private boolean hasValidFuel;

  private final ContainerData dataAccess;

  public ForgeInterfaceBlockEntity(IForgeType forgeType, BlockPos pos, BlockState state) {
    super(forgeType.getBlockEntityType().get(), pos, state);
    this.forgeType = forgeType;
    formed = true;
    inventory = new Inventory();
    inputInventory = new RangedWrapper(inventory, SLOT_INPUT_START, SLOT_INPUT_END + 1);
    fuelInventory = new RangedWrapper(inventory, SLOT_FUEL_START, SLOT_FUEL_END + 1);
    outputInventory = new RangedWrapper(inventory, SLOT_OUTPUT_START, SLOT_OUTPUT_END + 1);
    totalSmeltTime = 0;
    temperature = 0;
    fuelProgress = 0;
    smeltProgress = 0.0;
    processRate = 0.0;

    shouldProcess = false;
    cachedRecipes = new SmeltingRecipe[SLOTS_INPUT];
    mockOutputInventory = new ItemStackHandler(SLOTS_OUTPUT);
    hasValidFuel = false;

    dataAccess = new ContainerData() {
      @Override
      public int get(int index) {
        return switch (index) {
          case DATA_TEMPERATURE -> (int) temperature;
          case DATA_MAX_TEMPERATURE -> forgeType.getMaxTemperature();
          case DATA_SMELT_TIME -> totalSmeltTime;
          case DATA_SMELT_PROGRESS -> (int) smeltProgress;
          case DATA_FUEL_TIME -> totalFuelTime;
          case DATA_FUEL_PROGRESS -> fuelProgress;
          default -> throw new IndexOutOfBoundsException(index);
        };
      }
      @Override
      public void set(int index, int value) {
      }
      @Override
      public int getCount() {
        return COUNT_DATA;
      }
    };
  }

  public IForgeType getForgeType() {
    return forgeType;
  }

  @Override
  public void setLevel(Level level) {
    super.setLevel(level);
    refreshProcessingCaches();
    refreshFuelCaches();
    totalSmeltTime = calculateSmeltingTime();
    totalFuelTime = getBurnTime(getFirstValidFuelItem());
  }

  public IItemHandler getFullInventory() {
    return inventory;
  }

  public IItemHandler getInputInventory() {
    return inputInventory;
  }

  public IItemHandler getFuelInventory() {
    return fuelInventory;
  }

  public IItemHandler getOutputInventory() {
    return outputInventory;
  }

  public boolean isStructureFormed(Level level, BlockPos pos, BlockState state) {
    return true;
  }

  public double calculateProcessRate() {
    if (temperature < MIN_SMELTING_TEMPERATURE) {
      return 0.0;
    }
    // i.e. twice the min temperature = 2x processing speed
    return temperature / MIN_SMELTING_TEMPERATURE;
  }

  public int calculateSmeltingTime() {
    int maxBurnTime = 0;
    for (SmeltingRecipe recipe : cachedRecipes) {
      if (recipe != null && recipe.getCookingTime() > maxBurnTime) {
        maxBurnTime = recipe.getCookingTime();
      }
    }
    return maxBurnTime;
  }

  private int getBurnTime(ItemStack stack) {
    return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
  }

  private ItemStack getFirstValidFuelItem() {
    for (int i = 0; i < SLOTS_FUEL; i++) {
      ItemStack stack = inventory.getStackInSlot(SLOT_FUEL_START + i);
      if (getBurnTime(stack) > 0) {
        return stack;
      }
    }
    return ItemStack.EMPTY;
  }

  private ItemStack insertItem(ItemStack stack, boolean simulate, int startSlot, int endSlot) {
    ItemStack leftover = ItemStack.EMPTY;
    for (int i = startSlot; i <= endSlot; i++) {
      leftover = inventory.insertItem(i, stack, simulate);
    }
    return leftover;
  }

  private void updateTemperature(int maxTemperature, double delta) {
    if (delta != 0.0) {
      temperature = Mth.clamp(temperature + delta, 0.0, maxTemperature);
      processRate = calculateProcessRate();
    }
  }

  @Nullable
  private SmeltingRecipe fetchSmeltingRecipe(ItemStack stack) {
    return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level).orElse(null);
  }

  private void refreshProcessingCaches() {
    for (int i = 0; i < SLOTS_OUTPUT; i++) {
      mockOutputInventory.setStackInSlot(i, outputInventory.getStackInSlot(i).copy());
    }

    Arrays.fill(cachedRecipes, null);
    shouldProcess = false;
    ItemStack overflow = ItemStack.EMPTY;
    boolean hasRecipe = false;
    for (int i = 0; i < SLOTS_INPUT; i++) {
      SmeltingRecipe recipe = fetchSmeltingRecipe(inventory.getStackInSlot(i + SLOT_INPUT_START));
      if (recipe != null) {
        cachedRecipes[i] = recipe;
        hasRecipe = true;
        if (overflow.isEmpty()) {
          ItemStack output = recipe.getResultItem().copy();
          if (!output.isEmpty()) {
            overflow = InventoryUtils.insertIntoInventory(mockOutputInventory, output, false);
          }
        }
      }
    }
    totalSmeltTime = calculateSmeltingTime();
    shouldProcess = hasRecipe && overflow.isEmpty();
    if (!shouldProcess && smeltProgress > 0) {
      smeltProgress = 0;
    }
  }

  private void refreshFuelCaches() {
    hasValidFuel = !getFirstValidFuelItem().isEmpty();
  }

  public ItemStack addToInput(ItemStack stack, boolean simulate) {
    return insertItem(stack, simulate, SLOT_INPUT_START, SLOT_INPUT_END);
  }

  public ItemStack addToFuel(ItemStack stack, boolean simulate) {
    return insertItem(stack, simulate, SLOT_FUEL_START, SLOT_FUEL_END);
  }

  public ItemStack addToOutput(ItemStack stack, boolean simulate) {
    return insertItem(stack, simulate, SLOT_OUTPUT_START, SLOT_OUTPUT_END);
  }

  public boolean attemptForm(Level level, BlockPos pos, BlockState state) {
    if (formed) {
      return true;
    }
    return true;
  }

  public void balanceInputItems() {
  }

  @Override
  protected void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    tag.putBoolean("formed", formed);
    tag.put("inventory", inventory.serializeNBT());
    tag.putDouble("temperature", temperature);
    tag.putInt("fuelTime", fuelProgress);
    tag.putDouble("smeltProgress", smeltProgress);
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    formed = tag.getBoolean("formed");
    inventory.deserializeNBT(tag.getCompound("inventory"));
    temperature = tag.getDouble("temperature");
    fuelProgress = tag.getInt("fuelTime");
    smeltProgress = tag.getDouble("smeltProgress");
    processRate = calculateProcessRate();
  }

  @Override
  public Component getDisplayName() {
    return forgeType.getDisplayName();
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int windowId, net.minecraft.world.entity.player.Inventory playerInv, Player player) {
    return new ForgeMenu(forgeType.getMenuType().get(), windowId, playerInv, this, inventory, dataAccess);
  }

  private class Inventory extends ItemStackHandler {

    public Inventory() {
      super(TOTAL_SLOTS);
    }

    private Optional<SmeltingRecipe> getSmeltingRecipe(ItemStack stack) {
      return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level);
    }

    private void refreshCaches(int slot) {
      if ((slot >= SLOT_INPUT_START && slot <= SLOT_INPUT_END) ||
          (slot >= SLOT_OUTPUT_START && slot <= SLOT_OUTPUT_END)) {
        refreshProcessingCaches();
      } else if (slot >= SLOT_FUEL_START && slot <= SLOT_FUEL_END) {
        refreshFuelCaches();
      }
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
      ItemStack leftover = super.insertItem(slot, stack, simulate);
      if (!simulate) {
        refreshCaches(slot);
      }
      return leftover;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
      super.setStackInSlot(slot, stack);
      refreshCaches(slot);
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
      ItemStack result = super.extractItem(slot, amount, simulate);
      if (!simulate) {
        refreshCaches(slot);
      }
      return result;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
      if (slot >= SLOT_INPUT_START && slot <= SLOT_INPUT_END) {
        return getSmeltingRecipe(stack).isPresent();
      } else if (slot >= SLOT_FUEL_START && slot <= SLOT_FUEL_END) {
        return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
      }
      return true;
    }

  }

  public static <T extends BlockEntity> void tick(Level world, BlockPos pos, BlockState state, T be) {
    if (world.isClientSide) {
      return;
    }
    ForgeInterfaceBlockEntity forge = (ForgeInterfaceBlockEntity) be;
    if (!forge.formed) {
      return;
    }
    IForgeType forgeType = ((ForgeInterfaceBlock) forge.getBlockState().getBlock()).forgeType;
    if (forge.fuelProgress <= 0) {
      if (forge.hasValidFuel && forge.shouldProcess) {
        ItemStack stack = forge.getFirstValidFuelItem();
        if (stack.isEmpty()) {
          forge.hasValidFuel = false;
        } else {
          int burnTime = forge.getBurnTime(stack);
          stack.shrink(1);
          forge.refreshFuelCaches();
          forge.fuelProgress = burnTime;
          forge.totalFuelTime = burnTime;
          if (!state.getValue(ForgeInterfaceBlock.LIT)) {
            world.setBlock(pos, state.setValue(ForgeInterfaceBlock.LIT, true), 3);
          }
        }
      }
    }
    if (forge.fuelProgress > 0) {
      forge.updateTemperature(forgeType.getMaxTemperature(), forgeType.getTemperatureChange());
      if (forge.shouldProcess && forge.temperature >= MIN_SMELTING_TEMPERATURE) {
        if (forge.totalSmeltTime > 0 && forge.smeltProgress >= forge.totalSmeltTime) {
          forge.refreshProcessingCaches();
          if (forge.shouldProcess) {
            for (int i = 0; i < forge.cachedRecipes.length; i++) {
              SmeltingRecipe recipe = forge.cachedRecipes[i];
              if (recipe != null) {
                scanInputSlotsLoop:
                for (int j = 0; j < forge.inputInventory.getSlots(); j++) {
                  ItemStack inputStack = forge.inputInventory.getStackInSlot(i);
                  for (Ingredient ingredient : recipe.getIngredients()) {
                    for (ItemStack recipeInputStack : ingredient.getItems()) {
                      if (inputStack.sameItemStackIgnoreDurability(recipeInputStack)) {
                        inputStack.shrink(recipeInputStack.getCount());
                        break scanInputSlotsLoop;
                      }
                    }
                  }
                }
                InventoryUtils.insertIntoInventory(forge.outputInventory, recipe.getResultItem().copy(), false);
              }
            }
          }
          forge.smeltProgress = 0;
          forge.totalSmeltTime = forge.calculateSmeltingTime();
        } else {
          forge.smeltProgress = Mth.clamp(forge.smeltProgress + forge.processRate, 0.0, forge.totalSmeltTime);
        }
      }
      forge.fuelProgress--;
    } else {
      if (state.getValue(ForgeInterfaceBlock.LIT)) {
        world.setBlock(pos, state.setValue(ForgeInterfaceBlock.LIT, false), 3);
      }
      forge.updateTemperature(forgeType.getMaxTemperature(), -COOLING_RATE);
      if (forge.smeltProgress > 0) {
        forge.smeltProgress = 0;
      }
    }
  }

}
