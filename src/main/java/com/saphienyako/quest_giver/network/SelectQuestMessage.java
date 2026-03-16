package com.saphienyako.quest_giver.network;


import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.quest.QuestDisplay;
import com.saphienyako.quest_giver.quest.data.QuestData;
import com.saphienyako.quest_giver.quest.data.QuestLineData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;


import java.util.function.Supplier;

public record SelectQuestMessage(String questLineId, ResourceLocation quest, String backgroundName)  implements CustomPacketPayload {


    public static final CustomPacketPayload.Type<SelectQuestMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(QuestGiver.MOD_ID, "select_quest"));

    public static final StreamCodec<FriendlyByteBuf, SelectQuestMessage> STREAM_CODEC =
            StreamCodec.of(SelectQuestMessage::encode, SelectQuestMessage::decode);

    private static void encode(FriendlyByteBuf buffer, SelectQuestMessage msg) {
        buffer.writeUtf(msg.questLineId());
        buffer.writeResourceLocation(msg.quest());
        buffer.writeUtf(msg.backgroundName());
    }

    private static SelectQuestMessage decode(FriendlyByteBuf buffer) {
        String questLineId = buffer.readUtf();
        ResourceLocation quest = buffer.readResourceLocation();
        String backgroundName = buffer.readUtf();
        return new SelectQuestMessage(questLineId, quest, backgroundName);
    }
    @SuppressWarnings("resource")
    public static void handle(SelectQuestMessage msg, IPayloadContext context) {

        context.enqueueWork(() -> {
            Player player = context.player();
            if (player == null || player.level().isClientSide) return;
            ServerPlayer serverPlayer = (ServerPlayer) player;
                QuestData quests = QuestData.get((ServerPlayer) player);
                QuestLineData line = quests.getQuestLine(msg.questLineId());
                if (line != null) {
                    // Convert String questId → ResourceLocation internally
                    QuestDisplay display = line.getActiveQuestDisplay(msg.quest());

                    if (display != null) {
                        PacketDistributor.sendToPlayer(
                                serverPlayer,
                                new OpenQuestDisplayMessage(display, false, -1, msg.questLineId(), msg.backgroundName())
                        );
                    }
                }
        });

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
