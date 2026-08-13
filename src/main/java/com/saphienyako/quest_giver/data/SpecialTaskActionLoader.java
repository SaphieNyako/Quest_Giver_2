package com.saphienyako.quest_giver.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.quest.util.SpecialTaskAction;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nonnull;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SpecialTaskActionLoader extends SimplePreparableReloadListener<Set<String>> {

    private static final Identifier FILE = Identifier.fromNamespaceAndPath(QuestGiver.MOD_ID, "special_task_actions.json");

    @Nonnull
    @Override
    protected Set<String> prepare(@Nonnull ResourceManager manager,@Nonnull ProfilerFiller profiler) {

        Set<String> result = new LinkedHashSet<>();

        try {

            List<Resource> stack = manager.getResourceStack(FILE);

            for (Resource resource : stack) {

                try (Reader reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {

                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                    boolean overwrite = json.has("overwrite") && json.get("overwrite").getAsBoolean();

                    if (overwrite) {
                        result.clear();
                        QuestGiver.LOGGER.debug(
                                "Special task actions overwritten by pack {}",
                                resource.sourcePackId()
                        );
                    }

                    JsonArray actions = json.getAsJsonArray("actions");

                    if (actions == null) {
                        QuestGiver.LOGGER.warn("special_task_actions.json missing 'actions' array");
                        continue;
                    }

                    for (JsonElement elem : actions) {

                        String name = elem.getAsString().toLowerCase(Locale.ROOT);

                        if (!result.add(name)) {
                            QuestGiver.LOGGER.warn(
                                    "Duplicate special task action '{}' from pack {}",
                                    name,
                                    resource.sourcePackId()
                            );
                        }

                    }

                }

            }

        } catch (Exception e) {
            QuestGiver.LOGGER.error("Failed loading special task actions", e);
        }

        return result;
    }

    @Override
    protected void apply(@Nonnull Set<String> actions,@Nonnull ResourceManager manager,@Nonnull ProfilerFiller profiler) {

        QuestGiver.LOGGER.info("Loaded {} special task actions: {}", actions.size(), actions);

        SpecialTaskAction.setAll(actions);

    }

}
