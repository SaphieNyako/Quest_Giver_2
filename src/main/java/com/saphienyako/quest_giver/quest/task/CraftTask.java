package com.saphienyako.quest_giver.quest.task;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import com.google.gson.JsonObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class CraftTask implements TaskType<Ingredient, ItemStack> {

    public static final CraftTask INSTANCE = new CraftTask();

    private CraftTask() { }

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
        // Manually convert JSON -> Ingredient
        JsonArray array = json.getAsJsonArray("item");
        ItemStack[] stacks = new ItemStack[array.size()];
        for (int i = 0; i < array.size(); i++) {
            String itemId = array.get(i).getAsString();
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(itemId));
            if (item == null) item = Items.AIR;
            stacks[i] = new ItemStack(item);
        }
        return Ingredient.of(stacks);
    }

    @Override
    public JsonObject toJson(Ingredient element) {
        // Manually convert Ingredient -> JSON
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        for (ItemStack stack : element.getItems()) {
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (key != null) {
                array.add(key.toString());
            }
        }
        json.add("item", array);
        return json;
    }

    @Nullable
    @Override
    public Item icon(Ingredient element) {
        ItemStack[] matching = element.getItems();
        if (matching.length == 1 && !matching[0].isEmpty()) {
            return matching[0].getItem();
        }
        return null;
    }
}