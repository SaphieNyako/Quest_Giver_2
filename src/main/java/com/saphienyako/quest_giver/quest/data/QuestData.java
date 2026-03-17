package com.saphienyako.quest_giver.quest.data;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.saphienyako.quest_giver.ModAttachments;
import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.quest.Quest;
import com.saphienyako.quest_giver.quest.QuestDisplay;
import com.saphienyako.quest_giver.quest.QuestLine;
import com.saphienyako.quest_giver.quest.QuestManager;
import com.saphienyako.quest_giver.quest.task.TaskType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestData {

    private final Map<String, QuestLineData> questLines = new HashMap<>();

    @Nullable
    private ServerPlayer player;

    public static QuestData get(ServerPlayer player) {
        return player.getData(ModAttachments.QUESTS);
    }

    public void attach(ServerPlayer player) {
        this.player = player;

        for (QuestLineData line : questLines.values()) {
            line.attach(player);
        }
    }

    public boolean hasQuestLine(String id) {
        return questLines.containsKey(id);
    }

    @Nullable
    public QuestLineData getQuestLine(String id) {
        return questLines.get(id);
    }

    @Nullable
    public QuestDisplay initialize(String questLineId) {

        if (!questLines.containsKey(questLineId)) {

            QuestLine quests = QuestManager.getQuests(questLineId);
            Quest root = quests.getRootQuest();

            if (root != null) {

                QuestLineData data = new QuestLineData(questLineId);

                if (player != null) {
                    data.attach(player);
                }

                questLines.put(questLineId, data);

                return root.start;
            }
        }

        return null;
    }

    public void acceptQuestLine(String questLineId) {

        QuestLineData data = questLines.get(questLineId);

        if (data != null) {
            data.accept();
        }
    }

    public void denyQuestLine(String questLineId) {
        questLines.remove(questLineId);
    }

    public void reset() {
        questLines.clear();
    }

    // GLOBAL TASK CHECKING
    public <T> boolean checkComplete(TaskType<?, T> type, T element) {

        boolean success = false;

        for (QuestLineData line : questLines.values()) {
            if (line.checkComplete(type, element)) {
                success = true;
            }
        }

        return success;
    }

    public <T, X> List<CompletableTaskInfo<T, X>> getAllCurrentTasks(TaskType<T, X> type) {

        ImmutableList.Builder<CompletableTaskInfo<T, X>> list = ImmutableList.builder();

        for (QuestLineData line : questLines.values()) {
            list.addAll(line.getAllCurrentTasks(type));
        }

        return list.build();
    }

    public static final Codec<QuestData> CODEC =
            Codec.unboundedMap(Codec.STRING, QuestLineData.CODEC)
                    .xmap(map -> {
                        QuestData data = new QuestData();
                        data.questLines.putAll(map);
                        return data;
                    }, data -> data.questLines);
}
