package com.saphienyako.quest_giver.entity;

import com.saphienyako.quest_giver.QuestGiver;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, QuestGiver.MOD_ID);

    public static final ResourceKey<EntityType<?>> QUEST_VILLAGER_KEY = ResourceKey.create(Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(QuestGiver.MOD_ID, "quest_villager"));

    public static final Supplier<EntityType<QuestVillagerEntity>> QUEST_VILLAGER =
            ENTITY_TYPES.register("quest_villager", () -> EntityType.Builder.of(QuestVillagerEntity::new, MobCategory.CREATURE).build(QUEST_VILLAGER_KEY));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
