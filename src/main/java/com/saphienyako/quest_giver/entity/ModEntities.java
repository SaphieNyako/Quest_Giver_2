package com.saphienyako.quest_giver.entity;

import com.saphienyako.quest_giver.QuestGiver;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, QuestGiver.MOD_ID);

    public static final RegistryObject<EntityType<QuestVillagerEntity>> QUEST_VILLAGER =
            ENTITY_TYPES.register("quest_villager", () -> EntityType.Builder.of(QuestVillagerEntity::new, MobCategory.CREATURE).build("quest_villager"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
