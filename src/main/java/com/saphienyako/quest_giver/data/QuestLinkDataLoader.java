package com.saphienyako.quest_giver.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.quest.QuestLinkManager;
import com.saphienyako.quest_giver.quest.data.QuestLinkData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nonnull;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QuestLinkDataLoader extends SimplePreparableReloadListener<List<QuestLinkData>> {

    private static final Identifier FILE =  Identifier.fromNamespaceAndPath(QuestGiver.MOD_ID, "quest_line_links.json");

    @Nonnull
    @Override
    protected List<QuestLinkData> prepare(@Nonnull ResourceManager manager, @Nonnull ProfilerFiller profiler) {
        List<QuestLinkData> links = new ArrayList<>();

        try {
            List<Resource> stack = manager.getResourceStack(FILE);
            for (Resource resource : stack) {
                try (Reader reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
                    JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
                    for (JsonElement elem : array) {
                        links.add(QuestLinkData.fromJson(elem));
                    }
                }
            }
        } catch (Exception e) {
            QuestGiver.LOGGER.error("Failed loading quest links", e);
        }

        return links;
    }

    @Override
    protected void apply(@Nonnull List<QuestLinkData> links, @Nonnull ResourceManager manager, @Nonnull ProfilerFiller profiler) {
        QuestLinkManager.setAll(links);
        QuestGiver.LOGGER.info("Loaded {} quest links", links.size());
    }
}