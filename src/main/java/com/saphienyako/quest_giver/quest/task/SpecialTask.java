package com.saphienyako.quest_giver.quest.task;


import com.google.gson.JsonObject;
import com.saphienyako.quest_giver.quest.util.SpecialTaskAction;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;

public class SpecialTask implements TaskType<SpecialTaskAction, SpecialTaskAction> {

    public static final SpecialTask INSTANCE = new SpecialTask();

    private SpecialTask() {

    }

    @Override
    public Class<SpecialTaskAction> element() {
        return SpecialTaskAction.class;
    }

    @Override
    public Class<SpecialTaskAction> testType() {
        return SpecialTaskAction.class;
    }

    @Override
    public boolean checkCompleted(ServerPlayer player, SpecialTaskAction element, SpecialTaskAction match) {
        return element == match;
    }

    @Override
    public SpecialTaskAction fromJson(JsonObject json) {
        return SpecialTaskAction.valueOf(json.get("action").getAsString().toUpperCase(Locale.ROOT));
    }

    @Override
    public JsonObject toJson(SpecialTaskAction element) {
        JsonObject json = new JsonObject();
        json.addProperty("action", element.name().toLowerCase(Locale.ROOT));
        return json;
    }
}
