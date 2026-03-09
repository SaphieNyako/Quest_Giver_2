package com.saphienyako.quest_giver;

import com.mojang.logging.LogUtils;
import com.saphienyako.quest_giver.network.QuestGiverNetwork;
import com.saphienyako.quest_giver.quest.QuestManager;
import com.saphienyako.quest_giver.quest.player.CapabilityQuests;
import com.saphienyako.quest_giver.quest.reward.CommandReward;
import com.saphienyako.quest_giver.quest.reward.ItemReward;
import com.saphienyako.quest_giver.quest.reward.RewardTypes;
import com.saphienyako.quest_giver.quest.task.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(QuestGiver.MOD_ID)
public class QuestGiver
{
    public static final String MOD_ID = "quest_giver";
    public static final Logger LOGGER = LogUtils.getLogger();

    public QuestGiver() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        //Datapack for Quests
        MinecraftForge.EVENT_BUS.addListener(this::reloadData);
        //Player Capabilities
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityQuests::attachPlayerCaps);
        MinecraftForge.EVENT_BUS.addListener(CapabilityQuests::playerCopy);

        // Quest task & reward types. Not in setup as they are required for datagen.
        TaskTypes.register(new ResourceLocation(MOD_ID,"craft"), CraftTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"fey_gift"), GiftTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"item_stack"), ItemStackTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"kill"), KillTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"pet"), AnimalPetTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"tame"), AnimalTameTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"biome"), BiomeTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"structure"), StructureTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"tree"), GrowTreeTask.INSTANCE);
        TaskTypes.register(new ResourceLocation(MOD_ID,"special"), SpecialTask.INSTANCE);

        RewardTypes.register(new ResourceLocation(MOD_ID, "item"), ItemReward.INSTANCE);
        RewardTypes.register(new ResourceLocation(MOD_ID, "command"), CommandReward.INSTANCE);


    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(QuestGiverNetwork::register);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        //Added ModCreativeModeTab for the mod itself
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }

    public void reloadData(AddReloadListenerEvent event) {
        event.addListener(QuestManager.createReloadListener());
    }
}
