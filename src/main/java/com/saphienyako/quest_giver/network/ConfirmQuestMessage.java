package com.saphienyako.quest_giver.network;


import com.saphienyako.quest_giver.quest.data.QuestData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;


import java.util.function.Supplier;

public record ConfirmQuestMessage(String questLineId, boolean accept) {

    public static void encode(ConfirmQuestMessage msg, FriendlyByteBuf buffer) {
        buffer.writeUtf(msg.questLineId());
        buffer.writeBoolean(msg.accept());
    }

    public static ConfirmQuestMessage decode(FriendlyByteBuf buffer) {
        return new ConfirmQuestMessage(buffer.readUtf(), buffer.readBoolean());
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        ServerPlayer player = supplier.get().getSender();
        if (player != null) {
            QuestData data = QuestData.get(player);
            if (accept()) {
                data.acceptQuestLine(questLineId());
            } else {
                data.denyQuestLine(questLineId());
            }
        }
    }
}
