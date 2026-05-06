package entities;
import interface_adapters.IObtainable;
import java.util.ArrayList;
import java.util.HashSet;

public class Objective implements IObtainable{

    private String name;
    private ArrayList<ObtainOption> dependencies;
    private PlayerStateEffect effects;
    
    public Objective(String name, ArrayList<ObtainOption> dependencies, PlayerStateEffect effects) {
        this.name = name;
        this.dependencies = dependencies;
        this.effects = effects;
    }

    public Objective(String name, HashSet<IObtainable> dependencies, PlayerStateEffect effects) {
        this.name = name;
        this.dependencies = new ArrayList<>();
        this.dependencies.add(new ObtainOption(dependencies, effects));
        this.effects = effects;
    }


    @Override
    public boolean canObtain(PlayerState state) {
        if (dependencies.isEmpty()) {
            return true;
        }

        for (ObtainOption deps : dependencies) {
            if (state.hasItems(deps.getDependencies())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ObtainOption getSuccessfulOption(PlayerState state) {
        if (dependencies.isEmpty()) {
            return null;
        }

        for (ObtainOption option : dependencies) {
            if (state.hasItems(option.getDependencies())) {
                return option;
            }
        }
        return null;
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
    public ArrayList<ObtainOption> getDependencies() {
        return this.dependencies;
    }


    @Override
    public PlayerStateEffect getEffects() {
        return this.effects;
    }
}
