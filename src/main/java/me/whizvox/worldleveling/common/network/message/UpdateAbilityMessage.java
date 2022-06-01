package me.whizvox.worldleveling.common.network.message;

import me.whizvox.worldleveling.common.api.ability.Ability;
import me.whizvox.worldleveling.common.lib.WLRegistries;
import me.whizvox.worldleveling.common.lib.internal.WLLog;
import me.whizvox.worldleveling.common.network.MessageHandler;
import me.whizvox.worldleveling.common.util.SkillsHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public record UpdateAbilityMessage(Ability ability, boolean purchased) {

  public static final MessageHandler<UpdateAbilityMessage> HANDLER = new MessageHandler<>() {
    @Override
    public Class<UpdateAbilityMessage> getType() {
      return UpdateAbilityMessage.class;
    }
    @Override
    public void encode(UpdateAbilityMessage msg, FriendlyByteBuf buffer) {
      ResourceLocation abilityName = msg.ability.getRegistryName();
      if (abilityName != null) {
        buffer.writeResourceLocation(abilityName);
        buffer.writeBoolean(msg.purchased);
      } else {
        throw new IllegalArgumentException("Attempt to purchase unregistered ability");
      }
    }
    @Override
    public UpdateAbilityMessage decode(FriendlyByteBuf buffer) {
      ResourceLocation abilityName = buffer.readResourceLocation();
      Ability ability = WLRegistries.ABILITIES.get().getValue(abilityName);
      if (ability != null) {
        return new UpdateAbilityMessage(ability, buffer.readBoolean());
      }
      throw new IllegalArgumentException("Unregistered ability: " + abilityName);
    }
    @Override
    public void handle(UpdateAbilityMessage msg, @Nullable ServerPlayer sender) {
      SkillsHelper.handle(sender, skills -> {
        if (msg.purchased) {
          if (!skills.purchaseAbility(msg.ability)) {
            WLLog.LOGGER.warn(WLLog.SERVER, "Could not purchase ability {} for player {} ({})", msg.ability.getRegistryName(), sender.getName(), sender.getUUID());
          }
        } else {
          if (!skills.refundAbility(msg.ability)) {
            WLLog.LOGGER.warn(WLLog.SERVER, "Could not refund ability {} for player {} ({})", msg.ability.getRegistryName(), sender.getName(), sender.getUUID());
          }
        }
      });
    }
  };

}
