package com.saphienyako.quest_giver.quest.task;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.Identifier;


public class TaskTypes {

    private static final BiMap<Identifier, TaskType<?, ?>> values = HashBiMap.create();

    public static TaskType<?, ?> getType(Identifier id) {
        if (values.containsKey(id)) {
            return values.get(id);
        } else {
            throw new IllegalStateException("Unknown quest task type: " + id);
        }
    }

    public static Identifier getId(TaskType<?, ?> type) {
        if (values.containsValue(type)) {
            return values.inverse().get(type);
        } else {
            throw new IllegalStateException("Quest task type not registered: " + type);
        }
    }

    public static void register(Identifier id, TaskType<?, ?> type) {
        if (values.containsKey(id)) {
            throw new IllegalStateException("Task type with the same entityId already registered: " + id);
        } else if (values.containsValue(type)) {
            throw new IllegalStateException("Task type registered twice: " + type);
        } else {
            values.put(id, type);
        }
    }
}
