package utilities;

import entities.*;
import interface_adapters.IObtainable;
import entities.*;


import com.google.gson.*;
import java.io.FileReader;
import java.util.*;

public class GoalParser {

    public static ArrayList<IObtainable> parseGoals(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(reader).getAsJsonObject();
            JsonArray jsonGoals = object.getAsJsonArray("goals");

            Map<String, IObtainable> byName = new HashMap<>();
            ArrayList<IObtainable> result = new ArrayList<>();

            // First pass: create empty objects
            for (JsonElement element : jsonGoals) {
                JsonObject obj = element.getAsJsonObject();

                String name = obj.get("name").getAsString();
                String type = obj.get("type").getAsString();

                IObtainable goal;

                if (type.equals("CollectionGoal")) {
                    goal = new CollectionGoal(
                            name,
                            new PlayerStateEffect(),
                            new ArrayList<>(),
                            0
                    );
                } else if (type.equals("Objective")) {
                    goal = new Objective(
                            name,
                            new ArrayList<>(),
                            new PlayerStateEffect()
                    );
                } else {
                    goal = new AchievementGoal(
                            name,
                            new ArrayList<>(),
                            new PlayerStateEffect()
                    );
                }

                byName.put(name, goal);
                result.add(goal);
            }

            // Second pass: fill dependencies/effects
            for (JsonElement element : jsonGoals) {
                JsonObject obj = element.getAsJsonObject();

                String name = obj.get("name").getAsString();
                IObtainable goal = byName.get(name);

                ArrayList<ObtainOption> options = goal.getDependencies();

                if (!obj.has("options")) continue;

                for (JsonElement optionElement : obj.getAsJsonArray("options")) {
                    JsonObject optionObj = optionElement.getAsJsonObject();

                    HashSet<IObtainable> deps = new HashSet<>();

                    if (optionObj.has("dependencies")) {
                        for (JsonElement depElement : optionObj.getAsJsonArray("dependencies")) {
                            String depName = depElement.getAsString();

                            IObtainable dep = byName.get(depName);

                            if (dep == null) {
                                throw new RuntimeException("Unknown dependency: " + depName);
                            }

                            deps.add(dep);
                        }
                    }

                    PlayerStateEffect effect = parseEffect(optionObj.getAsJsonObject("effect"));

                    options.add(new ObtainOption(deps, effect));
                }
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse goals JSON", e);
        }
    }

    private static PlayerStateEffect parseEffect(JsonObject effectObj) {
        if (effectObj == null) {
            return new PlayerStateEffect();
        }

        int grubs = getInt(effectObj, "grubs_saved");
        int geo = getInt(effectObj, "geo");
        int essence = getInt(effectObj, "essence");

        return new PlayerStateEffect(grubs, Math.abs(geo), essence);
    }

    private static int getInt(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsInt();
        }
        return 0;
    }
}