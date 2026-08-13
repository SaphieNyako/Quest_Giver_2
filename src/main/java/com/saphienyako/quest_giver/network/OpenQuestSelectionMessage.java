package com.saphienyako.quest_giver.network;


import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.network.handler.OpenQuestSelectionHandler;
import com.saphienyako.quest_giver.quest.util.PacketUtil;
import com.saphienyako.quest_giver.quest.util.SelectableQuest;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record OpenQuestSelectionMessage(List<SelectableQuest> quests, int entityId, String questLineId, String backgroundName, boolean dismiss, double scale) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<OpenQuestSelectionMessage> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(QuestGiver.MOD_ID, "open_quest_selection"));

    public static final StreamCodec<FriendlyByteBuf, OpenQuestSelectionMessage> STREAM_CODEC =
            StreamCodec.of(OpenQuestSelectionMessage::encode, OpenQuestSelectionMessage::decode);

    private static void encode(FriendlyByteBuf buf, OpenQuestSelectionMessage msg) {
        PacketUtil.writeList(msg.quests(), buf, (b, q) -> q.toNetwork(b));
        buf.writeInt(msg.entityId());
        buf.writeUtf(msg.questLineId());
        buf.writeUtf(msg.backgroundName());
        buf.writeBoolean(msg.dismiss());
        buf.writeDouble(msg.scale());
    }

    private static OpenQuestSelectionMessage decode(FriendlyByteBuf buf) {
        List<SelectableQuest> quests = PacketUtil.readList(buf, SelectableQuest::fromNetwork);
        int id = buf.readInt();
        String questLineId = buf.readUtf();
        String backgroundName = buf.readUtf();
        boolean dismiss = buf.readBoolean();
        double scale = buf.readDouble();
        return new OpenQuestSelectionMessage(quests, id, questLineId, backgroundName, dismiss, scale);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
