package entities;
import interface_adapters.Obtainable;
import java.util.ArrayList;

public class AchievementGoal extends Obtainable {
    private String name;
    private ArrayList<ObtainOption> dependencies;
    
    public AchievementGoal(String name, ArrayList<ObtainOption> dependencies) {
        this.name = name;
        this.dependencies = dependencies;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ArrayList<ObtainOption> getDependencies() {
        return this.dependencies;
    }

}