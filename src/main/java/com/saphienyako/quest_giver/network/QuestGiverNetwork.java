package com.saphienyako.quest_giver.network;

import com.saphienyako.quest_giver.QuestGiver;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class QuestGiverNetwork {

    public static SimpleChannel INSTANCE;
    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(QuestGiver.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        //CLIENT
        net.messageBuilder(OpenQuestDisplayMessage.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenQuestDisplayMessage::decode)
                .encoder(OpenQuestDisplayMessage::encode)
                .consumerMainThread(OpenQuestDisplayMessage::handle)
                .add();

        net.messageBuilder(OpenQuestSelectionMessage.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenQuestSelectionMessage::decode)
                .encoder(OpenQuestSelectionMessage::encode)
                .consumerMainThread(OpenQuestSelectionMessage::handle)
                .add();


        //SERVER
        net.messageBuilder(ConfirmQuestMessage.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ConfirmQuestMessage::decode)
                .encoder(ConfirmQuestMessage::encode)
                .consumerMainThread(ConfirmQuestMessage::handle)
                .add();

        net.messageBuilder(SelectQuestMessage.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SelectQuestMessage::decode)
                .encoder(SelectQuestMessage::encode)
                .consumerMainThread(SelectQuestMessage::handle)
                .add();

        net.messageBuilder(DismissEntityMessage.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(DismissEntityMessage::decode)
                .encoder(DismissEntityMessage::encode)
                .consumerMainThread(DismissEntityMessage::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
