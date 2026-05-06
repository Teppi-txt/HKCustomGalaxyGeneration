package src.interface_adapters;
import src.entities.PlayerState;
import src.entities.PlayerStateEffect;
import java.util.ArrayList;
import java.util.HashSet;
import src.entities.ObtainOption;

public interface IObtainable {
    public boolean canObtain(PlayerState state);
    public ArrayList<ObtainOption> getDependencies();
    public String getName();
    public PlayerStateEffect getEffects();
    public boolean isObtained(PlayerState ps);
}