package com.saphienyako.quest_giver.network;


import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.quest.data.QuestData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;


import java.util.function.Supplier;

public record ConfirmQuestMessage(String questLineId, boolean accept) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ConfirmQuestMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(QuestGiver.MOD_ID, "confirm_quest"));

    public static final StreamCodec<FriendlyByteBuf, ConfirmQuestMessage> STREAM_CODEC =
            StreamCodec.of(ConfirmQuestMessage::encode, ConfirmQuestMessage::decode);

    private static void encode(FriendlyByteBuf buffer, ConfirmQuestMessage msg) {
        buffer.writeUtf(msg.questLineId());
        buffer.writeBoolean(msg.accept());
    }

    private static ConfirmQuestMessage decode(FriendlyByteBuf buffer) {
        return new ConfirmQuestMessage(buffer.readUtf(), buffer.readBoolean());
    }
    @SuppressWarnings("resource")
    public static void handle(ConfirmQuestMessage msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.level().isClientSide) return;
            if (player != null) {
                QuestData data = QuestData.get((ServerPlayer) player);
                if (msg.accept()) {
                    data.acceptQuestLine(msg.questLineId());
                } else {
                    data.denyQuestLine(msg.questLineId());
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
