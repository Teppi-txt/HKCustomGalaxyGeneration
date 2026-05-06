import entities.AchievementGoal;
import entities.CollectionGoal;
import entities.Objective;
import entities.ObtainOption;
import entities.PlayerState;
import entities.PlayerStateEffect;
import interface_adapters.IObtainable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Main {

    public static void printGoals(List<IObtainable> goals) {
        for (IObtainable goal : goals) {
            if (goal instanceof Objective g) {
                System.out.println(g.getName());
            } else {
                System.out.println("\u001B[32m" + goal.getName() + "\u001B[0m");
            }
            
        }
    }

    public static List<IObtainable> selectRandomGoals(List<IObtainable> allItems, int count) {

        // Store indices of only non-Objectives
        List<Integer> validIndices = new ArrayList<>();

        for (int i = 0; i < allItems.size(); i++) {
            if (!(allItems.get(i) instanceof Objective)) {
                validIndices.add(i);
            }
        }

        // Shuffle valid indices
        Collections.shuffle(validIndices);

        // Select first count
        Set<Integer> selected = new HashSet<>(
            validIndices.subList(0, Math.min(count, validIndices.size()))
        );

        // Rebuild in original order
        List<IObtainable> result = new ArrayList<>();

        for (int i = 0; i < allItems.size(); i++) {
            if (selected.contains(i)) {
                result.add(allItems.get(i));
            }
        }

        return result;
    }

    public static void main(String[] args) {
        ArrayList<IObtainable> goals = constructTestGraph1();
        
        TopologicalSort topologicalSort = new TopologicalSort();
        printGoals(selectRandomGoals(topologicalSort.sort_and_reduce(goals), 6)); //null if cycle detected
    }

    private static ArrayList<IObtainable> constructTestGraph1() {
        AchievementGoal lumaflyLantern = new AchievementGoal(
            "Lumafly Lantern",
            new ArrayList<>(List.of()),
            PlayerStateEffect.spend_geo(1800)
        );

        AchievementGoal dreamNail = new AchievementGoal(
            "Dream Nail",
            new ArrayList<>(List.of()),
            new PlayerStateEffect()
        );

        AchievementGoal xero = new AchievementGoal(
            "Xero",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(dreamNail)),
                    new PlayerStateEffect()
                )
            )),
            new PlayerStateEffect()
        );

        AchievementGoal collect500Essence = new AchievementGoal(
            "Collect 500 essence",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(dreamNail)),
                    new PlayerStateEffect()
                )
            )),
            new PlayerStateEffect()
        );

        AchievementGoal dreamWielder = new AchievementGoal(
            "Dream Wielder",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(collect500Essence)),
                    new PlayerStateEffect()
                )
            )),
            new PlayerStateEffect()
        );

        AchievementGoal cityCrest = new AchievementGoal(
            "City Crest",
            new ArrayList<>(List.of()),
            new PlayerStateEffect()
        );

        AchievementGoal enterCity = new AchievementGoal(
            "Enter City of Tears",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(cityCrest)),
                    new PlayerStateEffect()
                )
            )),
            new PlayerStateEffect()
        );

        AchievementGoal soulMaster = new AchievementGoal(
            "Soul Master",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(enterCity)),
                    new PlayerStateEffect()
                )
            )),
            new PlayerStateEffect()
        );

        AchievementGoal desolateDive = new AchievementGoal(
            "Desolate Dive",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(soulMaster)),
                    new PlayerStateEffect()
                )
            )),
            new PlayerStateEffect()
        );

        AchievementGoal descendingDark = new AchievementGoal(
            "Descending Dark",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(desolateDive)),
                    new PlayerStateEffect()
                )
            )),
            new PlayerStateEffect()
        );

        AchievementGoal crystalHeart = new AchievementGoal(
            "Crystal Heart",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(enterCity)),
                    new PlayerStateEffect()
                )
            )),
            new PlayerStateEffect()
        );

        AchievementGoal monarchWings = new AchievementGoal(
            "Monarch Wings",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(crystalHeart)),
                    new PlayerStateEffect()
                )
            )),
            new PlayerStateEffect()
        );

        AchievementGoal shadeSoul = new AchievementGoal(
            "Shade Soul",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(enterCity)),
                    new PlayerStateEffect()
                )
            )),
            new PlayerStateEffect()
        );

        AchievementGoal abyssShriek = new AchievementGoal(
            "Abyss Shriek",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(shadeSoul)),
                    new PlayerStateEffect()
                )
            )),
            new PlayerStateEffect()
        );

        AchievementGoal spend3K = new AchievementGoal(
            "Spend 3k geo",
            new ArrayList<>(List.of()),
            new PlayerStateEffect(),
            new PlayerState(3000, 0, 0)
        );

        AchievementGoal fountainVesselFragment = new AchievementGoal(
            "Fountain Vessel",
            new ArrayList<>(List.of()),
            new PlayerStateEffect(0, 3000, 0)
        );

        AchievementGoal greengalGrubs = new AchievementGoal(
            "Greenpath Fungal Grubs",
            new ArrayList<>(List.of()),
            new PlayerStateEffect(6, 0, 0)
        );

        AchievementGoal crossroadsCanyonGrubs = new AchievementGoal(
            "Cross Canyon Grubs",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(crystalHeart)),
                    new PlayerStateEffect()
                )
            )),
            new PlayerStateEffect(5, 0, 0)
        );

        AchievementGoal fiveGrubs = new AchievementGoal(
            "Five Grubs",
            new ArrayList<>(List.of()),
            new PlayerStateEffect(0, 0, 0),
            new PlayerState(0, 0, 5)
        );

        Objective slyMask1 = new Objective(
            "Sly Mask 1",
            new ArrayList<>(List.of()),
            PlayerStateEffect.spend_geo(120)
        );

        Objective slyMask2 = new Objective(
            "Sly Mask 2",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(slyMask1)),
                    PlayerStateEffect.spend_geo(500)
                )
            )),
            PlayerStateEffect.spend_geo(500)
        );

        Objective slyMask3 = new Objective(
            "Sly Mask 3",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(slyMask2)),
                    PlayerStateEffect.spend_geo(1200)
                )
            )),
            PlayerStateEffect.spend_geo(1200)
        );

        Objective slyMask4 = new Objective(
            "Sly Mask 4",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(slyMask3)),
                    PlayerStateEffect.spend_geo(2000)
                )
            )),
            PlayerStateEffect.spend_geo(2000)
        );

        Objective peaksAccess = new Objective(
            "Peaks Access",
            new ArrayList<>(List.of(
                new ObtainOption(
                    new HashSet<>(List.of(lumaflyLantern)),
                    new PlayerStateEffect()
                ),
                new ObtainOption(
                    new HashSet<>(List.of(desolateDive)),
                    new PlayerStateEffect()
                )
            )),
            new PlayerStateEffect()
        );

        CollectionGoal oneMask = new CollectionGoal(
            "Obtain one extra mask",
            null,
            new ArrayList<>(List.of(slyMask1, slyMask2, slyMask3, slyMask4)),
            4
        );


        ArrayList<IObtainable> goals = new ArrayList<>(List.of(
            dreamNail,
            xero,
            collect500Essence,
            dreamWielder,
            desolateDive,
            descendingDark,
            crystalHeart,
            monarchWings,
            cityCrest,
            enterCity,
            soulMaster,
            shadeSoul,
            abyssShriek,
            spend3K,
            fountainVesselFragment,
            greengalGrubs,
            crossroadsCanyonGrubs,
            fiveGrubs,
            slyMask1,
            slyMask2,
            slyMask3,
            slyMask4,
            oneMask,
            lumaflyLantern,
            peaksAccess
        ));

        return goals;
    }
}

