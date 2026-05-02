import entities.Goal;
import entities.PlayerState;
import entities.PlayerStateEffect;
import interface_adapters.IObtainable;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void printGoals(List<IObtainable> goals) {
        for (IObtainable goal : goals) {
            System.out.println(goal.getName());
        }
    }

    public static void main(String[] args) {
        ArrayList<IObtainable> goals = constructTestGraph1();
        
        TopologicalSort topologicalSort = new TopologicalSort();
        printGoals(topologicalSort.sort_and_reduce(goals)); //null if cycle detected
    }

    private static ArrayList<IObtainable> constructTestGraph1() {
        Goal mothwingCloak = new Goal(
            "Mothwing Cloak",
            new ArrayList<>(),
            new PlayerStateEffect()
        );

        Goal dreamNail = new Goal(
            "Dream Nail",
            new ArrayList<>(List.of(mothwingCloak)),
            new PlayerStateEffect()
        );

        Goal xero = new Goal(
            "Xero",
            new ArrayList<>(List.of(dreamNail)),
            new PlayerStateEffect()
        );

        Goal collect500Essence = new Goal(
            "Collect 500 essence",
            new ArrayList<>(List.of(dreamNail, xero)),
            new PlayerStateEffect()
        );

        Goal dreamWielder = new Goal(
            "Dream Wielder",
            new ArrayList<>(List.of(collect500Essence)),
            new PlayerStateEffect()
        );

        Goal desolateDive = new Goal(
            "Desolate Dive",
            new ArrayList<>(),
            new PlayerStateEffect()
        );

        Goal descendingDark = new Goal(
            "Descending Dark",
            new ArrayList<>(List.of(desolateDive)),
            new PlayerStateEffect()
        );

        Goal mantisClaw = new Goal(
            "Mantis Claw",
            new ArrayList<>(List.of(mothwingCloak)),
            new PlayerStateEffect()
        );

        Goal crystalHeart = new Goal(
            "Crystal Heart",
            new ArrayList<>(List.of(mantisClaw)),
            new PlayerStateEffect()
        );

        Goal monarchWings = new Goal(
            "Monarch Wings",
            new ArrayList<>(List.of(crystalHeart)),
            new PlayerStateEffect()
        );

        Goal cityCrest = new Goal(
            "City Crest",
            new ArrayList<>(),
            new PlayerStateEffect()
        );

        Goal enterCity = new Goal(
            "Enter City of Tears",
            new ArrayList<>(List.of(cityCrest)),
            new PlayerStateEffect()
        );

        Goal soulMaster = new Goal(
            "Soul Master",
            new ArrayList<>(List.of(enterCity)),
            new PlayerStateEffect()
        );

        
        Goal shadeSoul = new Goal(
            "Shade Soul",
            new ArrayList<>(),
            new PlayerStateEffect()
        );

        Goal abyssShriek = new Goal(
            "Abyss Shriek",
            new ArrayList<>(List.of(shadeSoul)),
            new PlayerStateEffect()
        );

        Goal spend3K = new Goal(
            "Spend 3k geo",
            new ArrayList<>(),
            new PlayerStateEffect(),
            new PlayerState(3000, 0, 0, 0, 0, 0)
        );

        Goal fountainVesselFragment = new Goal(
            "Fountain Vessel",
            new ArrayList<>(),
            new PlayerStateEffect(0, 3000, 0, 0, 0)
        );

        Goal greengalGrubs = new Goal(
            "Greenpath Fungal Gubs",
            new ArrayList<>(),
            new PlayerStateEffect(6, 0, 0, 0, 0)
        );

        Goal crossroadsCanyonGrubs = new Goal(
            "Cross Canyon Gubs",
            new ArrayList<>(List.of(crystalHeart)),
            new PlayerStateEffect(5, 0, 0, 0, 0)
        );

        Goal fiveGrubs = new Goal(
            "Five Grubs",
            new ArrayList<>(),
            new PlayerStateEffect(0, 0, 0, 0, 0),
            new PlayerState(0, 0, 5, 0, 0, 0)
        );

        ArrayList<IObtainable> goals = new ArrayList<>(List.of(
            dreamNail,
            xero,
            collect500Essence,
            dreamWielder,
            desolateDive,
            descendingDark,
            mothwingCloak,
            mantisClaw,
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
            fiveGrubs
        ));

        return goals;
    }
}

