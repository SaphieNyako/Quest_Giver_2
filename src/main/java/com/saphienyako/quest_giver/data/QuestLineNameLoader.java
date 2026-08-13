package com.saphienyako.quest_giver.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saphienyako.quest_giver.QuestGiver;
import com.saphienyako.quest_giver.quest.QuestLineRegistry;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nonnull;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class QuestLineNameLoader extends SimplePreparableReloadListener<Set<String>> {

    private static final Identifier FILE =
            Identifier.fromNamespaceAndPath(QuestGiver.MOD_ID, "quest_line_names.json");

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
                        QuestGiver.LOGGER.debug("Quest line list overwritten by pack: {}", resource.sourcePackId());
                    }

                    JsonArray names = json.getAsJsonArray("names");

                    if (names == null) {
                        QuestGiver.LOGGER.warn("Quest line file missing 'names' array");
                        continue;
                    }

                    for (JsonElement elem : names) {

                        String name = elem.getAsString().toLowerCase(Locale.ROOT);
                        if (!result.add(name)) {
                            QuestGiver.LOGGER.warn(
                                    "Quest line '{}' already registered (pack {})",
                                    name,
                                    resource.sourcePackId()
                            );
                        }

                    }

                }

            }

        } catch (Exception e) {

            QuestGiver.LOGGER.error("Failed loading quest line names", e);

        }

        return result;
    }

    @Override
    protected void apply(@Nonnull Set<String> names,@Nonnull ResourceManager manager,@Nonnull ProfilerFiller profiler) {

        QuestGiver.LOGGER.info("Loaded {} quest lines: {}", names.size(), names);

        QuestLineRegistry.setAll(names);

    }
}
