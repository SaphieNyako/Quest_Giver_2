package com.saphienyako.quest_giver;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(QuestGiver.MOD_ID);

    public static final DeferredItem<Item> QUEST_SCROLL = ITEMS.registerSimpleItem("quest_scroll");

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
