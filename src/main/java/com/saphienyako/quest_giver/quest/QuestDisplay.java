package com.saphienyako.quest_giver.quest;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.sounds.SoundEvent;


import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class QuestDisplay {

    public static final Codec<QuestDisplay> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("title").forGetter(d -> d.title),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(d -> d.description),
            BuiltInRegistries.SOUND_EVENT.byNameCodec()
                    .optionalFieldOf("sound")
                    .forGetter(d -> Optional.ofNullable(d.sound))
    ).apply(instance, (title, description, sound) ->
            new QuestDisplay(title, description, sound.orElse(null))
    ));

    public final Component title;
    public final Component description;
    @Nullable
    public final SoundEvent sound;

    public QuestDisplay(Component title, Component description, @Nullable SoundEvent sound) {
        this.title = title;
        this.description = description;
        this.sound = sound;
    }

    public static QuestDisplay fromJson(JsonObject json) {
        return CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
    }

    public JsonObject toJson() {
        return CODEC.encodeStart(JsonOps.INSTANCE, this)
                .getOrThrow()
                .getAsJsonObject();
    }

    public static QuestDisplay fromNetwork(FriendlyByteBuf buffer) {
        return buffer.readLenientJsonWithCodec(CODEC);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeJsonWithCodec(CODEC, this);
    }
}
