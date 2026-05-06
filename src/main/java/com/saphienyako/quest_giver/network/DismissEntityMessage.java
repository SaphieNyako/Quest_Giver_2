package com.saphienyako.quest_giver.network;

import com.saphienyako.quest_giver.QuestGiver;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record DismissEntityMessage(int entityId) implements CustomPacketPayload {

    public static final Type<DismissEntityMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(QuestGiver.MOD_ID, "dismiss_entity"));

    public static final StreamCodec<FriendlyByteBuf, DismissEntityMessage> STREAM_CODEC =
            StreamCodec.of(DismissEntityMessage::encode, DismissEntityMessage::decode);

    private static void encode(FriendlyByteBuf buf, DismissEntityMessage msg) {
        buf.writeInt(msg.entityId());
    }

    private static DismissEntityMessage decode(FriendlyByteBuf buf) {
        int id = buf.readInt();
        return new DismissEntityMessage(id);
    }
    @SuppressWarnings("resource")
    public static void handle(DismissEntityMessage msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.level().isClientSide) return;

            Level level = player.level();
            if (!level.isClientSide && msg.entityId() != -1) {
                Entity entity = level.getEntity(msg.entityId());
                entity.remove(Entity.RemovalReason.DISCARDED);
                }
            }
        );
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
