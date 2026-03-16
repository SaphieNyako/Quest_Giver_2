package com.saphienyako.quest_giver.quest.task;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;

public class GiftTask implements TaskType<Ingredient, ItemStack> {

    //TODO GiftToSpecific Name or Entity Task

    public static final GiftTask INSTANCE = new GiftTask();

    private GiftTask() {}

    @Override
    public Class<Ingredient> element() {
        return Ingredient.class;
    }

    @Override
    public Class<ItemStack> testType() {
        return ItemStack.class;
    }

    @Override
    public boolean checkCompleted(ServerPlayer player, Ingredient element, ItemStack match) {
        return element.test(match);
    }

    @Override
    public Ingredient fromJson(JsonObject json) {
        JsonElement itemJson = json.get("item");
        return Ingredient.CODEC
                .parse(JsonOps.INSTANCE, itemJson)
                .getOrThrow(msg -> new JsonSyntaxException("Failed to parse Ingredient: " + msg));
    }

    @Override
    public JsonObject toJson(Ingredient element) {
        JsonObject json = new JsonObject();
        JsonElement ingredientJson = Ingredient.CODEC
                .encodeStart(JsonOps.INSTANCE, element)
                .getOrThrow(msg -> new JsonSyntaxException("Failed to encode Ingredient: " + msg));
        json.add("item", ingredientJson);
        return json;
    }

    @Nullable
    @Override
    public Item icon(Ingredient element) {
        ItemStack[] stacks = element.getItems();
        if (stacks.length > 0 && !stacks[0].isEmpty()) {
            return stacks[0].getItem();
        }
        return null;
    }
}
