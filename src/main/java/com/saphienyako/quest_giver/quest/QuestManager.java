package com.saphienyako.quest_giver.quest;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.data.QuestGiverDataLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nonnull;
import java.io.Reader;
import java.util.*;

public class QuestManager {

    private static Map<String, QuestLine> questLines = ImmutableMap.of();

    public static QuestLine getQuests(String questLineId) {
        return questLines.getOrDefault(questLineId, QuestLine.EMPTY);
    }

    public static PreparableReloadListener createReloadListener() {
        return new SimplePreparableReloadListener<Void>() {
            @Nonnull
            @Override
            protected Void prepare(@Nonnull ResourceManager rm, @Nonnull ProfilerFiller profiler) {
                return null;
            }

            @Override
            protected void apply(Void value, ResourceManager rm, ProfilerFiller profiler) {

                QuestGiver.LOGGER.info("Loading quest lines...");

                Map<String, QuestLine> lines = new HashMap<>();

                try {

                    Map<Identifier, Resource> resources = rm.listResources("quest_lines", rl -> rl.getPath().endsWith(".json"));

                    Set<String> discoveredLines = new HashSet<>();

                    for (Identifier rl : resources.keySet()) {

                        String path = rl.getPath();
                        String[] split = path.split("/");

                        if (split.length >= 2) {
                            discoveredLines.add(split[1]);
                        }
                    }

                    for (String lineId : discoveredLines) {

                        Map<Identifier, Quest> loaded = new HashMap<>();
                        QuestEnd end = null;
                        String folder = "quest_lines/" + lineId + "/";

                        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {

                            Identifier fileId = entry.getKey();

                            if (!fileId.getPath().startsWith(folder)) {
                                continue;
                            }

                            String fileName = fileId.getPath().substring(folder.length());

                            // Ignore nested folders if you don't support those
                            if (fileName.contains("/")) {
                                continue;
                            }

                            try (Reader reader = entry.getValue().openAsReader()) {

                                JsonElement json = JsonParser.parseReader(reader);

                                // SPECIAL END FILE
                                if (fileName.equals("end.json")) {
                                    end = QuestEnd.fromJson(json);
                                    continue;
                                }

                                String questName = fileName.substring(0, fileName.length() - ".json".length());
                                Identifier questId = Identifier.fromNamespaceAndPath(QuestGiver.MOD_ID, questName);
                                Quest quest = Quest.fromJson(questId, json);
                                loaded.put(questId, quest);
                            }
                        }

                        QuestGiver.LOGGER.info("Loaded {} quests for {}{}", loaded.size(), lineId, end != null ? " with end.json" : "");

                        lines.put(lineId, new QuestLine(loaded, end));
                    }

                } catch (Exception e) {
                    QuestGiver.LOGGER.error("Failed loading quest lines", e);
                }

                questLines = Collections.unmodifiableMap(lines);
            }
        };
    }
}
