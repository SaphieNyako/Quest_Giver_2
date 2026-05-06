package com.saphienyako.quest_giver.network;


import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.quest.QuestDisplay;
import com.saphienyako.quest_giver.quest.data.ClientQuests;
import com.saphienyako.quest_giver.quest.util.PacketUtil;
import com.saphienyako.quest_giver.quest.util.SelectableQuest;
import com.saphienyako.quest_giver.screen.SelectQuestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;


import java.util.List;
import java.util.function.Supplier;

public record OpenQuestSelectionMessage(List<SelectableQuest> quests, int entityId, String questLineId, String backgroundName, boolean dismiss) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<OpenQuestSelectionMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(QuestGiver.MOD_ID, "open_quest_selection"));

    public static final StreamCodec<FriendlyByteBuf, OpenQuestSelectionMessage> STREAM_CODEC =
            StreamCodec.of(OpenQuestSelectionMessage::encode, OpenQuestSelectionMessage::decode);

    private static void encode(FriendlyByteBuf buf, OpenQuestSelectionMessage msg) {
        PacketUtil.writeList(msg.quests(), buf, (b, q) -> q.toNetwork(b));
        buf.writeInt(msg.entityId());
        buf.writeUtf(msg.questLineId());
        buf.writeUtf(msg.backgroundName());
        buf.writeBoolean(msg.dismiss());
    }

    private static OpenQuestSelectionMessage decode(FriendlyByteBuf buf) {
        List<SelectableQuest> quests = PacketUtil.readList(buf, SelectableQuest::fromNetwork);
        int id = buf.readInt();
        String questLineId = buf.readUtf();
        String backgroundName = buf.readUtf();
        boolean dismiss = buf.readBoolean();
        return new OpenQuestSelectionMessage(quests, id, questLineId, backgroundName, dismiss);
    }

    public static void handle(OpenQuestSelectionMessage msg, IPayloadContext context) {
        if (msg.entityId() != -1) ClientQuests.lastTalkedEntityId = msg.entityId();
        Minecraft.getInstance().setScreen(new SelectQuestScreen(msg.quests(), ClientQuests.lastTalkedEntityId, msg.questLineId, msg.backgroundName, msg.dismiss));

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
