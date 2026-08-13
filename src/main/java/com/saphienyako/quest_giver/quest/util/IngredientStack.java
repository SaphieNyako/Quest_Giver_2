package com.saphienyako.quest_giver.quest.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Predicate;

public record IngredientStack(Ingredient ingredient, int count)
        implements Predicate<ItemStack> {

    public static final IngredientStack EMPTY =
            new IngredientStack(null, 0);

    public IngredientStack {
        count = Math.max(count, 0);
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack.isEmpty() || ingredient == null || count <= 0) {
            return false;
        }

        return stack.getCount() >= count && ingredient.test(stack);
    }

    public boolean isEmpty() {
        return count <= 0 || ingredient == null;
    }

    public static IngredientStack fromJson(JsonObject json) {
        JsonElement elem = json.get("item");

        if (elem == null) {
            return EMPTY;
        }

        Ingredient ingredient = Ingredient.CODEC
                .parse(JsonOps.INSTANCE, elem)
                .getOrThrow(JsonSyntaxException::new);

        int count = json.has("amount") ? json.get("amount").getAsInt() : 1;

        return new IngredientStack(ingredient, count);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        if (ingredient != null) {
            json.add("item", Ingredient.CODEC
                            .encodeStart(JsonOps.INSTANCE, ingredient)
                            .getOrThrow(JsonSyntaxException::new)
            );
        }

        if (count != 1) {
            json.addProperty("amount", count);
        }

        return json;
    }
}
