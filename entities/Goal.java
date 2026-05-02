package entities;
import interface_adapters.IObtainable;
import java.util.ArrayList;
    
public class Goal implements IObtainable{
    private String name;
    private ArrayList<IObtainable> dependencies;
    private PlayerStateEffect effects;
    private PlayerState obtainedAtState = null;
    
    public Goal(String name, ArrayList<IObtainable> dependencies, PlayerStateEffect effects) {
        this.name = name;
        this.dependencies = dependencies;
        this.effects = effects;
    }

    public Goal(String name, ArrayList<IObtainable> dependencies, PlayerStateEffect effects, PlayerState obtainedAtState) {
        this.name = name;
        this.dependencies = dependencies;
        this.effects = effects;
        this.obtainedAtState = obtainedAtState;
    }

    public boolean canObtain(PlayerState state) {
        return true;
    }

    public boolean isObtained(PlayerState state) {
        if (obtainedAtState != null) {
            return state.isGreaterThan(obtainedAtState);
        }
        return false;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<IObtainable> getDependencies() {
        return this.dependencies;
    }

    public ArrayList<Goal> getGoalDependencies() {
        ArrayList<Goal> goals = new ArrayList<>();
        for (IObtainable i : this.dependencies) {
            if (i instanceof Goal) {
                goals.add((Goal) i);
            }
        }
        return goals;
    }

    public PlayerStateEffect getEffects() {
        return this.effects;
    }

	public void addPrerequesite(IObtainable item) {
		this.dependencies.add(item);
	}
}