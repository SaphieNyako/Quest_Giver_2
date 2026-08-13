package com.saphienyako.quest_giver.quest.task;

import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

// Test type must be a resource locations as biomes are handled in a weird way
public class BiomeTask implements TaskType<Identifier, Identifier> {

    public static final BiomeTask INSTANCE = new BiomeTask();

    private BiomeTask() {

    }

    @Override
    public Class<Identifier> element() {
        return Identifier.class;
    }

    @Override
    public Class<Identifier> testType() {
        return Identifier.class;
    }

    @Override
    public boolean checkCompleted(ServerPlayer player, Identifier element, Identifier match) {
        return Objects.equals(element, match);
    }

    @Override
    public Identifier fromJson(JsonObject json) {
        Identifier rl = Identifier.tryParse(json.get("biome").getAsString());
        if (rl == null) {
            throw new IllegalStateException("Can't load feywild quest task: invalid resource: " + json.get("biome"));
        }
        return rl;
    }

    @Override
    public JsonObject toJson(Identifier element) {
        JsonObject json = new JsonObject();
        json.addProperty("biome", element.toString());
        return json;
    }

}
