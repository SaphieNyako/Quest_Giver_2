package com.saphienyako.quest_giver.quest.task;

import com.google.gson.JsonObject;
import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.quest.data.QuestData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class CompleteQuestTask implements TaskType<CompleteQuestTask.Requirement, CompleteQuestTask.Context> {

    public static final CompleteQuestTask INSTANCE = new CompleteQuestTask();

    private CompleteQuestTask() {}

    public record Requirement(String questLine, ResourceLocation quest) {}

    public record Context(String questLine, ResourceLocation quest) {}

    @Override
    public Class<Requirement> element() {
        return Requirement.class;
    }

    @Override
    public Class<Context> testType() {
        return Context.class;
    }

    @Override
    public boolean checkCompleted(ServerPlayer player, Requirement element, Context match) {
        return element.questLine().equals(match.questLine()) && element.quest().equals(match.quest());
    }

    public boolean isAlreadyCompleted(ServerPlayer player, Requirement requirement) {
        return QuestData.get(player).hasCompletedQuest(requirement.questLine(), requirement.quest());
    }

    @Override
    public Requirement fromJson(JsonObject json) {

        String questLine = json.get("quest_line").getAsString();
        String questName = json.get("quest").getAsString();

        ResourceLocation quest = questName.contains(":") ? new ResourceLocation(questName) : new ResourceLocation(QuestGiver.MOD_ID, questName);
        return new Requirement(questLine, quest);
    }

    @Override
    public JsonObject toJson(Requirement element) {
        JsonObject json = new JsonObject();
        json.addProperty("quest_line", element.questLine());
        json.addProperty("quest", element.quest().getPath());
        return json;
    }

    @Override
    public boolean repeatable() {
        return false;
    }
}
