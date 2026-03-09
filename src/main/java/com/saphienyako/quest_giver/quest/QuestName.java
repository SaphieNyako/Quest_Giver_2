package com.saphienyako.quest_giver.quest;

import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.Locale;

public enum QuestName {

    //INSTEAD OF ALIGNMENT
    //TODO make this datapacked


    SPRING("spring"),
    SUMMER("summer"),
    AUTUMN("autumn"),
    WINTER("winter");

    public final String id;


    QuestName(String id) {
        this.id = id;
    }

    public static QuestName byId(String id) {
        return switch (id.toLowerCase(Locale.ROOT).trim()) {
            case "spring" -> SPRING;
            case "summer" -> SUMMER;
            case "autumn" -> AUTUMN;
            case "winter" -> WINTER;
            default -> throw new IllegalArgumentException("Invalid name: " + id);
        };
    }

    public static String optionId(@Nullable QuestName name) {
        return name == null ? "name missing" : name.id;
    }

    @Nullable
    public static QuestName byOptionId(String id) {
        try {
            return byId(id);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
