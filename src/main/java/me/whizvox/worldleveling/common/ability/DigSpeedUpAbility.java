package me.whizvox.worldleveling.common.ability;

import me.whizvox.worldleveling.common.api.ability.Ability;
import me.whizvox.worldleveling.common.api.ability.AbilityBuilder;
import me.whizvox.worldleveling.common.util.SkillsHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.Nullable;

public class DigSpeedUpAbility extends Ability {

  private final Class<? extends Item> filterItemClass;

  public DigSpeedUpAbility(AbilityBuilder<?> builder, @Nullable Class<? extends Item> filterItemClass) {
    super(builder);
    this.filterItemClass = filterItemClass;
  }

  @Override
  public void addListeners(IEventBus bus) {
    bus.addListener(this::onCalculateBreakSpeed);
  }

  private void onCalculateBreakSpeed(final PlayerEvent.BreakSpeed event) {
    Player player = event.getPlayer();
    ItemStack tool = player.getMainHandItem();
    if (filterItemClass == null || !tool.isEmpty()) {
      SkillsHelper.handle(player, skills -> {
        if (skills.isAbilityActive(this)) {
          if (filterItemClass == null) {
            event.setNewSpeed(event.getOriginalSpeed() * 1.6F);
          } else if (filterItemClass.isAssignableFrom(tool.getItem().getClass())) {
            event.setNewSpeed(event.getOriginalSpeed() * 1.3F);
          }
        }
      });
    }
  }

}
