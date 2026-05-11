package com.saphienyako.quest_giver.network;


import com.saphienyako.quest_giver.quest.QuestDisplay;
import com.saphienyako.quest_giver.quest.data.QuestData;
import com.saphienyako.quest_giver.quest.data.QuestLineData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;


import java.util.function.Supplier;

public record SelectQuestMessage(String questLineId, ResourceLocation quest, String backgroundName, boolean dismiss, double scale) {

    public static void encode(SelectQuestMessage msg, FriendlyByteBuf buffer) {
        buffer.writeUtf(msg.questLineId());
        buffer.writeResourceLocation(msg.quest());
        buffer.writeUtf(msg.backgroundName());
        buffer.writeBoolean(msg.dismiss());
        buffer.writeDouble(msg.scale());
    }

    public static SelectQuestMessage decode(FriendlyByteBuf buffer) {
        String questLineId = buffer.readUtf();
        ResourceLocation quest = buffer.readResourceLocation();
        String backgroundName = buffer.readUtf();
        boolean dismiss = buffer.readBoolean();
        double scale = buffer.readDouble();
        return new SelectQuestMessage(questLineId, quest, backgroundName, dismiss, scale);
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
                                new OpenQuestDisplayMessage(display, false, -1, this.questLineId, this.backgroundName, this.dismiss, this.scale), context
                        );
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
