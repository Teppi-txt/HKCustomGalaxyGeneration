package entities;
import interface_adapters.IObtainable;
import java.util.ArrayList;
import java.util.HashSet;

public class Objective implements IObtainable{

    private String name;
    private ArrayList<HashSet<IObtainable>> dependencies;
    private PlayerStateEffect effects;
    
    public Objective(String name, ArrayList<HashSet<IObtainable>> dependencies, PlayerStateEffect effects) {
        this.name = name;
        this.dependencies = dependencies;
        this.effects = effects;
    }

    public Objective(String name, HashSet<IObtainable> dependencies, PlayerStateEffect effects) {
    this.name = name;
        this.dependencies = new ArrayList<>();
        this.dependencies.add(dependencies);
        this.effects = effects;
    }


    @Override
    public boolean canObtain(PlayerState state) {
        if (dependencies.isEmpty()) {
            return true;
        }

        for (HashSet<IObtainable> deps : dependencies) {
            if (state.hasItems(deps)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isObtained(PlayerState state) {
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ArrayList<HashSet<IObtainable>> getDependencies() {
        return this.dependencies;
    }

    public ArrayList<CollectionGoal> getGoalDependencies() {
        ArrayList<CollectionGoal> goals = new ArrayList<>();
        for (IObtainable i : this.dependencies.get(0)) {
            if (i instanceof CollectionGoal) {
                goals.add((CollectionGoal) i);
            }
        }
        return goals;
    }

    @Override
    public PlayerStateEffect getEffects() {
        return this.effects;
    }
}
