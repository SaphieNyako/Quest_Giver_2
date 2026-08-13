package com.saphienyako.quest_giver.quest.task;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.List;

public class GiftTask implements TaskType<GiftTask.GiftRequirement, GiftTask.GiftContext> {

    public static final GiftTask INSTANCE = new GiftTask();

    private GiftTask() {}

    public record GiftRequirement(Ingredient ingredient, @Nullable Identifier entity, @Nullable String name) {}

    public record GiftContext(ItemStack stack, Entity target) {}

    @Override
    public Class<GiftRequirement> element() {
        return GiftRequirement.class;
    }

    @Override
    public Class<GiftContext> testType() {
        return GiftContext.class;
    }

    @Override
    public boolean checkCompleted(ServerPlayer player, GiftRequirement element, GiftContext match) {
        if (!element.ingredient().test(match.stack())) {
            return false;
        }

        Entity target = match.target();

        if (element.entity() != null) {
            Identifier targetId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());

            if (!element.entity().equals(targetId)) {
                return false;
            }
        }

        if (element.name() != null) {
            Component customName = target.getCustomName();

            if (customName == null || !element.name().equals(customName.getString())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public GiftRequirement fromJson(JsonObject json) {
        JsonElement itemElement = json.get("item");

        Ingredient ingredient;

        if (itemElement.isJsonPrimitive()) {
            Identifier itemId = Identifier.parse(itemElement.getAsString());

            Item item = BuiltInRegistries.ITEM
                    .get(itemId)
                    .map(Holder::value)
                    .orElseThrow(() -> new JsonSyntaxException("Unknown item: " + itemId));

            ingredient = Ingredient.of(item);
        } else {
            ingredient = Ingredient.CODEC
                    .parse(JsonOps.INSTANCE, itemElement)
                    .getOrThrow(JsonSyntaxException::new);
        }

        Identifier entity = null;

        if (json.has("entity")) {
            entity = Identifier.parse(json.get("entity").getAsString());
        }

        String name = null;

        if (json.has("name")) {
            name = json.get("name").getAsString();
        }

        return new GiftRequirement(ingredient, entity, name);
    }

    @Override
    public JsonObject toJson(GiftRequirement element) {
        JsonObject json = new JsonObject();

        if (!element.ingredient().isCustom()) {
            List<Holder<Item>> items = element.ingredient()
                    .getValues()
                    .stream()
                    .toList();

            if (items.size() == 1) {
                Identifier itemId = BuiltInRegistries.ITEM.getKey(items.getFirst().value());

                json.addProperty("item", itemId.toString());
            } else {
                JsonElement ingredientJson = Ingredient.CODEC
                        .encodeStart(JsonOps.INSTANCE, element.ingredient())
                        .getOrThrow(JsonSyntaxException::new);

                json.add("item", ingredientJson);
            }
        } else {
            JsonElement ingredientJson = Ingredient.CODEC
                    .encodeStart(JsonOps.INSTANCE, element.ingredient())
                    .getOrThrow(JsonSyntaxException::new);

            json.add("item", ingredientJson);
        }

        if (element.entity() != null) {
            json.addProperty("entity", element.entity().toString());
        }

        if (element.name() != null) {
            json.addProperty("name", element.name());
        }

        return json;
    }

    @Nullable
    @Override
    public Item icon(GiftRequirement element) {
        List<Holder<Item>> items = element.ingredient()
                .getValues()
                .stream()
                .toList();

        if (!items.isEmpty()) {
            return items.getFirst().value();
        }

        return null;
    }
}