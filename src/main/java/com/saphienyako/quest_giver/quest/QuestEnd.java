package com.saphienyako.quest_giver.quest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class QuestEnd {

    public final Item icon;
    public final QuestDisplay display;

    public QuestEnd(Item icon, QuestDisplay display) {
        this.icon = icon;
        this.display = display;
    }

    public static QuestEnd fromJson(JsonElement data) {
        JsonObject json = data.getAsJsonObject();

        ResourceLocation itemId = new ResourceLocation(json.get("icon").getAsString());

        Item icon = ForgeRegistries.ITEMS.getValue(itemId);

        if (icon == null) {
            throw new JsonParseException("Invalid end icon: " + itemId);
        }

        QuestDisplay display = QuestDisplay.fromJson(json.get("start").getAsJsonObject());

        return new QuestEnd(icon, display);
    }
}
