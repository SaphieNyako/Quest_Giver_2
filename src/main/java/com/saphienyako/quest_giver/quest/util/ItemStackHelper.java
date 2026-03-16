package com.saphienyako.quest_giver.quest.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public class ItemStackHelper {

    public static JsonObject toJson(ItemStack stack) {
        JsonObject json = new JsonObject();
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id == null) throw new IllegalStateException("Item not registered: " + stack.getItem());

        json.addProperty("item", id.toString());
        if (stack.getCount() != 1) json.addProperty("count", stack.getCount());

        // Serialize all DataComponents
        if (!stack.getComponents().isEmpty()) {
            json.addProperty("components", stack.saveOptional(null).toString());
        }

        return json;
    }

    public static ItemStack fromJson(JsonObject json) {
        ResourceLocation id = ResourceLocation.tryParse(json.get("item").getAsString());
        Item item = BuiltInRegistries.ITEM.get(id);
        if (item == null) throw new IllegalStateException("Unknown item: " + id);

        int count = json.has("count") ? json.get("count").getAsInt() : 1;
        ItemStack stack = new ItemStack(item, count);

        if (json.has("components")) {
            try {
                CompoundTag tag = TagParser.parseTag(json.get("components").getAsString());
                stack = ItemStack.parseOptional(null, tag);
            } catch (CommandSyntaxException e) {
                throw new JsonParseException("Invalid component data for item: " + id, e);
            }
        }

        return stack;
    }

}
