package com.saphienyako.quest_giver.quest.task;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;


public class AnimalPetTask extends RegistryTaskType<EntityType<?>, Entity> {

    public static final AnimalPetTask INSTANCE = new AnimalPetTask();

    private AnimalPetTask() {
        super("type");
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
        return element.identifier().equals(BuiltInRegistries.ENTITY_TYPE.getKey(match.getType()));
    }
}
