package me.whizvox.worldleveling.common.ability;

import me.whizvox.worldleveling.common.api.ability.Ability;
import me.whizvox.worldleveling.common.api.ability.AbilityBuilder;
import me.whizvox.worldleveling.common.event.ItemDamagedEvent;
import me.whizvox.worldleveling.common.util.SkillsHelper;
import net.minecraft.world.item.DiggerItem;
import net.minecraftforge.eventbus.api.IEventBus;

public class UnbreakingToolAbility extends Ability {

  public final byte level;

  public UnbreakingToolAbility(AbilityBuilder<?> builder, int level) {
    super(builder);
    this.level = (byte) level;
  }

  @Override
  public void addListeners(IEventBus bus) {
    bus.addListener(this::onToolDamage);
  }

  private void onToolDamage(final ItemDamagedEvent event) {
    if (event.getPlayer() != null) {
      SkillsHelper.handle(event.getPlayer(), skills -> {
        if (skills.isAbilityActive(this) && !event.getStack().isEmpty() && event.getStack().getItem() instanceof DiggerItem) {
          for (int i = 0; i < event.getDamage() && !event.isCanceled(); i++) {
            if (event.getRandom().nextInt(level + 1) > 0) {
              event.setDamage(event.getDamage() - 1);
            }
          }
        }
      });
    }
  }

}
