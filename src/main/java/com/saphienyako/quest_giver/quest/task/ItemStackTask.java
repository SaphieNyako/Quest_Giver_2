package com.saphienyako.quest_giver.quest.task;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import com.saphienyako.quest_giver.quest.util.IngredientStack;
import net.minecraft.core.Holder;
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

        Ingredient ingredient;
        try {
            ingredient = Ingredient.CODEC.parse(JsonOps.INSTANCE, itemJson).getOrThrow(JsonSyntaxException::new);
        } catch (Exception e) {
            throw new JsonSyntaxException("Failed to parse Ingredient from JSON", e);
        }

        int amount = json.has("amount") ? json.get("amount").getAsInt() : 1;
        return new IngredientStack(ingredient, amount);
    }

    @Override
    public JsonObject toJson(IngredientStack element) {
        JsonObject json = new JsonObject();

        // Use the Ingredient codec to encode
        JsonElement ingredientJson = Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, element.ingredient())
                .getOrThrow(JsonSyntaxException::new);

        json.add("item", ingredientJson);
        json.addProperty("amount", element.count());
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
