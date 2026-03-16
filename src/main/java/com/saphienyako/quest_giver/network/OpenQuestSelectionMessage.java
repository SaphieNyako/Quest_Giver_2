package com.saphienyako.quest_giver.network;


import com.saphienyako.quest_giver.quest.data.ClientQuests;
import com.saphienyako.quest_giver.quest.util.PacketUtil;
import com.saphienyako.quest_giver.quest.util.SelectableQuest;
import com.saphienyako.quest_giver.screen.SelectQuestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;


import java.util.List;
import java.util.function.Supplier;

public record OpenQuestSelectionMessage(Component title, List<SelectableQuest> quests, int entityId, String questLineId, String backgroundName) {

    public static void encode(OpenQuestSelectionMessage msg, FriendlyByteBuf buffer) {
        buffer.writeComponent(msg.title());
        PacketUtil.writeList(msg.quests(), buffer, (b, q) -> q.toNetwork(b));
        buffer.writeInt(msg.entityId());
        buffer.writeUtf(msg.questLineId());
        buffer.writeUtf(msg.backgroundName());
    }

    public static OpenQuestSelectionMessage decode(FriendlyByteBuf buffer) {
        Component title = buffer.readComponent();
        List<SelectableQuest> quests = PacketUtil.readList(buffer, SelectableQuest::fromNetwork);
        int id = buffer.readInt();
        return new OpenQuestSelectionMessage(title, quests, id, buffer.readUtf(), buffer.readUtf());
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        if (this.entityId() != -1) ClientQuests.lastTalkedEntityId = this.entityId();
        Minecraft.getInstance().setScreen(new SelectQuestScreen(this.title(), this.quests(), ClientQuests.lastTalkedEntityId, this.questLineId, this.backgroundName));
    }
}
