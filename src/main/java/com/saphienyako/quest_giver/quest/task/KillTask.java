package com.saphienyako.quest_giver.quest.task;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;


public class KillTask extends RegistryTaskType<EntityType<?>, Entity> {

    public static final KillTask INSTANCE = new KillTask();

    private KillTask() {
        super("entity");
    }

    @Override
    public Registry<EntityType<?>> registry() {
        return BuiltInRegistries.ENTITY_TYPE;
    }

    @Override
    public Class<Entity> testType() {
        return Entity.class;
    }

    @Override
    public boolean checkCompleted(ServerPlayer player, ResourceKey<EntityType<?>> element, Entity match) {
        return element.location().equals(BuiltInRegistries.ENTITY_TYPE.getKey(match.getType()));
    }
}
