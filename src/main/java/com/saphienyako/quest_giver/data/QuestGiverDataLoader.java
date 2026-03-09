package com.saphienyako.quest_giver.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
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
        T create(ResourceLocation id, JsonElement json);
    }

    public static <T> Map<ResourceLocation, T> loadJson(ResourceManager manager, String basePath, JsonFactory<T> factory) throws IOException {

        Map<ResourceLocation, T> result = new HashMap<>();

        Map<ResourceLocation, Resource> resources = manager.listResources(basePath, rl -> rl.getPath().endsWith(".json"));

        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {

            ResourceLocation fileLocation = entry.getKey();
            Resource resource = entry.getValue();

            // convert path -> quest id
            String path = fileLocation.getPath();
            path = path.substring(basePath.length() + 1, path.length() - ".json".length());

            ResourceLocation id = new ResourceLocation(fileLocation.getNamespace(), path);

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

