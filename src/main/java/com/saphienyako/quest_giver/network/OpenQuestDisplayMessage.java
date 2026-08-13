package com.saphienyako.quest_giver.network;


import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.network.handler.OpenQuestDisplayHandler;
import com.saphienyako.quest_giver.quest.QuestDisplay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenQuestDisplayMessage(QuestDisplay display, boolean confirmationButtons, int entityId, String questLineId, String backgroundName, boolean dismiss, double scale) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<OpenQuestDisplayMessage> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(QuestGiver.MOD_ID, "open_quest_display"));

    public static final StreamCodec<FriendlyByteBuf, OpenQuestDisplayMessage> STREAM_CODEC =
            StreamCodec.of(OpenQuestDisplayMessage::encode, OpenQuestDisplayMessage::decode);

    private static void encode(FriendlyByteBuf buffer, OpenQuestDisplayMessage msg) {
        msg.display().toNetwork(buffer);
        buffer.writeBoolean(msg.confirmationButtons());
        buffer.writeInt(msg.entityId());
        buffer.writeUtf(msg.questLineId());
        buffer.writeUtf(msg.backgroundName());
        buffer.writeBoolean(msg.dismiss());
        buffer.writeDouble(msg.scale());
    }

    private static OpenQuestDisplayMessage decode(FriendlyByteBuf buffer) {
        QuestDisplay display = QuestDisplay.fromNetwork(buffer);
        boolean confirmationButtons = buffer.readBoolean();
        int id = buffer.readInt();
        return new OpenQuestDisplayMessage(display, confirmationButtons, id, buffer.readUtf(), buffer.readUtf(), buffer.readBoolean(), buffer.readDouble());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
