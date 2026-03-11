package com.saphienyako.quest_giver.quest.util;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

// All enum names must be completely uppercase!
public class SpecialTaskAction {
    private static final Set<String> ACTIONS = new LinkedHashSet<>();

    public static void setAll(Set<String> actions) {
        ACTIONS.clear();
        ACTIONS.addAll(actions);
    }

    public static boolean isValid(String name) {
        return ACTIONS.contains(name.toLowerCase(Locale.ROOT));
    }

    public static Set<String> getAll() {
        return Collections.unmodifiableSet(ACTIONS);
    }
}
