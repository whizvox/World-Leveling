package me.whizvox.worldleveling.common.lib;

import me.whizvox.worldleveling.WorldLeveling;
import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.api.ability.Ability;
import me.whizvox.worldleveling.common.lib.internal.WLKeys;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;

public class WLRegistries {

  protected static final DeferredRegister<SkillType> REG_SKILLS = DeferredRegister.create(WLKeys.REGISTRY_SKILLS, WorldLeveling.MOD_ID);
  protected static final DeferredRegister<Ability> REG_ABILITIES = DeferredRegister.create(WLKeys.REGISTRY_ABILITIES, WorldLeveling.MOD_ID);

  public static final Lazy<IForgeRegistry<SkillType>> SKILLS = Lazy.of(() -> RegistryManager.ACTIVE.getRegistry(WLKeys.REGISTRY_SKILLS));
  public static final Lazy<IForgeRegistry<Ability>> ABILITIES = Lazy.of(() -> RegistryManager.ACTIVE.getRegistry(WLKeys.REGISTRY_ABILITIES));

  public static void register(IEventBus bus) {
    REG_SKILLS.makeRegistry(SkillType.class, () -> new RegistryBuilder<SkillType>()
        .allowModification()
        .disableSaving()
    );
    REG_ABILITIES.makeRegistry(Ability.class, () -> new RegistryBuilder<Ability>()
        .allowModification()
        .disableSaving()
    );
  }

}
