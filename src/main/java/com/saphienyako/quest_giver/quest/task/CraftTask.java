package com.saphienyako.quest_giver.quest.task;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CraftTask implements TaskType<Ingredient, ItemStack> {

    public static final CraftTask INSTANCE = new CraftTask();

    private CraftTask() {
    }

    @Override
    public Class<Ingredient> element() {
        return Ingredient.class;
    }

    @Override
    public Class<ItemStack> testType() {
        return ItemStack.class;
    }

    @Override
    public boolean checkCompleted(
            ServerPlayer player,
            Ingredient element,
            ItemStack match
    ) {
        return element.test(match);
    }

    @Override
    public Ingredient fromJson(JsonObject json) {
        JsonElement elem = json.get("item");

        if (!elem.isJsonPrimitive()) {
            throw new JsonSyntaxException("Craft task item must be an item id, got: " + elem);
        }

        Identifier itemId = Identifier.parse(elem.getAsString());

        Item item = BuiltInRegistries.ITEM
                .get(itemId)
                .map(Holder::value)
                .orElseThrow(() -> new JsonSyntaxException("Unknown item: " + itemId));

        return Ingredient.of(item);
    }

    @Override
    public JsonObject toJson(Ingredient element) {
        JsonObject json = new JsonObject();

        List<Holder<Item>> items = element.getValues()
                .stream()
                .toList();

        if (items.size() != 1) {
            throw new IllegalStateException("CraftTask currently expects exactly one item ingredient");
        }

        Identifier itemId = BuiltInRegistries.ITEM.getKey(items.getFirst().value());

        if (itemId == null) {
            throw new IllegalStateException("Craft task item is not registered");
        }

        json.addProperty("item", itemId.toString());

        return json;
    }

    @Nullable
    @Override
    public Item icon(Ingredient element) {
        List<Holder<Item>> matching = element.getValues()
                .stream()
                .toList();

        if (matching.size() == 1) {
            return matching.getFirst().value();
        }

        return null;
    }
}