package com.saphienyako.quest_giver.quest;

import com.google.common.collect.ImmutableMap;
import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.data.QuestGiverDataLoader;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class QuestManager {

    private static Map<QuestName, QuestLine> questLines = ImmutableMap.of();

    public static QuestLine getQuests(QuestName questName) {
        return questLines.getOrDefault(questName, QuestLine.EMPTY);
    }

    public static PreparableReloadListener createReloadListener() {
        return new SimplePreparableReloadListener<Void>() {
            @Nonnull
            @Override
            protected Void prepare(@Nonnull ResourceManager rm, @Nonnull ProfilerFiller profiler) {
                return null;
            }

            @Override
            protected void apply(@Nonnull Void value, @Nonnull ResourceManager rm, @Nonnull ProfilerFiller profiler) {
                EnumMap<QuestName, QuestLine> lines = new EnumMap<>(QuestName.class);
                for (QuestName questName : QuestName.values()) {
                    try {
                        lines.put(questName, new QuestLine(QuestGiverDataLoader.loadJson(rm, "feywild_quests/" + questName.id, Quest::fromJson)));
                    } catch (Exception e) {
                       QuestGiver.LOGGER.error("Failed to load quests for {}", questName, e);
                    }
                }
                questLines = Collections.unmodifiableMap(lines);
            }
        };
    }
}
