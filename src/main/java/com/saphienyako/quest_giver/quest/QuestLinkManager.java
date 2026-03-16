package com.saphienyako.quest_giver.quest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.data.QuestLinkDataLoader;
import com.saphienyako.quest_giver.quest.data.QuestLinkData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestLinkManager {

    private static List<QuestLinkData> LINKS = new ArrayList<>();

    /** Called by QuestLinkLoader to set the loaded data */
    public static void setAll(List<QuestLinkData> links) {
        LINKS = links;
    }

    /** Returns the first matching link for an entity (type + optional name) */
    @Nullable
    public static QuestLinkData getMatchingLink(Entity entity) {
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (entityId == null) return null;

        for (QuestLinkData link : LINKS) {
            // Match entity type
            if (!link.entityId.equals(entityId)) continue;

            // Match name if specified
            if (link.name != null) {
                if (entity.getCustomName() == null) continue;
                if (!entity.getCustomName().getString().equalsIgnoreCase(link.name)) continue;
            }

            return link;
        }
        return null;
    }

    public static int size() {
        return LINKS.size();
    }
}
