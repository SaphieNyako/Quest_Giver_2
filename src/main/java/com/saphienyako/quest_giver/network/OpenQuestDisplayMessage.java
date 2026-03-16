package com.saphienyako.quest_giver.network;


import com.saphienyako.quest_giver.quest.QuestDisplay;
import com.saphienyako.quest_giver.quest.data.ClientQuests;
import com.saphienyako.quest_giver.screen.DisplayQuestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;


import java.util.function.Supplier;

public record OpenQuestDisplayMessage(QuestDisplay display, boolean confirmationButtons, int entityId, String questLineId, String backgroundName) {

    public static void encode(OpenQuestDisplayMessage msg, FriendlyByteBuf buffer) {
        msg.display().toNetwork(buffer);
        buffer.writeBoolean(msg.confirmationButtons());
        buffer.writeInt(msg.entityId());
        buffer.writeUtf(msg.questLineId());
        buffer.writeUtf(msg.backgroundName());
    }

    public static OpenQuestDisplayMessage decode(FriendlyByteBuf buffer) {
        QuestDisplay display = QuestDisplay.fromNetwork(buffer);
        boolean confirmationButtons = buffer.readBoolean();
        int id = buffer.readInt();
        return new OpenQuestDisplayMessage(display, confirmationButtons, id, buffer.readUtf(), buffer.readUtf());
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        if (this.display.sound != null) {
            Player player = Minecraft.getInstance().player;
            if (player != null && this.display().sound != null) {
                Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(this.display().sound, SoundSource.MASTER, 1, 1, player.getRandom(), player.getX(), player.getY(), player.getZ()));
            }
        }
        if (this.entityId() != -1) ClientQuests.lastTalkedEntityId = this.entityId();
        Minecraft.getInstance().setScreen(new DisplayQuestScreen(this.display(), this.confirmationButtons(), ClientQuests.lastTalkedEntityId, this.questLineId, this.backgroundName));
    }
}
