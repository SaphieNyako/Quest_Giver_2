package com.saphienyako.quest_giver.quest.data;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.saphienyako.quest_giver.events.QuestCompletionEvent;
import com.saphienyako.quest_giver.quest.*;
import com.saphienyako.quest_giver.quest.task.TaskType;
import com.saphienyako.quest_giver.quest.util.SelectableQuest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.util.*;

public class QuestLineData {

    public final String questLineId;
    private final List<ResourceLocation> pendingCompletion = new ArrayList<>();
    private final Set<ResourceLocation> completedQuests = new HashSet<>();
    private final Map<ResourceLocation, QuestProgress> activeQuests = new HashMap<>();
    @Nullable
    private ServerPlayer player;
    private boolean accepted = false;

    public QuestLineData(String questLineId) {
        this.questLineId = questLineId;
    }

    // Called when the capability is attached to the player
    public void attach(ServerPlayer player) {

        this.player = player;

        QuestLine quests = getQuestLine();

        if (quests != null) {
            activeQuests.entrySet().removeIf(entry ->
                    quests.getQuest(entry.getKey()) == null
            );
        }

        startNextQuests();
    }

    public void accept() {

        if (!accepted && player != null) {

            accepted = true;

            pendingCompletion.clear();
            completedQuests.clear();
            activeQuests.clear();

            QuestLine quests = getQuestLine();
            Quest root = quests == null ? null : quests.getRootQuest();

            if (root != null && root.tasks.isEmpty()) {

                for (QuestReward reward : root.rewards) {
                    reward.grantReward(player);
                }

                completedQuests.add(root.id);
            }

            startNextQuests();
        }
    }

    @Nullable
    public QuestLine getQuestLine() {
        return QuestManager.getQuests(questLineId);
    }

