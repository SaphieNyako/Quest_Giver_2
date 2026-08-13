package com.saphienyako.quest_giver.quest.task;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
        JsonElement elem = json.get("item");

        List<Item> items = new ArrayList<>();

        if (elem.isJsonArray()) {
            JsonArray array = elem.getAsJsonArray();

            for (JsonElement entry : array) {
                String itemId = entry.getAsString();

                BuiltInRegistries.ITEM
                        .get(Identifier.tryParse(itemId))
                        .map(Holder::value)
                        .ifPresent(items::add);
            }
        } else if (elem.isJsonObject()) {
            String itemId = elem.getAsJsonObject()
                    .get("item")
                    .getAsString();

            BuiltInRegistries.ITEM
                    .get(Identifier.tryParse(itemId))
                    .map(Holder::value)
                    .ifPresent(items::add);
        }

        return Ingredient.of(items.stream());
    }


    @Override
    public JsonObject toJson(Ingredient element) {
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();

        element.getValues().stream().forEach(holder -> {
            Identifier key = BuiltInRegistries.ITEM.getKey(holder.value());

            if (key != null) {
                array.add(key.toString());
            }
        });

        json.add("item", array);
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