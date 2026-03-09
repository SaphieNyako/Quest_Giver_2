package com.saphienyako.quest_giver.quest.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemStackHelper {

    public static JsonObject toJson(ItemStack stack, boolean writeNBT) {
        JsonObject json = new JsonObject(); ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null) {
            throw new IllegalStateException("Item is not registered: " + stack.getItem());
        }

        json.addProperty("item", id.toString());
        if (stack.getCount() != 1) {
            json.addProperty("count", stack.getCount());
        }
        if (writeNBT && stack.hasTag()) {
            json.addProperty("nbt", stack.getTag().toString());
        }

        return json;
    }

    public static ItemStack fromJson(JsonObject json) {
        ResourceLocation id = new ResourceLocation(json.get("item").getAsString());

        Item item = ForgeRegistries.ITEMS.getValue(id);
        if (item == null) {
            throw new IllegalStateException("Unknown item: " + id);
        }

        int count = json.has("count") ? json.get("count").getAsInt() : 1; ItemStack stack = new ItemStack(item, count);

        if (json.has("nbt")) {

            try {
                CompoundTag tag = TagParser.parseTag(json.get("nbt").getAsString()); stack.setTag(tag);
            }
            catch (CommandSyntaxException e) {
                throw new JsonParseException("Invalid NBT for item: " + id, e);
            }
        }

        return stack;
    }

}
