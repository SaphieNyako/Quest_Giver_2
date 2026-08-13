package com.saphienyako.quest_giver.quest.task;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;



import java.util.Objects;

public abstract class RegistryTaskType<T, X> implements TaskType<ResourceKey<T>, X> {

    private final String key;

    protected RegistryTaskType(String key) {
        this.key = key;
    }

    public abstract Registry<T> registry();

    @Override
    @SuppressWarnings("unchecked")
    public Class<ResourceKey<T>> element() {
        return (Class<ResourceKey<T>>) (Class<?>) ResourceKey.class;
    }

    @Override
    public ResourceKey<T> fromJson(JsonObject json) {
        Identifier rl = Identifier.parse(json.get(this.key).getAsString());

        Registry<T> registry = Objects.requireNonNull(this.registry());

        if (!registry.containsKey(rl)) {
            throw new IllegalStateException("Can't load quest task: entry not found in registry: " + rl);
        }

        return ResourceKey.create(registry.key(), rl);
    }

    @Override
    public JsonObject toJson(ResourceKey<T> element) {
        JsonObject json = new JsonObject();
        json.addProperty(this.key, element.identifier().toString());
        return json;
    }
}
