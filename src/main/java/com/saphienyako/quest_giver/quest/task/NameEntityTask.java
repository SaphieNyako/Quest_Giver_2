package com.saphienyako.quest_giver.quest.task;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class NameEntityTask implements TaskType<NameEntityTask.Requirement, NameEntityTask.Context> {

    public static final NameEntityTask INSTANCE = new NameEntityTask();

    private NameEntityTask() {
    }

    public record Requirement(@Nullable ResourceLocation entity, @Nullable String name) {
    }

    public record Context(Entity entity, String name) {
    }

    @Override
    public Class<Requirement> element() {
        return Requirement.class;
    }

    @Override
    public Class<Context> testType() {
        return Context.class;
    }

    @Override
    public boolean checkCompleted(ServerPlayer player, Requirement requirement, Context context) {

        if (requirement.entity() != null) {
            ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(context.entity().getType());

            if (!requirement.entity().equals(entityId)) {
                return false;
            }
        }

        if (requirement.name() != null) {
            if (!requirement.name().equals(context.name())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Requirement fromJson(JsonObject json) {


        ResourceLocation entity = null;
        String name = null;

        if (json.has("entity")) {
            entity = new ResourceLocation(GsonHelper.getAsString(json, "entity"));
        }

        if (json.has("name")) {
            name = GsonHelper.getAsString(json, "name");
        }

        return new Requirement(entity, name);
    }

    @Override
    public JsonObject toJson(Requirement requirement) {

        JsonObject json = new JsonObject();

        if (requirement.entity() != null) {
            json.addProperty("entity", requirement.entity().toString());
        }

        if (requirement.name() != null) {
            json.addProperty("name", requirement.name());
        }

        return json;
    }
}
