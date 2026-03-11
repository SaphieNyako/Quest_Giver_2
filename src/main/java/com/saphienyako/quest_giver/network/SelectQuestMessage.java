package com.saphienyako.quest_giver.network;


import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.quest.QuestDisplay;
import com.saphienyako.quest_giver.quest.player.QuestData;
import com.saphienyako.quest_giver.quest.player.QuestLineData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;


import java.util.function.Supplier;

public record SelectQuestMessage(String questLineId, ResourceLocation quest) {

    public static void encode(SelectQuestMessage msg, FriendlyByteBuf buffer) {
        buffer.writeUtf(msg.questLineId());
        buffer.writeResourceLocation(msg.quest());
    }

    public static SelectQuestMessage decode(FriendlyByteBuf buffer) {
        String questLineId = buffer.readUtf();
        ResourceLocation quest = buffer.readResourceLocation();
        return new SelectQuestMessage(questLineId, quest);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                QuestData quests = QuestData.get(player);
                QuestLineData line = quests.getQuestLine(this.questLineId());
                if (line != null) {
                    // Convert String questId → ResourceLocation internally
                    QuestDisplay display = line.getActiveQuestDisplay(this.quest());

                    if (display != null) {
                        QuestGiverNetwork.INSTANCE.reply(
                                new OpenQuestDisplayMessage(display, false, -1, this.questLineId), context
                        );
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
