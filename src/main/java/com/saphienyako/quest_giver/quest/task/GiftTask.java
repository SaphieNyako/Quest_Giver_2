package com.saphienyako.quest_giver.quest.task;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;

public class GiftTask implements TaskType<Ingredient, ItemStack> {

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
        return Ingredient.fromJson(json.get("item"));
    }

    @Override
    public JsonObject toJson(Ingredient element) {
        JsonObject json = new JsonObject();
        json.add("item", element.toJson());
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
