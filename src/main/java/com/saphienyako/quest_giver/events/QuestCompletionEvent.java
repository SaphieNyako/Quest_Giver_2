package com.saphienyako.quest_giver.events;

import com.saphienyako.quest_giver.quest.Quest;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;


public class QuestCompletionEvent extends Event {

    private final ServerPlayer entity;
    private final Quest quest;

    public QuestCompletionEvent(ServerPlayer entity, Quest quest) {
        this.entity = entity;
        this.quest = quest;
    }

    public ServerPlayer getEntity() {
        return this.entity;
    }

    public Quest getQuest() {
        return this.quest;
    }

}
