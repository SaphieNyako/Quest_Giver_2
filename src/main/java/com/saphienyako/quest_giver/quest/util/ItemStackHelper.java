package com.saphienyako.quest_giver.quest.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;


public class ItemStackHelper {

    public static JsonObject toJson(ItemStackTemplate template) {
        JsonObject json = new JsonObject();

        Identifier id = BuiltInRegistries.ITEM.getKey(
                template.typeHolder().value()
        );

        if (id == null) {
            throw new IllegalStateException("Item not registered: " + template.typeHolder().value());
        }

        json.addProperty("item", id.toString());

        if (template.count() != 1) {
            json.addProperty("count", template.count());
        }

        return json;
    }

    public static ItemStackTemplate fromJson(JsonObject json) {
        Identifier id = Identifier.parse(json.get("item").getAsString());

        Holder<Item> item = BuiltInRegistries.ITEM
                .get(id)
                .orElseThrow(() -> new JsonParseException("Unknown item: " + id));

        int count = json.has("count") ? json.get("count").getAsInt() : 1;

        return new ItemStackTemplate(item, count, DataComponentPatch.EMPTY);
    }
}
