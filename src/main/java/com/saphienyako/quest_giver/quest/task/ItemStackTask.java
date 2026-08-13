package com.saphienyako.quest_giver.quest.task;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import com.saphienyako.quest_giver.quest.util.IngredientStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.List;

public class ItemStackTask implements TaskType<IngredientStack, ItemStack> {

    public static final ItemStackTask INSTANCE = new ItemStackTask();

    private ItemStackTask() {

    }

    @Override
    public Class<IngredientStack> element() {
        return IngredientStack.class;
    }

    @Override
    public Class<ItemStack> testType() {
        return ItemStack.class;
    }

    @Override
    public boolean checkCompleted(ServerPlayer player, IngredientStack element, ItemStack match) {
        return element.test(match);
    }

    @Override
    public IngredientStack fromJson(JsonObject json) {
        JsonElement itemJson = json.get("item");

        if (!itemJson.isJsonPrimitive()) {
            throw new JsonSyntaxException(
                    "ItemStackTask item must be an item id, got: " + itemJson
            );
        }

        Identifier itemId = Identifier.parse(itemJson.getAsString());

        Item item = BuiltInRegistries.ITEM
                .get(itemId)
                .map(Holder::value)
                .orElseThrow(() -> new JsonSyntaxException("Unknown item: " + itemId));

        Ingredient ingredient = Ingredient.of(item);

        int amount = json.has("amount") ? json.get("amount").getAsInt() : 1;

        return new IngredientStack(ingredient, amount);
    }

    @Override
    public JsonObject toJson(IngredientStack element) {
        JsonObject json = new JsonObject();

        List<Holder<Item>> items = element.ingredient()
                .getValues()
                .stream()
                .toList();

        if (items.size() != 1) {
            throw new IllegalStateException("ItemStackTask currently expects exactly one item ingredient");
        }

        Identifier itemId = BuiltInRegistries.ITEM.getKey(items.getFirst().value());

        if (itemId == null) {
            throw new IllegalStateException("ItemStackTask item is not registered");
        }

        json.addProperty("item", itemId.toString());

        if (element.count() != 1) {
            json.addProperty("amount", element.count());
        }

        return json;
    }

    @Override
    public boolean repeatable() {
        return false;
    }

    @Nullable
    @Override
    public Item icon(IngredientStack element) {
        List<Holder<Item>> matching = element.ingredient()
                .getValues()
                .stream()
                .toList();

        if (matching.size() == 1) {
            return matching.getFirst().value();
        }

        return null;
    }
}
