package com.saphienyako.quest_giver.quest.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Predicate;

public record IngredientStack(Ingredient ingredient, int count) implements Predicate<ItemStack> {

    public static final IngredientStack EMPTY = new IngredientStack(Ingredient.EMPTY, 0);

    public IngredientStack(Ingredient ingredient, int count) {
        this.ingredient = ingredient == null ? Ingredient.EMPTY : ingredient;
        this.count = Math.max(count, 0);
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return stack.getCount() >= this.count && ingredient.test(stack);
    }

    public boolean isEmpty() {
        return count <= 0 || ingredient.isEmpty();
    }

    public static IngredientStack fromJson(JsonObject json) {
        JsonElement elem = json.get("item");
        Ingredient ingredient = elem == null
                ? Ingredient.EMPTY
                : net.neoforged.neoforge.common.crafting.CraftingHelper.makeIngredientCodec(true)
                .parse(JsonOps.INSTANCE, elem)
                .getOrThrow(JsonSyntaxException::new);
        int count = json.has("amount") ? json.get("amount").getAsInt() : 1;
        return new IngredientStack(ingredient, count);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("item", net.neoforged.neoforge.common.crafting.CraftingHelper.makeIngredientCodec(true)
                .encodeStart(JsonOps.INSTANCE, ingredient)
                .getOrThrow(JsonSyntaxException::new));
        if (count != 1) {
            json.addProperty("amount", count);
        }
        return json;
    }
}
