package me.whizvox.worldleveling.server.event;

import me.whizvox.worldleveling.common.network.WLNetwork;
import me.whizvox.worldleveling.common.network.message.SyncPlayerSkillsMessage;
import me.whizvox.worldleveling.common.util.SkillsHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class ServerEventListeners {

  public static void register(IEventBus bus) {
    bus.addListener(ServerEventListeners::onEntityJoin);
  }

  private static void onEntityJoin(final EntityJoinWorldEvent event) {
    if (event.getEntity() instanceof ServerPlayer player) {
      SkillsHelper.handle(player, skills ->
          WLNetwork.sendToClient(player, SyncPlayerSkillsMessage.create(player))
      );
    }
  }

  private static void onReload() {

  }

}
