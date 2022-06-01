package me.whizvox.worldleveling.common.lib;

import me.whizvox.worldleveling.WorldLeveling;
import me.whizvox.worldleveling.common.ability.DigSpeedUpAbility;
import me.whizvox.worldleveling.common.ability.ForgeTierAbility;
import me.whizvox.worldleveling.common.ability.ProspectorAbility;
import me.whizvox.worldleveling.common.ability.UnbreakingToolAbility;
import me.whizvox.worldleveling.common.api.ability.Ability;
import me.whizvox.worldleveling.common.api.ability.AbilityBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;

import static me.whizvox.worldleveling.common.lib.WLRegistries.REG_ABILITIES;

public class WLAbilities {

  /*private static RegistryObject<Ability> register(String name, RegistryObject<SkillType> skill, List<ResourceLocation> itemIcons, int xpos, int ypos, int cost, RegistryObject<Ability>... dependantAbilities) {
    Supplier<Ability> sup;
    if (dependantAbilities.length == 0) {
      sup = () -> new Ability(skill, cost, itemIcons, xpos, ypos, Collections.emptyList());
    } else {
      sup = () -> new Ability(skill, cost, itemIcons, xpos, ypos, Arrays.stream(dependantAbilities).collect(Collectors.toList()));
    }
    return REG_ABILITIES.register(name, sup);
  }

  private static RegistryObject<Ability> register(String name, RegistryObject<SkillType> skill, ResourceLocation itemIcon, int xpos, int ypos, int cost, RegistryObject<Ability>... dependantAbilities) {
    return register(name, skill, List.of(itemIcon), xpos, ypos, cost, dependantAbilities);
  }

  private static ResourceLocation rl(String namespaceAndPath) {
    return new ResourceLocation(namespaceAndPath);
  }

  public static final RegistryObject<Ability>
      PICK_SPEED = register("mining.pick_speed", WLSkills.MINING, rl("minecraft:iron_pickaxe"), -25, 20, 2),
      SHOVEL_SPEED = register("mining.shovel_speed", WLSkills.MINING, rl("minecraft:iron_shovel"), 5, 20, 2),
      AXE_SPEED = register("mining.axe_speed", WLSkills.MINING, rl("minecraft:iron_axe"), 35, 20, 2),
      HOE_SPEED = register("mining.hoe_speed", WLSkills.MINING, rl("minecraft:iron_hoe"), 65, 20, 2),
      ALL_TOOLS_SPEED = register("mining.all_tools_speed", WLSkills.MINING, List.of(rl("minecraft:diamond_pickaxe"), rl("minecraft:diamond_shovel"), rl("minecraft:diamond_axe"), rl("minecraft:diamond_hoe")), 20, 50, 2, PICK_SPEED, SHOVEL_SPEED, AXE_SPEED, HOE_SPEED),
      SOOTY_ARMOR = register("mining.sooty_armor", WLSkills.MINING, rl("minecraft:barrier"), -80, 20, 1),
      SOOTY_STEEL = register("mining.sooty_steel", WLSkills.MINING, rl("minecraft:barrier"), -80, 50, 2, SOOTY_ARMOR),
      PROSPECTOR_1 = register("mining.prospector_1", WLSkills.MINING, rl("minecraft:barrier"), -80, -20, 2),
      PROSPECTOR_2 = register("mining.prospector_2", WLSkills.MINING, rl("minecraft:barrier"), -80, -50, 3, PROSPECTOR_1),
      PROSPECTOR_3 = register("mining.prospector_3", WLSkills.MINING, rl("minecraft:barrier"), -80, -80, 3, PROSPECTOR_2),
      EXTRA_1 = register("mining.extra_1", WLSkills.MINING, rl("minecraft:barrier"), 0, -20, 1),
      EXTRA_2 = register("mining.extra_2", WLSkills.MINING, rl("minecraft:barrier"), 0, -50, 3, EXTRA_1),
      LUCKY_1 = register("mining.lucky_1", WLSkills.MINING, rl("minecraft:barrier"), -40, -20, 2, EXTRA_1),
      LUCKY_2 = register("mining.lucky_2", WLSkills.MINING, rl("minecraft:barrier"), -40, -50, 4, LUCKY_1),
      LUCKY_3 = register("mining.lucky_3", WLSkills.MINING, rl("minecraft:barrier"), -40, -80, 6, LUCKY_2),
      SEISMIC_1 = register("mining.seismic_1", WLSkills.MINING, rl("minecraft:barrier"), 40, -20, 2),
      SEISMIC_2 = register("mining.seismic_2", WLSkills.MINING, rl("minecraft:barrier"), 40, -50, 3, SEISMIC_1),
      HARVEST_1 = register("mining.harvest_1", WLSkills.MINING, rl("minecraft:barrier"), 80, -20, 3),
      HARVEST_2 = register("mining.harvest_2", WLSkills.MINING, rl("minecraft:barrier"), 80, -50, 3, HARVEST_1),
      COMBAT_1 = register("combat_1", WLSkills.COMBAT, rl("minecraft:barrier"), 0, 0, 1),
      COMBAT_2 = register("combat_2", WLSkills.COMBAT, rl("minecraft:barrier"), 50, -50, 1, COMBAT_1),
      COMBAT_3 = register("combat_3", WLSkills.COMBAT, rl("minecraft:barrier"), 50, 0, 1, COMBAT_1),
      COMBAT_4 = register("combat_4", WLSkills.COMBAT, rl("minecraft:barrier"), 50, 50, 1, COMBAT_1),
      COMBAT_5 = register("combat_5", WLSkills.COMBAT, rl("minecraft:barrier"), 0, 50, 1, COMBAT_1),
      COMBAT_6 = register("combat_6", WLSkills.COMBAT, rl("minecraft:barrier"), -50, 50, 1, COMBAT_1),
      COMBAT_7 = register("combat_7", WLSkills.COMBAT, rl("minecraft:barrier"), -50, 0, 1, COMBAT_1),
      COMBAT_8 = register("combat_8", WLSkills.COMBAT, rl("minecraft:barrier"), -50, -50, 1, COMBAT_1),
      COMBAT_9 = register("combat_9", WLSkills.COMBAT, rl("minecraft:barrier"), 0, -50, 1, COMBAT_1);*/

