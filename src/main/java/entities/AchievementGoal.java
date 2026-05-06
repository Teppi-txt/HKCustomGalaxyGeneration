package src.entities;
import interface_adapters.IObtainable;
import java.util.ArrayList;
import java.util.HashSet;
    
public class AchievementGoal implements IObtainable{
    private String name;
    private ArrayList<ObtainOption> dependencies;
    private PlayerStateEffect effects;
    private PlayerState obtainedAtState = null;
    
    public AchievementGoal(String name, ArrayList<ObtainOption> dependencies, PlayerStateEffect effects) {
        this.name = name;
        this.dependencies = dependencies;
        this.effects = effects;
    }

    public AchievementGoal(String name, ArrayList<ObtainOption> dependencies, PlayerStateEffect effects, PlayerState obtainedAtState) {
        this.name = name;
        this.dependencies = dependencies;
        this.effects = effects;
        this.obtainedAtState = obtainedAtState;
    }

    @Override
    public boolean canObtain(PlayerState state) {
        if (dependencies.isEmpty()) {
            return true;
        }

        for (ObtainOption option : dependencies) {
            if (state.hasItems(option.getDependencies())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isObtained(PlayerState state) {
        if (obtainedAtState != null) {
            return state.isGreaterThan(obtainedAtState);
        }
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ArrayList<ObtainOption> getDependencies() {
        return this.dependencies;
    }

    // public ArrayList<CollectionGoal> getGoalDependencies() {
    //     ArrayList<CollectionGoal> goals = new ArrayList<>();
    //     for (IObtainable i : this.dependencies) {
    //         if (i instanceof CollectionGoal) {
    //             goals.add((CollectionGoal) i);
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