package com.saphienyako.quest_giver.network;


import com.saphienyako.quest_giver.quest.player.QuestData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;


import java.util.function.Supplier;

public record ConfirmQuestMessage(boolean accept) {


    public static void encode(ConfirmQuestMessage msg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(msg.accept());
    }

    public static ConfirmQuestMessage decode(FriendlyByteBuf buffer) {
        return new ConfirmQuestMessage(buffer.readBoolean());
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        ServerPlayer player = supplier.get().getSender();
        if (player != null) {
            if (this.accept) {
                QuestData.get(player).acceptQuestLine();
            } else {
                QuestData.get(player).denyQuestLine();
            }
        }
    }
}
