package me.whizvox.worldleveling.common.network.message;

import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.lib.WLRegistries;
import me.whizvox.worldleveling.common.lib.internal.WLLog;
import me.whizvox.worldleveling.common.network.MessageHandler;
import me.whizvox.worldleveling.common.util.SkillsHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public record IncreasePlayerSkillExperienceMessage(ResourceLocation skillId, int amount) {

  public static final MessageHandler<IncreasePlayerSkillExperienceMessage> HANDLER = new MessageHandler<>() {

    @Override
    public Class<IncreasePlayerSkillExperienceMessage> getType() {
      return IncreasePlayerSkillExperienceMessage.class;
    }

    @Override
    public void encode(IncreasePlayerSkillExperienceMessage msg, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(msg.skillId);
      buffer.writeInt(msg.amount);
    }

    @Override
    public IncreasePlayerSkillExperienceMessage decode(FriendlyByteBuf buffer) {
      return new IncreasePlayerSkillExperienceMessage(buffer.readResourceLocation(), buffer.readInt());
    }

    @Override
    public void handle(IncreasePlayerSkillExperienceMessage msg, @Nullable ServerPlayer sender) {
      SkillType skill = WLRegistries.SKILLS.get().getValue(msg.skillId);
      if (skill != null) {
        SkillsHelper.handle(Minecraft.getInstance().player, skills -> {
          skills.increaseExperience(skill, msg.amount);
        });
      } else {
        WLLog.LOGGER.warn(WLLog.CLIENT, "Attempt to increase player skill XP with unknown skill ID: {}", msg.skillId);
      }
    }

  };

}
