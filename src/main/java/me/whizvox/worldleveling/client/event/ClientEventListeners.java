package me.whizvox.worldleveling.client.event;

import me.whizvox.worldleveling.client.WLKeyBindings;
import me.whizvox.worldleveling.client.screen.SkillsScreen;
import me.whizvox.worldleveling.common.util.SkillsHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientEventListeners {

  public static void register(IEventBus bus) {
    bus.addListener(ClientEventListeners::onKeyInput);
  }

  private static void onKeyInput(final InputEvent.KeyInputEvent event) {
    if (WLKeyBindings.OPEN_SKILLS_MENU.consumeClick() && Minecraft.getInstance().screen == null) {
      if (Minecraft.getInstance().player != null) {
        SkillsHelper.handle(Minecraft.getInstance().player, skills ->
            Minecraft.getInstance().setScreen(new SkillsScreen(skills, null))
        );
      }
    }
  }

}
