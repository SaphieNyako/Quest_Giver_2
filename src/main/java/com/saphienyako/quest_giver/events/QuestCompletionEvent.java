package com.saphienyako.quest_giver.events;

import com.saphienyako.quest_giver.quest.Quest;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public class QuestCompletionEvent extends Event {

    private final ServerPlayer entity;
    private final String questLineId;
    private final Quest quest;

    public QuestCompletionEvent(ServerPlayer entity,String questLineId, Quest quest) {
        this.entity = entity;
        this.questLineId = questLineId;
        this.quest = quest;
    }

    public ServerPlayer getEntity() {
        return this.entity;
    }

    public String getQuestLineId() {
        return questLineId;
    }

    public Quest getQuest() {
        return this.quest;
    }

}
