package com.saphienyako.quest_giver.network;

import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.network.handler.OpenQuestDisplayHandler;
import com.saphienyako.quest_giver.network.handler.OpenQuestSelectionHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

@EventBusSubscriber(modid = QuestGiver.MOD_ID, value = Dist.CLIENT)
public final class QuestGiverClientNetwork {

    @SubscribeEvent
    public static void registerClientPayloads(RegisterClientPayloadHandlersEvent event) {

        event.register(
                OpenQuestDisplayMessage.TYPE,
                OpenQuestDisplayHandler::handle
        );

        event.register(
                OpenQuestSelectionMessage.TYPE,
                OpenQuestSelectionHandler::handle
        );
    }
}