package com.saphienyako.quest_giver.quest.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class QuestLinkData {

    public final String questLineId;
    public final ResourceLocation entityId;
    @Nullable
    public final String name;
    public final String backgroundName;
    public final ResourceLocation interactionItem;
    public final double scale;

    public QuestLinkData(String questLineId, ResourceLocation entityId, @Nullable String name, String backgroundName, ResourceLocation interactionItem, double scale) {
        this.questLineId = questLineId;
        this.entityId = entityId;
        this.name = name;
        this.backgroundName = backgroundName;
        this.interactionItem = interactionItem;
        this.scale = scale;
    }

    /** Convert a JSON element into this data object */
    public static QuestLinkData fromJson(JsonElement element) {
        JsonObject json = element.getAsJsonObject();
        String questLineId = json.get("quest_line_id").getAsString();
        ResourceLocation entityId = new ResourceLocation(json.get("entity_id").getAsString());
        String name = json.has("name") ? json.get("name").getAsString() : null;
        String backgroundName = json.has("background_name") ? json.get("background_name").getAsString() : null;
        ResourceLocation interactionItem = json.has("interaction_item") ? ResourceLocation.tryParse(json.get("interaction_item").getAsString()) : null;
        double scale = json.has("scale") ? json.get("scale").getAsDouble() : 1.5;
        return new QuestLinkData(questLineId, entityId, name, backgroundName, interactionItem, scale);
    }
}
