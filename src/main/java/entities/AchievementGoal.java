package entities;
import interface_adapters.IObtainable;
import java.util.ArrayList;

public class AchievementGoal implements IObtainable{
    private String name;
    private ArrayList<ObtainOption> dependencies;
    private PlayerState obtainedAtState = null;
    
    public AchievementGoal(String name, ArrayList<ObtainOption> dependencies) {
        this.name = name;
        this.dependencies = dependencies;
    }

    public AchievementGoal(String name, ArrayList<ObtainOption> dependencies, PlayerState obtainedAtState) {
        this.name = name;
        this.dependencies = dependencies;
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
    public PlayerStateEffect getMinimalEffect(PlayerState state) {
        PlayerStateEffect effect = new PlayerStateEffect();

        if (dependencies.isEmpty()) {
            return effect;
        }

        for (ObtainOption option : dependencies) {
            if (state.hasItems(option.getDependencies())) {
                effect = PlayerStateEffect.getMinimal(option.getEffect(), effect);
            }
        }
        return effect;
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

}