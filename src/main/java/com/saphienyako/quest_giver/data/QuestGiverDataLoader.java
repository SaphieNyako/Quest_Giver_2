package com.saphienyako.quest_giver.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class QuestGiverDataLoader {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .setLenient()
            .create();

    public interface JsonFactory<T> {
        T create(Identifier id, JsonElement json);
    }

    public static <T> Map<Identifier, T> loadJson(ResourceManager manager, String basePath, JsonFactory<T> factory) throws IOException {

        Map<Identifier, T> result = new HashMap<>();

        Map<Identifier, Resource> resources = manager.listResources(basePath, rl -> rl.getPath().endsWith(".json"));

        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {

            Identifier fileLocation = entry.getKey();
            Resource resource = entry.getValue();

            // convert path -> quest id
            String path = fileLocation.getPath();
            path = path.substring(basePath.length() + 1, path.length() - ".json".length());

            Identifier id = Identifier.fromNamespaceAndPath(fileLocation.getNamespace(), path);

            try (Reader reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {

                JsonElement json = JsonParser.parseReader(reader);

                T value = factory.create(id, json);

                if (value != null) {
                    result.put(id, value);
                }

            }
        }

        return Map.copyOf(result);
    }
}

