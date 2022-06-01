package me.whizvox.worldleveling.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

public class WLKeyBindings {

  public static final KeyMapping OPEN_SKILLS_MENU = new KeyMapping("key.worldleveling.openSkillsMenu", InputConstants.KEY_O, "key.categories.worldleveling");

  public static void register() {
    ClientRegistry.registerKeyBinding(OPEN_SKILLS_MENU);
  }

}
