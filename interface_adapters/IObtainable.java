package interface_adapters;
import entities.PlayerState;
import entities.PlayerStateEffect;
import java.util.ArrayList;

public interface IObtainable {
    public boolean canObtain(PlayerState state);
    public ArrayList<IObtainable> getDependencies();
    public String getName();
    public PlayerStateEffect getEffects();
    public boolean isObtained(PlayerState ps);
}