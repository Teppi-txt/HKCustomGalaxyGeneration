package utilities;

import entities.*;
import interface_adapters.IObtainable;


import com.google.gson.*;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GoalParser {

    private static final Set<String> LANTERN_REQUIRED = new HashSet<>(Arrays.asList("No Eyes", "Peaks Access"));

    public static ArrayList<IObtainable> parseGoals(InputStream inputStream, SkipSettings settings) {
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);) {
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
                    int count = 0;
                    if (obj.has("count")) {
                        count = obj.get("count").getAsInt();
                    }
                    goal = new CollectionGoal(
                            name,
                            new ArrayList<>(),
                            count
                    );
                } else if (type.equals("Objective")) {
                    goal = new Objective(
                            name,
                            new ArrayList<>()
                    );
                } else if (type.equals("AchievementGoal")){
                    goal = new AchievementGoal(
                            name,
                            new ArrayList<>()
                    );
                } else if (type.equals("MilestoneGoal")) {
                    int count = 0;
                    if (obj.has("count")) {
                        count = obj.get("count").getAsInt();
                    }

                    String objective = null;
                    if (obj.has("objective")) {
                        objective = String.valueOf(obj.get("objective"));
                    }
                    goal = new MilestoneGoal(name, objective, count);
                } else {
                    goal = null;
                }

                byName.put(name, goal);
                result.add(goal);
            }

            // Second pass: fill dependencies/options
            for (JsonElement element : jsonGoals) {
                JsonObject obj = element.getAsJsonObject();
                String name = obj.get("name").getAsString();
                IObtainable goal = byName.get(name);

                if (!obj.has("options")) {
                    continue;
                }

                // CollectionGoal special handling
                if (goal instanceof CollectionGoal) {
                    for (JsonElement optionElement : obj.getAsJsonArray("options")) {
                        String dependencyName = optionElement.getAsString();
                        IObtainable dependency = byName.get(dependencyName);
                        if (dependency == null) {
                            throw new RuntimeException(
                                    "Unknown collection dependency: " + dependencyName
                            );
                        }
                        ((CollectionGoal) goal).getCollectionItems().add(dependency);
                    }
                    continue;
                }

                // MilestoneGoal special handling
                if (goal instanceof MilestoneGoal) {
                    continue;
                }

                // Normal ObtainOption handling
                ArrayList<ObtainOption> options = goal.getDependencies();

                for (JsonElement optionElement : obj.getAsJsonArray("options")) {
                    JsonObject optionObj = optionElement.getAsJsonObject();
                    HashSet<IObtainable> dependencies = new HashSet<>();

                    if (optionObj.has("notes")) {
                        String note = optionObj.get("notes").getAsString();
                        if (!settings.getSettings().contains(note)) {
                            continue; //dont process
                        }
                    }

                    if (optionObj.has("dependencies")) {
                        for (JsonElement depElement : optionObj.getAsJsonArray("dependencies")) {
                            String depName = depElement.getAsString();
                            IObtainable dependency = byName.get(depName);
                            if (dependency == null) {
                                throw new RuntimeException(
                                        "Unknown dependency: " + depName
                                );
                            }
                            dependencies.add(dependency);
                        }
                    }
                    PlayerStateEffect effect = parseEffect(optionObj.getAsJsonObject("effect"));
                    options.add(new ObtainOption(dependencies, effect));
                }
            }
            if (settings.hasDarkrooms()) {
                System.out.println("darkrooms on");
                removeLantern(GoalUtility.getGoalByName(result, "Lumafly Lantern"),
                                                    result);
            }
            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse goals JSON", e);
        }
    }

    private static void removeLantern(IObtainable lumaflyLantern, ArrayList<IObtainable> result) {
        for (IObtainable l : result) {
            if (LANTERN_REQUIRED.contains(l.getName())) {
                continue;
            }

            for (ObtainOption o : l.getDependencies()) {
                o.getDependencies().remove(lumaflyLantern);
            }
        }
    }

    private static PlayerStateEffect parseEffect(JsonObject effectObj) {
        if (effectObj == null) {
            return new PlayerStateEffect();
        }

        int grubs = getInt(effectObj, "grubs_saved");
        int geo = getInt(effectObj, "geo");
        int essence = getInt(effectObj, "essence");
        int tolls = getInt(effectObj, "tolls");

        return new PlayerStateEffect(grubs, Math.abs(geo), essence, tolls);
    }

    private static int getInt(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsInt();
        }
        return 0;
    }
}