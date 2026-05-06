package com.saphienyako.quest_giver.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record DismissEntityMessage(int entityId) {

    public static void encode(DismissEntityMessage msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.entityId());
    }

    public static DismissEntityMessage decode(FriendlyByteBuf buffer) {
        int id = buffer.readInt();

        return new DismissEntityMessage(id);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {

        Level level = supplier.get().getSender().level;
        if (this.entityId() != -1) {
            Entity entity = level.getEntity(this.entityId);
            if(entity != null) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }
}
