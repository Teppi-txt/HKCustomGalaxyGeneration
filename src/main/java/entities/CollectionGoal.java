package src.entities;
import interface_adapters.IObtainable;
import java.util.ArrayList;
import java.util.HashSet;
    
public class CollectionGoal implements IObtainable{
    private String name;
    private PlayerStateEffect effects;
    private ArrayList<Objective> collectionItems;
    private int collectionCount;
    
    public CollectionGoal(String name, PlayerStateEffect effects, ArrayList<Objective> items, int count) {
        this.name = name;
        this.effects = effects;
        this.collectionItems = items;
        this.collectionCount = count;
    }

    @Override
    public boolean canObtain(PlayerState state) {
        return false;
    }

    @Override
    public boolean isObtained(PlayerState state) {
        int count = 0;
        for (Objective objective : state.getObjectives()) {
            if (collectionItems.contains(objective)) {
                count += 1;
            }
        }
        return count >= collectionCount;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ArrayList<ObtainOption> getDependencies() {
        return new ArrayList<>();
    }

    // public ArrayList<AchievementGoal> getGoalDependencies() {
    //     ArrayList<AchievementGoal> goals = new ArrayList<>();
    //     for (IObtainable i : this.dependencies) {
    //         if (i instanceof AchievementGoal achievementGoal) {
    //             goals.add(achievementGoal);
    //         }
    //     }
    //     return goals;
    // }

    @Override
    public PlayerStateEffect getEffects() {
        return this.effects;
    }

	// public void addPrerequesite(IObtainable item) {
	// 	this.dependencies.add(item);
	// }
}