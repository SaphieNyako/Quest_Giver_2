package com.saphienyako.quest_giver.quest.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.ItemStack;


public class ItemStackHelper {

    public static JsonObject toJson(ItemStack stack) {
        JsonElement encoded = ItemStack.CODEC
                .encodeStart(JsonOps.INSTANCE, stack)
                .getOrThrow();

        return encoded.getAsJsonObject();
    }

    public static ItemStack fromJson(JsonObject json) {
        return ItemStack.CODEC
                .parse(JsonOps.INSTANCE, json)
                .getOrThrow();
    }
}
