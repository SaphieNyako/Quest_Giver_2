package com.saphienyako.quest_giver;

import com.mojang.logging.LogUtils;
import com.saphienyako.quest_giver.data.QuestLineNameLoader;
import com.saphienyako.quest_giver.data.QuestLinkDataLoader;
import com.saphienyako.quest_giver.data.SpecialTaskActionLoader;
import com.saphienyako.quest_giver.entity.ModEntities;
import com.saphienyako.quest_giver.entity.QuestVillagerEntity;
import com.saphienyako.quest_giver.network.QuestGiverNetwork;
import com.saphienyako.quest_giver.quest.QuestCommands;
import com.saphienyako.quest_giver.quest.QuestManager;
import com.saphienyako.quest_giver.quest.reward.CommandReward;
import com.saphienyako.quest_giver.quest.reward.ItemReward;
import com.saphienyako.quest_giver.quest.reward.RewardTypes;
import com.saphienyako.quest_giver.quest.task.*;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(QuestGiver.MOD_ID)
public class QuestGiver
{
    public static final String MOD_ID = "quest_giver";
    public static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("removal")
    public QuestGiver(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::entityAttributes);

        ModEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModAttachments.register(modEventBus);

        modEventBus.addListener(QuestGiverNetwork::register);

        NeoForge.EVENT_BUS.addListener(this::reloadData);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);


        NeoForge.EVENT_BUS.register(new EventListener());

        // Quest task & reward types
        TaskTypes.register(Identifier.fromNamespaceAndPath(MOD_ID,"craft"), CraftTask.INSTANCE);
        TaskTypes.register(Identifier.fromNamespaceAndPath(MOD_ID,"gift"), GiftTask.INSTANCE);
        TaskTypes.register(Identifier.fromNamespaceAndPath(MOD_ID,"item_stack"), ItemStackTask.INSTANCE);
        TaskTypes.register(Identifier.fromNamespaceAndPath(MOD_ID,"kill"), KillTask.INSTANCE);
        TaskTypes.register(Identifier.fromNamespaceAndPath(MOD_ID,"pet"), AnimalPetTask.INSTANCE);
        TaskTypes.register(Identifier.fromNamespaceAndPath(MOD_ID,"tame"), AnimalTameTask.INSTANCE);
        TaskTypes.register(Identifier.fromNamespaceAndPath(MOD_ID,"biome"), BiomeTask.INSTANCE);
        TaskTypes.register(Identifier.fromNamespaceAndPath(MOD_ID,"structure"), StructureTask.INSTANCE);
        //  TaskTypes.register(Identifier.fromNamespaceAndPath(MOD_ID,"tree"), GrowTreeTask.INSTANCE);
        TaskTypes.register(Identifier.fromNamespaceAndPath(MOD_ID, "complete_quest"), CompleteQuestTask.INSTANCE);
        TaskTypes.register(Identifier.fromNamespaceAndPath(MOD_ID,"special_task"), SpecialTask.INSTANCE);

        RewardTypes.register(Identifier.fromNamespaceAndPath(MOD_ID, "item"), ItemReward.INSTANCE);
        RewardTypes.register(Identifier.fromNamespaceAndPath(MOD_ID, "command"), CommandReward.INSTANCE);


    }

    private void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.QUEST_VILLAGER.get(), Villager.createAttributes().build());
    }

    public void reloadData(AddServerReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "quest_line_names"), new QuestLineNameLoader());
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "special_task_actions"), new SpecialTaskActionLoader());
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "quests"), QuestManager.createReloadListener());
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "quest_links"), new QuestLinkDataLoader());
    }

    private void registerCommands(RegisterCommandsEvent event) {
        QuestCommands.register(event.getDispatcher());
    }

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.QUEST_VILLAGER.get(), VillagerRenderer::new);
        }
    }

    @EventBusSubscriber(modid = MOD_ID)
    public static class ModEvents {

        @SubscribeEvent
        public static void spawnPlacement(RegisterSpawnPlacementsEvent event) {
            event.register(ModEntities.QUEST_VILLAGER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, QuestVillagerEntity::canSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }
    }
}
