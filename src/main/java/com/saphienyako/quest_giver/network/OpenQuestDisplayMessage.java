package com.saphienyako.quest_giver.network;


import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.network.handler.OpenQuestDisplayHandler;
import com.saphienyako.quest_giver.quest.QuestDisplay;
import com.saphienyako.quest_giver.quest.data.ClientQuests;
import com.saphienyako.quest_giver.screen.DisplayQuestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;


import java.util.function.Supplier;

public record OpenQuestDisplayMessage(QuestDisplay display, boolean confirmationButtons, int entityId, String questLineId, String backgroundName) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<OpenQuestDisplayMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(QuestGiver.MOD_ID, "open_quest_display"));

    public static final StreamCodec<FriendlyByteBuf, OpenQuestDisplayMessage> STREAM_CODEC =
            StreamCodec.of(OpenQuestDisplayMessage::encode, OpenQuestDisplayMessage::decode);

    private static void encode(FriendlyByteBuf buffer, OpenQuestDisplayMessage msg) {
        msg.display().toNetwork(buffer);
        buffer.writeBoolean(msg.confirmationButtons());
        buffer.writeInt(msg.entityId());
        buffer.writeUtf(msg.questLineId());
        buffer.writeUtf(msg.backgroundName());
    }

    private static OpenQuestDisplayMessage decode(FriendlyByteBuf buffer) {
        QuestDisplay display = QuestDisplay.fromNetwork(buffer);
        boolean confirmationButtons = buffer.readBoolean();
        int id = buffer.readInt();
        return new OpenQuestDisplayMessage(display, confirmationButtons, id, buffer.readUtf(), buffer.readUtf());
    }
    @SuppressWarnings("resource")
    public static void handle(OpenQuestDisplayMessage msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                OpenQuestDisplayHandler.openMenu(msg);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
