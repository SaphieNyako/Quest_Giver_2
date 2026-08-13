package com.saphienyako.quest_giver.network.handler;

import com.saphienyako.quest_giver.network.OpenQuestSelectionMessage;
import com.saphienyako.quest_giver.quest.data.ClientQuests;
import com.saphienyako.quest_giver.screen.SelectQuestScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OpenQuestSelectionHandler {

    public static void handle(OpenQuestSelectionMessage msg, IPayloadContext context) {
        if (msg.entityId() != -1) {
            ClientQuests.lastTalkedEntityId = msg.entityId();
        }

        Minecraft.getInstance().setScreen(new SelectQuestScreen(msg.quests(), ClientQuests.lastTalkedEntityId, msg.questLineId(), msg.backgroundName(), msg.dismiss(), msg.scale()));
    }

}