    @Nullable
    public QuestDisplay getActiveQuestDisplay(ResourceLocation id) {
        QuestLine quests = this.getQuestLine();
        if (quests != null && this.player != null && this.activeQuests.containsKey(id)) {
            Quest quest = quests.getQuest(id);
            if (quest != null) {
                return quest.start;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public List<SelectableQuest> getActiveQuests() {
        QuestLine quests = this.getQuestLine();
        if (quests != null && this.player != null) {
            ImmutableList.Builder<SelectableQuest> list = ImmutableList.builder();
            for (QuestProgress progress : this.activeQuests.values().stream().sorted(Comparator.comparing(q -> q.quest)).toList()) {
                Quest quest = quests.getQuest(progress.quest);
                if (quest != null) {
                    list.add(new SelectableQuest(quest.id, quest.icon, quest.start));
                }
            }
            return list.build();
        } else {
            return ImmutableList.of();
        }
    }

    // if there are quests pending for completion, picks the first one, grants
    // rewards and returns a quest display for the user
    // If there's non, returns null.
    @Nullable
    public QuestDisplay completePendingQuest() {
        QuestLine quests = this.getQuestLine();
        if (quests != null && this.player != null && !this.pendingCompletion.isEmpty()) {
            while (!this.pendingCompletion.isEmpty()) {
                ResourceLocation id = this.pendingCompletion.remove(0);
                QuestDisplay display = this.tryComplete(this.player, quests, id);
                if (display != null) {
                    return display;
                }
            }
        }
        return null;
    }

    @Nullable
    private QuestDisplay tryComplete(ServerPlayer player, QuestLine quests, ResourceLocation id) {
        Quest quest = quests.getQuest(id);
        if (quest != null) {
            QuestDisplay display = quest.tasks.isEmpty() ? quest.start : quest.complete;
            if (display != null) {
                for (QuestReward reward : quest.rewards) {
                    reward.grantReward(player);
                }
                NeoForge.EVENT_BUS.post(new QuestCompletionEvent(this.player, quest));
                return display;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean hasCompleted(Quest quest) {
        return this.completedQuests.contains(quest.id);
    }
    // True if the task was completed
    // Can be used so fey entities only accept gifts for quests
    public <T> boolean checkComplete(TaskType<?, T> type, T element) {
        boolean success = false;
        QuestLine quests = this.getQuestLine();
        if (quests != null && this.player != null) {
            String msgToDisplay = null;
            // Check each active quest if the task can be completed somewhere
            for (QuestProgress progress : this.activeQuests.values()) {
                String progressMsg = progress.checkComplete(this.player, quests, type, element);
                if (progressMsg != null) {
                    // Something was completed. Set the success flag
                    success = true;
                    if (msgToDisplay == null) msgToDisplay = progressMsg;
                }
            }
            if (success) {
                triggerAfterComplete(quests, msgToDisplay);
            }
        }
        return success;
    }

    public <T, X> List<CompletableTaskInfo<T, X>> getAllCurrentTasks(TaskType<T, X> type) {
        ImmutableList.Builder<CompletableTaskInfo<T, X>> list = ImmutableList.builder();
        QuestLine quests = this.getQuestLine();
        if (quests != null && this.player != null) {
            for (QuestProgress progress : this.activeQuests.values()) {
                progress.getQuestElements(player, quests, type)
                        .forEach(elem ->
                                list.add(new CompletableTaskInfo<>(type, elem, element -> {
                                    String progressMsg = progress.checkComplete(this.player, quests, type, element);
                                    if (progressMsg != null) {
                                        triggerAfterComplete(quests, progressMsg);
                                    }
                                }))
                        );
            }
        }
        return list.build();
    }

    private void triggerAfterComplete(QuestLine quests, String msgToDisplay) {
        // Something was completed. Move all completed quests into the
        // completed quests set and unlock new quests
        boolean shouldNotify = false;
        Iterator<QuestProgress> itr = this.activeQuests.values().iterator();
        while (itr.hasNext()) {
            QuestProgress progress = itr.next();
            if (progress.shouldBeComplete(quests)) {
                // grant rewards and remove quest from active quests
                this.pendingCompletion.add(progress.quest);
                this.completedQuests.add(progress.quest);
                shouldNotify = true;
                itr.remove();
            }
        }
        if (this.player != null) {
            if (shouldNotify) {
                this.player.displayClientMessage(Component.translatable("message.quest_giver.quest_completion"), true);
            } else {
                this.player.displayClientMessage(Component.literal(msgToDisplay), true);
            }
        }
        this.startNextQuests();
    }

    public void startNextQuests() {

        QuestLine quests = getQuestLine();

        boolean hasEmptyQuests = false;

        if (quests != null) {

            for (Quest newQuest : quests.getNextQuests(activeQuests.keySet(), completedQuests)) {

                if (newQuest.tasks.isEmpty()) {

                    if (!pendingCompletion.contains(newQuest.id)) {

                        pendingCompletion.add(newQuest.id);
                        completedQuests.add(newQuest.id);

                        hasEmptyQuests = true;
                    }

                } else {

                    if (!activeQuests.containsKey(newQuest.id)) {

                        QuestProgress progress = new QuestProgress(newQuest.id);
                        activeQuests.put(newQuest.id, progress);
                    }
                }
            }
        }

        if (hasEmptyQuests) {
            startNextQuests();
        }
    }

    public static final Codec<QuestLineData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("questLineId").forGetter(d -> d.questLineId),
                    ResourceLocation.CODEC.listOf().fieldOf("pending").forGetter(d -> d.pendingCompletion),
                    ResourceLocation.CODEC.listOf().fieldOf("completed").forGetter(d -> List.copyOf(d.completedQuests)),
                    Codec.unboundedMap(ResourceLocation.CODEC, QuestProgress.CODEC)
                            .fieldOf("active")
                            .forGetter(d -> d.activeQuests)
            ).apply(instance, (id, pending, completed, active) -> {

                QuestLineData data = new QuestLineData(id);

                data.pendingCompletion.addAll(pending);
                data.completedQuests.addAll(completed);
                data.activeQuests.putAll(active);

                return data;
            }));

}
