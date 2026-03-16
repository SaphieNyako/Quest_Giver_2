package com.saphienyako.quest_giver.quest.util;

import com.saphienyako.quest_giver.quest.QuestDisplay;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Objects;

public record SelectableQuest(ResourceLocation id, Item icon,
                              com.saphienyako.quest_giver.quest.QuestDisplay display) {

    public static SelectableQuest fromNetwork(FriendlyByteBuf buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        Item icon = BuiltInRegistries.ITEM.get(buffer.readResourceLocation());
        QuestDisplay display = QuestDisplay.fromNetwork(buffer);
        return new SelectableQuest(id, icon, display);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.id());
        buffer.writeResourceLocation(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(this.icon()), "Item not registered"));
        this.display.toNetwork(buffer);
    }
}
