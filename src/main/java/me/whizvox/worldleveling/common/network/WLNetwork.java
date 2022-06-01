package me.whizvox.worldleveling.common.network;

import me.whizvox.worldleveling.common.lib.internal.WLKeys;
import me.whizvox.worldleveling.common.network.message.*;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class WLNetwork {

  private static final String PROTOCOL_VERSION = "1";

  private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
      WLKeys.NETWORK_CHANNEL_MAIN,
      () -> PROTOCOL_VERSION,
      PROTOCOL_VERSION::equals,
      PROTOCOL_VERSION::equals
  );

  private static <MSG> void register(int id, MessageHandler<MSG> handler) {
    CHANNEL.registerMessage(id, handler.getType(), handler::encode, handler::decode, (msg, ctxSup) -> {
      NetworkEvent.Context ctx = ctxSup.get();
      ctx.enqueueWork(() -> handler.handle(msg, ctx.getSender()));
      ctx.setPacketHandled(true);
    });
  }

  public static void registerPackets() {
    int id = 0;

    register(id++, SyncPlayerSkillsMessage.HANDLER);
    register(id++, IncreasePlayerSkillExperienceMessage.HANDLER);
    register(id++, UpdateAbilityMessage.HANDLER);
    register(id++, ShowOresMessage.HANDLER);
    register(id++, SyncProspectedBlockMessage.HANDLER);
  }

  public static void sendToClient(ServerPlayer player, Object msg) {
    CHANNEL.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
  }

  public static void sendToServer(LocalPlayer player, Object msg) {
    CHANNEL.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_SERVER);
  }

  public static void broadcast(Object msg) {
    CHANNEL.sendToServer(msg);
  }

}