  public static void register(IEventBus bus) {
    REG_ABILITIES.register(bus);
  }

  private static <T extends Ability> RegistryObject<T> register(String name, AbilityBuilder<T> builder) {
    if (builder.hasNoIcons()) {
      builder.defaultTextureIcon(new ResourceLocation(WorldLeveling.MOD_ID, name));
    }
    return REG_ABILITIES.register(name, builder::build);
  }

  public static final RegistryObject<DigSpeedUpAbility>
      MINING_PICKAXE_SPEED_UP,
      MINING_SHOVEL_SPEED_UP,
      MINING_AXE_SPEED_UP,
      MINING_HOE_SPEED_UP,
      MINING_ALL_TOOLS_SPEED_UP;
  public static final RegistryObject<ForgeTierAbility>
      MINING_SOOTY_ALLOY,
      MINING_DARK_ALLOY;
  public static final RegistryObject<ProspectorAbility>
      MINING_PROSPECTOR_1,
      MINING_PROSPECTOR_2,
      MINING_PROSPECTOR_3;
  public static final RegistryObject<UnbreakingToolAbility>
      MINING_UNBREAKING_1,
      MINING_UNBREAKING_2;

  static {
    MINING_PICKAXE_SPEED_UP = register("mining/pickaxe_speed_up", new AbilityBuilder<DigSpeedUpAbility>(2, -25, 20)
        .skill(WLSkills.MINING)
        .itemIcon(() -> Items.IRON_PICKAXE)
        .constructor(builder -> new DigSpeedUpAbility(builder, PickaxeItem.class))
    );
    MINING_SHOVEL_SPEED_UP = register("mining/shovel_speed_up", new AbilityBuilder<DigSpeedUpAbility>(2, 5, 20)
        .skill(WLSkills.MINING)
        .itemIcon(() -> Items.IRON_SHOVEL)
        .constructor(builder -> new DigSpeedUpAbility(builder, ShovelItem.class))
    );
    MINING_AXE_SPEED_UP = register("mining/axe_speed_up", new AbilityBuilder<DigSpeedUpAbility>(2, 35, 20)
        .skill(WLSkills.MINING)
        .itemIcon(() -> Items.IRON_AXE)
        .constructor(builder -> new DigSpeedUpAbility(builder, AxeItem.class))
    );
    MINING_HOE_SPEED_UP = register("mining/hoe_speed_up", new AbilityBuilder<DigSpeedUpAbility>(2, 65, 20)
        .skill(WLSkills.MINING)
        .itemIcon(() -> Items.IRON_HOE)
        .constructor(builder -> new DigSpeedUpAbility(builder, HoeItem.class))
    );
    MINING_ALL_TOOLS_SPEED_UP = register("mining/all_tools_speed_up", new AbilityBuilder<DigSpeedUpAbility>(2, 20, 50)
        .dependency(MINING_PICKAXE_SPEED_UP)
        .dependency(MINING_SHOVEL_SPEED_UP)
        .dependency(MINING_AXE_SPEED_UP)
        .dependency(MINING_HOE_SPEED_UP)
        .replace(MINING_PICKAXE_SPEED_UP)
        .replace(MINING_SHOVEL_SPEED_UP)
        .replace(MINING_AXE_SPEED_UP)
        .replace(MINING_HOE_SPEED_UP)
        .skill(WLSkills.MINING)
        .itemIcon(() -> Items.DIAMOND_PICKAXE)
        .itemIcon(() -> Items.DIAMOND_SHOVEL)
        .itemIcon(() -> Items.DIAMOND_AXE)
        .itemIcon(() -> Items.DIAMOND_HOE)
        .constructor(builder -> new DigSpeedUpAbility(builder, null))
    );
    MINING_SOOTY_ALLOY = register("mining/sooty_alloy", new AbilityBuilder<ForgeTierAbility>(1, -80, 20)
        .skill(WLSkills.MINING)
        .itemIcon(WLItems.SOOTY_ALLOY_INGOT)
        .constructor(builder -> new ForgeTierAbility(builder, 1))
    );
    MINING_DARK_ALLOY = register("mining/dark_alloy", new AbilityBuilder<ForgeTierAbility>(2, -80, 50)
        .skill(WLSkills.MINING)
        .dependency(MINING_SOOTY_ALLOY)
        .replace(MINING_SOOTY_ALLOY)
        .itemIcon(WLItems.DARK_ALLOY_INGOT)
        .constructor(builder -> new ForgeTierAbility(builder, 2))
    );
    MINING_PROSPECTOR_1 = register("mining/prospector_1", new AbilityBuilder<ProspectorAbility>(2, -80, -20)
        .skill(WLSkills.MINING)
        .constructor(builder -> new ProspectorAbility(builder, 1))
    );
    MINING_PROSPECTOR_2 = register("mining/prospector_2", new AbilityBuilder<ProspectorAbility>(2, -80, -50)
        .skill(WLSkills.MINING)
        .dependency(MINING_PROSPECTOR_1)
        .replace(MINING_PROSPECTOR_1)
        .constructor(builder -> new ProspectorAbility(builder, 2))
    );
    MINING_PROSPECTOR_3 = register("mining/prospector_3", new AbilityBuilder<ProspectorAbility>(2, -80, -80)
        .skill(WLSkills.MINING)
        .dependency(MINING_PROSPECTOR_2)
        .replace(MINING_PROSPECTOR_2)
        .constructor(builder -> new ProspectorAbility(builder, 3))
    );
    MINING_UNBREAKING_1 = register("mining/unbreaking_1", new AbilityBuilder<UnbreakingToolAbility>(1, 0, -20)
        .skill(WLSkills.MINING)
        .constructor(builder -> new UnbreakingToolAbility(builder, 1))
    );
    MINING_UNBREAKING_2 = register("mining/unbreaking_2", new AbilityBuilder<UnbreakingToolAbility>(3, 0, -50)
        .skill(WLSkills.MINING)
        .dependency(MINING_UNBREAKING_1)
        .replace(MINING_UNBREAKING_1)
        .constructor(builder -> new UnbreakingToolAbility(builder, 2))
    );
  }

}
