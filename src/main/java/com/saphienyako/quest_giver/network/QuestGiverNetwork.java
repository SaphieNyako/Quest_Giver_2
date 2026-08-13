package com.saphienyako.quest_giver.network;

import com.saphienyako.quest_giver.QuestGiver;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class QuestGiverNetwork {

    public static final String PROTOCOL_VERSION = "1";

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(QuestGiver.MOD_ID)
                .versioned(PROTOCOL_VERSION);

        // Clientbound
        registrar.playToClient(
                OpenQuestDisplayMessage.TYPE,
                OpenQuestDisplayMessage.STREAM_CODEC
        );

        registrar.playToClient(
                OpenQuestSelectionMessage.TYPE,
                OpenQuestSelectionMessage.STREAM_CODEC
        );

        // Serverbound
        registrar.playToServer(
                ConfirmQuestMessage.TYPE,
                ConfirmQuestMessage.STREAM_CODEC,
                ConfirmQuestMessage::handle
        );

        registrar.playToServer(
                SelectQuestMessage.TYPE,
                SelectQuestMessage.STREAM_CODEC,
                SelectQuestMessage::handle
        );

        registrar.playToServer(
                DismissEntityMessage.TYPE,
                DismissEntityMessage.STREAM_CODEC,
                DismissEntityMessage::handle
        );
    }

}
