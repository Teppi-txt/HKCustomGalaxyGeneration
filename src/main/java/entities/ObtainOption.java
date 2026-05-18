package entities;

import interface_adapters.Obtainable;
import java.util.HashSet;

public class ObtainOption {

    private GoalPool dependencies;
    private PlayerStateEffect effect;

    // Constructor
    public ObtainOption(HashSet<Obtainable> dependencies,
                        PlayerStateEffect effect) {
        this.dependencies = new GoalPool();
        for (Obtainable dependency : dependencies) {
            this.dependencies.add(dependency);
        }
        this.effect = effect;
    }

    // Getter for dependencies
    public GoalPool getDependencies() {
        return dependencies;
    }

    // Setter for dependencies
    public void setDependencies(HashSet<Obtainable> dependencies) {
        this.dependencies = new GoalPool();
        for (Obtainable dependency : dependencies) {
            this.dependencies.add(dependency);
        }
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
            for (Obtainable dep : dependencies.getElements()) {
                if (deps.length() > 0) {
                    deps.append(", ");
                }
                deps.append(dep.getName());
            }
        }

        return "[" + deps + "]";
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    public boolean requires(Obtainable dependency) {

        // direct dependency
        if (dependencies.contains(dependency)) {
            return true;
        }

        // recursive dependency
        for (Obtainable dep : dependencies.getElements()) {
            for (ObtainOption option : dep.getDependencies()) {
                if (option.requires(dependency)) {
                    return true;
                }
            }
        }

        return false;
    }
}