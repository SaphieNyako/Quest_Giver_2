package com.saphienyako.quest_giver.quest;

import com.google.common.collect.ImmutableMap;
import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.data.QuestGiverDataLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class QuestManager {

    private static Map<QuestLineName, QuestLine> questLines = ImmutableMap.of();

    public static QuestLine getQuests(QuestLineName questLineName) {
        return questLines.getOrDefault(questLineName, QuestLine.EMPTY);
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

                EnumMap<QuestLineName, QuestLine> lines = new EnumMap<>(QuestLineName.class);

                for (QuestLineName questLineName : QuestLineName.values()) {
                    try {
                        Map<ResourceLocation, Quest> loaded =
                                QuestGiverDataLoader.loadJson(rm, "quest_lines/" + questLineName.id, Quest::fromJson);

                        QuestGiver.LOGGER.info("Loaded {} quests for {}", loaded.size(), questLineName);

                        lines.put(questLineName, new QuestLine(loaded));

                    } catch (Exception e) {
                        QuestGiver.LOGGER.error("Failed to load quests for {}", questLineName, e);
                    }
                }

                questLines = Collections.unmodifiableMap(lines);
            }
        };
    }
}
