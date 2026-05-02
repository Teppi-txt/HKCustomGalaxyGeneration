import entities.Goal;
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
        

        ArrayList<Goal> goals = constructTestGraph1();
        
        TopologicalSort topologicalSort = new TopologicalSort();
        printGoals(topologicalSort.sort_and_reduce(goals)); //null if cycle detected
    }

    private static ArrayList<Goal> constructTestGraph1() {
        Goal mothwingCloak = new Goal(
            "Mothwing Cloak",
            new ArrayList<>(),
            new ArrayList<>()
        );

        Goal dreamNail = new Goal(
            "Dream Nail",
            new ArrayList<>(List.of(mothwingCloak)),
            new ArrayList<>()
        );

        Goal xero = new Goal(
            "Xero",
            new ArrayList<>(List.of(dreamNail)),
            new ArrayList<>()
        );

        Goal collect500Essence = new Goal(
            "Collect 500 essence",
            new ArrayList<>(List.of(dreamNail, xero)),
            new ArrayList<>()
        );

        Goal dreamWielder = new Goal(
            "Dream Wielder",
            new ArrayList<>(List.of(collect500Essence)),
            new ArrayList<>()
        );

        Goal desolateDive = new Goal(
            "Desolate Dive",
            new ArrayList<>(),
            new ArrayList<>()
        );

        Goal descendingDark = new Goal(
            "Descending Dark",
            new ArrayList<>(List.of(desolateDive)),
            new ArrayList<>()
        );

        Goal mantisClaw = new Goal(
            "Mantis Claw",
            new ArrayList<>(List.of(mothwingCloak)),
            new ArrayList<>()
        );

        Goal crystalHeart = new Goal(
            "Crystal Heart",
            new ArrayList<>(List.of(mantisClaw)),
            new ArrayList<>()
        );

        Goal monarchWings = new Goal(
            "Monarch Wings",
            new ArrayList<>(List.of(crystalHeart)),
            new ArrayList<>()
        );

        Goal cityCrest = new Goal(
            "City Crest",
            new ArrayList<>(),
            new ArrayList<>()
        );

        Goal enterCity = new Goal(
            "Enter City of Tears",
            new ArrayList<>(List.of(cityCrest)),
            new ArrayList<>()
        );

        Goal soulMaster = new Goal(
            "Soul Master",
            new ArrayList<>(List.of(enterCity)),
            new ArrayList<>()
        );

        Goal shadeSoul = new Goal(
            "Shade Soul",
            new ArrayList<>(List.of(soulMaster)),
            new ArrayList<>()
        );

        Goal abyssShriek = new Goal(
            "Abyss Shriek",
            new ArrayList<>(List.of(shadeSoul)),
            new ArrayList<>()
        );

        ArrayList<Goal> goals = new ArrayList<>(List.of(
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
            abyssShriek
        ));

        return goals;
    }
}

