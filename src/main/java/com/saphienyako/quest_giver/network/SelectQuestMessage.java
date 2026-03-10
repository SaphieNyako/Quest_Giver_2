package com.saphienyako.quest_giver.network;


import com.saphienyako.quest_giver.quest.QuestDisplay;
import com.saphienyako.quest_giver.quest.player.QuestData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;


import java.util.function.Supplier;

public record SelectQuestMessage(ResourceLocation quest) {

    public static void encode(SelectQuestMessage msg, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(msg.quest());
    }

    public static SelectQuestMessage decode(FriendlyByteBuf buffer) {
        ResourceLocation quest = buffer.readResourceLocation();
        return new SelectQuestMessage(quest);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        ServerPlayer player =supplier.get().getSender();
        if (player != null) {
            QuestData quests = QuestData.get(player);
            QuestDisplay display = quests.getActiveQuestDisplay(this.quest);
            if (quests.getQuestName() != null && display != null) {
                QuestGiverNetwork.INSTANCE.reply(new OpenQuestDisplayMessage(display, false, -1), supplier.get());
            }
        }
    }
}
