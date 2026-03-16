package com.saphienyako.quest_giver.network;

import com.saphienyako.quest_giver.QuestGiver;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class QuestGiverNetwork {

    public static final String PROTOCOL_VERSION = "1";

    public static PayloadRegistrar REGISTRAR;

    public static void register(RegisterPayloadHandlersEvent event) {
        REGISTRAR = event.registrar(QuestGiver.MOD_ID)
                .versioned(PROTOCOL_VERSION);


        // Client
        REGISTRAR.playToClient(
                OpenQuestDisplayMessage.TYPE,
                OpenQuestDisplayMessage.STREAM_CODEC,
                OpenQuestDisplayMessage::handle
        );

        REGISTRAR.playToClient(
                OpenQuestSelectionMessage.TYPE,
                OpenQuestSelectionMessage.STREAM_CODEC,
                OpenQuestSelectionMessage::handle
        );

        //SERVER
        // Server
        REGISTRAR.playToServer(
                ConfirmQuestMessage.TYPE,
                ConfirmQuestMessage.STREAM_CODEC,
                ConfirmQuestMessage::handle
        );

        REGISTRAR.playToServer(
                SelectQuestMessage.TYPE,
                SelectQuestMessage.STREAM_CODEC,
                SelectQuestMessage::handle
        );
    }

}
