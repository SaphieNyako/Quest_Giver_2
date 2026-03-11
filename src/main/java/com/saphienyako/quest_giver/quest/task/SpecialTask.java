package com.saphienyako.quest_giver.quest.task;


import com.google.gson.JsonObject;
import com.saphienyako.quest_giver.quest.util.SpecialTaskAction;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;

public class SpecialTask implements TaskType<String, String> {

    public static final SpecialTask INSTANCE = new SpecialTask();

    private SpecialTask() {}

    @Override
    public Class<String> element() {
        return String.class;
    }

    @Override
    public Class<String> testType() {
        return String.class;
    }

    @Override
    public boolean checkCompleted(ServerPlayer player, String element, String match) {
        return element.equals(match);
    }

    @Override
    public String fromJson(JsonObject json) {

        String action = json.get("action").getAsString().toLowerCase(Locale.ROOT);

        if (!SpecialTaskAction.isValid(action)) {
            throw new IllegalStateException("Unknown special task action: " + action);
        }

        return action;
    }

    @Override
    public JsonObject toJson(String element) {
        JsonObject json = new JsonObject();
        json.addProperty("action", element);
        return json;
    }
}