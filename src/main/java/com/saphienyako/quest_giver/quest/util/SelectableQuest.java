package com.saphienyako.quest_giver.quest.util;

import com.saphienyako.quest_giver.quest.QuestDisplay;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.core.Holder;

import java.util.Objects;

public record SelectableQuest(Identifier id, Item icon, QuestDisplay display) {

    public static SelectableQuest fromNetwork(FriendlyByteBuf buffer) {
        Identifier id = buffer.readIdentifier();
        Item icon = BuiltInRegistries.ITEM
                .get(buffer.readIdentifier())
                .map(Holder::value)
                .orElseThrow(() -> new IllegalStateException("Unknown item received for selectable quest"));

        QuestDisplay display = QuestDisplay.fromNetwork(buffer);
        return new SelectableQuest(id, icon, display);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeIdentifier(this.id());
        buffer.writeIdentifier(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(this.icon()), "Item not registered"));
        this.display.toNetwork(buffer);
    }
}
