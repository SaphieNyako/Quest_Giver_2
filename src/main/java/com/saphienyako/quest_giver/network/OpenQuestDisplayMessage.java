package com.saphienyako.quest_giver.network;


import com.saphienyako.quest_giver.network.handler.OpenQuestDisplayHandler;
import com.saphienyako.quest_giver.quest.QuestDisplay;
import com.saphienyako.quest_giver.quest.data.ClientQuests;
import com.saphienyako.quest_giver.screen.DisplayQuestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;


import java.util.function.Supplier;

public record OpenQuestDisplayMessage(QuestDisplay display, boolean confirmationButtons, int entityId, String questLineId, String backgroundName, boolean dismiss, double scale) {

    public static void encode(OpenQuestDisplayMessage msg, FriendlyByteBuf buffer) {
        msg.display().toNetwork(buffer);
        buffer.writeBoolean(msg.confirmationButtons());
        buffer.writeInt(msg.entityId());
        buffer.writeUtf(msg.questLineId());
        buffer.writeUtf(msg.backgroundName());
        buffer.writeBoolean(msg.dismiss());
        buffer.writeDouble(msg.scale());
    }

    public static OpenQuestDisplayMessage decode(FriendlyByteBuf buffer) {
        QuestDisplay display = QuestDisplay.fromNetwork(buffer);
        boolean confirmationButtons = buffer.readBoolean();
        int id = buffer.readInt();
        return new OpenQuestDisplayMessage(display, confirmationButtons, id, buffer.readUtf(), buffer.readUtf(), buffer.readBoolean(), buffer.readDouble());
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                OpenQuestDisplayHandler.openMenu(display, confirmationButtons, entityId, questLineId, backgroundName, dismiss, scale);
            }
        });
    }
}
