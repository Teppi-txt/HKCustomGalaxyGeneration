package entities;
import java.util.ArrayList;

import interface_adapters.IObtainable;
    
public class Goal implements IObtainable{
    private String name;
    private ArrayList<IObtainable> dependencies;
    private ArrayList<IObtainable> effects;
    
    public Goal(String name, ArrayList<IObtainable> dependencies, ArrayList<IObtainable> effects) {
        this.name = name;
        this.dependencies = dependencies;
        this.effects = effects;
    }

    public boolean canObtain(PlayerState state) {
        return true;
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

    public ArrayList<IObtainable> getEffects() {
        return this.effects;
    }

	public void addPrerequesite(IObtainable item) {
		this.dependencies.add(item);
	}
}