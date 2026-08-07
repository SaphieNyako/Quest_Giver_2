package com.saphienyako.quest_giver.quest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestLine {
    //A QuestLine is a collection of Quest(s)

    public static final QuestLine EMPTY = new QuestLine(ImmutableMap.of(), null);

    @Nullable
    private final QuestEnd end;

    private final Map<ResourceLocation, Quest> quests;
    private final Map<Quest, List<Quest>> questOrder;

    public QuestLine(Map<ResourceLocation, Quest> quests, @Nullable QuestEnd end) {
        this.quests = ImmutableMap.copyOf(quests);
        ImmutableMap.Builder<Quest, List<Quest>> questOrder = ImmutableMap.builder();
        this.end = end;
        // Iterate through every quest in the questline
        for (Quest quest : this.quests.values()) {
            questOrder.put(quest, quest.parents.stream().map(rl -> {
                if (this.quests.containsKey(rl)) {
                    return this.quests.get(rl);
                } else {
                    throw new IllegalStateException("Invalid reference in quest: " + rl);
                }
            }).collect(ImmutableList.toImmutableList()));
        }
        this.questOrder = questOrder.build();
    }

    @Nullable
    public QuestEnd getEnd() {
        return end;
    }

    @Nullable
    public Quest getQuest(ResourceLocation id) {
        return this.quests.getOrDefault(id, null);
    }

    @Nullable
    public Quest getRootQuest() {
        return quests.values().stream()
                .filter(q -> q.parents.isEmpty())
                .findFirst()
                .orElse(null);
    }

    public Set<Quest> getNextQuests(Set<ResourceLocation> active, Set<ResourceLocation> completed) {
        return this.questOrder.entrySet().stream()
                .filter(entry -> !active.contains(entry.getKey().id)) // Filter out quests that are currently active
                .filter(entry -> entry.getKey().repeatable || !completed.contains(entry.getKey().id)) // Only repeatable or not yet completed quests
                .filter(entry -> entry.getValue().stream().allMatch(quest -> completed.contains(quest.id))) // Check that all required quests are completed
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public int getQuestCount() {
        return quests.size();
    }

}
