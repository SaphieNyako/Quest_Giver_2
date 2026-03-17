package com.saphienyako.quest_giver;

import com.saphienyako.quest_giver.quest.data.QuestData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, QuestGiver.MOD_ID);

    public static final Supplier<AttachmentType<QuestData>> QUESTS =
            ATTACHMENTS.register("quests", () ->
                    AttachmentType.builder(QuestData::new)
                            .serialize(QuestData.CODEC) //TODO create codec?
                            .copyOnDeath()
                            .build()
            );

    public static void register(IEventBus bus) {
        ATTACHMENTS.register(bus);
    }
}
