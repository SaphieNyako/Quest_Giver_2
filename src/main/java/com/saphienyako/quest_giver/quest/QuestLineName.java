package com.saphienyako.quest_giver.quest;

import javax.annotation.Nullable;
import java.util.Locale;

public enum QuestLineName {

    //INSTEAD OF ALIGNMENT
    //TODO make this datapacked


    SPRING("spring"),
    SUMMER("summer"),
    AUTUMN("autumn"),
    WINTER("winter");

    public final String id;


    QuestLineName(String id) {
        this.id = id;
    }

    public static QuestLineName byId(String id) {
        return switch (id.toLowerCase(Locale.ROOT).trim()) {
            case "spring" -> SPRING;
            case "summer" -> SUMMER;
            case "autumn" -> AUTUMN;
            case "winter" -> WINTER;
            default -> throw new IllegalArgumentException("Invalid name: " + id);
        };
    }

    public static String optionId(@Nullable QuestLineName name) {
        return name == null ? "name missing" : name.id;
    }

    @Nullable
    public static QuestLineName byOptionId(String id) {
        try {
            return byId(id);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
