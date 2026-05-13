package com.saphienyako.quest_giver.network.handler;

import com.saphienyako.quest_giver.network.OpenQuestSelectionMessage;
import com.saphienyako.quest_giver.quest.data.ClientQuests;
import com.saphienyako.quest_giver.quest.util.SelectableQuest;
import com.saphienyako.quest_giver.screen.SelectQuestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public class OpenQuestSelectionHandler {

    public static void handle(Component title, List<SelectableQuest> quests, int entityId, String questLineId, String backgroundName, boolean dismiss, double scale) {
        if (entityId != -1) {
            ClientQuests.lastTalkedEntityId = entityId;
        }

        Minecraft.getInstance().setScreen(
                new SelectQuestScreen(
                        title,
                        quests,
                        ClientQuests.lastTalkedEntityId,
                        questLineId,
                        backgroundName,
                        dismiss,
                        scale
                )
        );
    }

}
