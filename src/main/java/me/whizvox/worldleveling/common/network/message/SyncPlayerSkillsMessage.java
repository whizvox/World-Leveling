package me.whizvox.worldleveling.common.network.message;

import me.whizvox.worldleveling.common.api.Skill;
import me.whizvox.worldleveling.common.api.SkillType;
import me.whizvox.worldleveling.common.api.ability.Ability;
import me.whizvox.worldleveling.common.capability.PlayerSkills;
import me.whizvox.worldleveling.common.lib.WLCapabilities;
import me.whizvox.worldleveling.common.lib.WLRegistries;
import me.whizvox.worldleveling.common.lib.internal.WLLog;
import me.whizvox.worldleveling.common.network.MessageHandler;
import me.whizvox.worldleveling.common.util.SkillsHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

public record SyncPlayerSkillsMessage(Collection<Skill> skills, Collection<Ability> abilities) {

  @Nullable
  public static SyncPlayerSkillsMessage create(Player player) {
    if (!player.level.isClientSide()) {
      LazyOptional<PlayerSkills> cap = player.getCapability(WLCapabilities.PLAYER_SKILLS);
      if (cap.isPresent()) {
        PlayerSkills skills = cap.resolve().get();
        return new SyncPlayerSkillsMessage(skills.allSkills().toList(), skills.allAbilities().toList());
      }
      WLLog.LOGGER.warn(WLLog.CLIENT, "Cannot create sync message as player has no {} capability: {} ({})", WLCapabilities.PLAYER_SKILLS.getName(), player.getUUID(), player.getName().getString());
    }
    return null;
  }

  public static final MessageHandler<SyncPlayerSkillsMessage> HANDLER = new MessageHandler<>() {

    @Override
    public Class<SyncPlayerSkillsMessage> getType() {
      return SyncPlayerSkillsMessage.class;
    }

    @Override
    public void encode(SyncPlayerSkillsMessage msg, FriendlyByteBuf buffer) {
      buffer.writeShort(msg.skills.size());
      msg.skills.forEach(skill -> {
        buffer.writeResourceLocation(skill.getType().getRegistryName());
        buffer.writeInt(skill.getExperience());
        buffer.writeByte(skill.getAbilityPoints());
        Tag extraData = skill.getExtraData();
        buffer.writeBoolean(extraData != null);
        if (extraData != null) {
          // NbtIo.writeUnnamedTag is public, but NbtIo.readUnnamedTag is private, so use a wrapper compound tag
          CompoundTag extraDataTag = new CompoundTag();
          extraDataTag.put("a", extraData);
          buffer.writeNbt(extraDataTag);
        }
      });
      buffer.writeShort(msg.abilities.size());
      msg.abilities.forEach(ability -> buffer.writeResourceLocation(ability.getRegistryName()));
    }

    @Override
    public SyncPlayerSkillsMessage decode(FriendlyByteBuf buffer) {
      int skillsCount = buffer.readShort();
      ArrayList<Skill> skills = new ArrayList<>(skillsCount);
      for (int i = 0; i < skillsCount; i++) {
        ResourceLocation skillName = buffer.readResourceLocation();
        int xp = buffer.readInt();
        int points = buffer.readByte();
        Tag extraData;
        if (buffer.readBoolean()) {
          CompoundTag extraDataTag = buffer.readNbt();
          extraData = extraDataTag.get("a");
        } else {
          extraData = null;
        }
        SkillType skill = WLRegistries.SKILLS.get().getValue(skillName);
        if (skill != null) {
          Skill s = skill.createSkill(xp, extraData);
          s.setAbilityPoints(points);
          skills.add(s);
        } else {
          WLLog.LOGGER.warn(WLLog.CLIENT, "Unknown skill name: {}", skillName);
        }
      }
      int abilitiesCount = buffer.readShort();
      ArrayList<Ability> abilities = new ArrayList<>();
      for (int i = 0; i < abilitiesCount; i++) {
        ResourceLocation abilityName = buffer.readResourceLocation();
        Ability ability = WLRegistries.ABILITIES.get().getValue(abilityName);
        if (ability != null) {
          abilities.add(ability);
        } else {
          WLLog.LOGGER.warn(WLLog.CLIENT, "Unknown ability name: {}", abilityName);
        }
      }
      return new SyncPlayerSkillsMessage(skills, abilities);
    }

    @Override
    public void handle(SyncPlayerSkillsMessage msg, @Nullable ServerPlayer sender) {
      SkillsHelper.handle(Minecraft.getInstance().player, skills -> skills.sync(msg));
    }

  };

}
