package me.whizvox.worldleveling.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

public interface MessageHandler<MSG> {

  /**
   * The class type of this message
   * @return Class type of this message
   */
  Class<MSG> getType();

  /**
   * Encodes a message into a buffer
   * @param msg The message to encode
   * @param buffer The buffer to encode into
   */
  void encode(MSG msg, FriendlyByteBuf buffer);

  /**
   * Decodes a message from a buffer
   * @param buffer The buffer to decode from
   * @return A new instance of this message
   */
  MSG decode(FriendlyByteBuf buffer);

  /**
   * Handle this message. Called within {@link NetworkEvent.Context#enqueueWork(Runnable)}.
   * @param msg The received message
   * @param sender If this message is sent from a client to a server, this will be the player that sent it. Otherwise,
   *               if this message is sent from a server to a client, this will be <code>null</code> (you can just use
   *               {@link net.minecraft.client.Minecraft#player} in that case).
   */
  void handle(MSG msg, @Nullable ServerPlayer sender);

}
