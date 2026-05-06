package entities;

import interface_adapters.IObtainable;
import java.util.HashSet;

public class ObtainOption {

    private HashSet<IObtainable> dependencies;
    private PlayerStateEffect effect;

    // Constructor
    public ObtainOption(HashSet<IObtainable> dependencies,
                        PlayerStateEffect effect) {
        this.dependencies = dependencies;
        this.effect = effect;
    }

    // Getter for dependencies
    public HashSet<IObtainable> getDependencies() {
        return dependencies;
    }

    // Setter for dependencies
    public void setDependencies(HashSet<IObtainable> dependencies) {
        this.dependencies = dependencies;
    }

    // Getter for effect
    public PlayerStateEffect getEffect() {
        return effect;
    }

    // Setter for effect
    public void setEffect(PlayerStateEffect effect) {
        this.effect = effect;
    }

    @Override
    public String toString() {
        StringBuilder deps = new StringBuilder();

        if (dependencies != null) {
            for (IObtainable dep : dependencies) {
                if (deps.length() > 0) {
                    deps.append(", ");
                }
                deps.append(dep.getName());
            }
        }

        return "ObtainOption{" +
                "dependencies=[" + deps + "]" +
                ", effect=" + effect +
                '}';
    }
}