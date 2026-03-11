package com.saphienyako.quest_giver.quest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class QuestLineRegistry {

    private static final Set<String> QUEST_LINES = new HashSet<>();

    public static Set<String> getAll() {
        return Collections.unmodifiableSet(QUEST_LINES);
    }

    public static boolean exists(String id) {
        return QUEST_LINES.contains(id);
    }

    public static void setAll(Collection<String> ids) {
        QUEST_LINES.clear();
        QUEST_LINES.addAll(ids);
    }

}
