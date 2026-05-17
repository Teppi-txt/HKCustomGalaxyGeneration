package interface_adapters;
import java.util.ArrayList;

import entities.ObtainOption;

public interface IObtainable {
    public ArrayList<ObtainOption> getDependencies();
    public String getName();
}