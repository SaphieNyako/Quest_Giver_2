package com.saphienyako.quest_giver.quest;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.data.QuestGiverDataLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
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

                    Map<ResourceLocation, Resource> resources = rm.listResources("quest_lines", rl -> rl.getPath().endsWith(".json"));

                    Set<String> discoveredLines = new HashSet<>();

                    for (ResourceLocation rl : resources.keySet()) {

                        String path = rl.getPath();
                        String[] split = path.split("/");

                        if (split.length >= 2) {
                            discoveredLines.add(split[1]);
                        }
                    }

                    for (String lineId : discoveredLines) {

                        Map<ResourceLocation, Quest> loaded = QuestGiverDataLoader.loadJson(rm, "quest_lines/" + lineId,
                                        (id, json) -> {

                                            if (id.getPath().equals("end")) {
                                                return null;
                                            }

                                            return Quest.fromJson(id, json);
                                        }
                                        );

                        QuestEnd end = loadEnd(rm, lineId);

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

    @Nullable
    private static QuestEnd loadEnd(ResourceManager rm, String lineId) {

        ResourceLocation location = new ResourceLocation(QuestGiver.MOD_ID, "quest_lines/" + lineId + "/end.json");

        Optional<Resource> resource =
                rm.getResource(location);

        if (resource.isEmpty()) {
            return null;
        }

        try (Reader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {

            JsonElement json = JsonParser.parseReader(reader);

            return QuestEnd.fromJson(json);

        } catch (Exception e) {

            QuestGiver.LOGGER.error("Failed loading end.json for quest line {}", lineId, e);
            return null;
        }
    }
}
