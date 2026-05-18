package interface_adapters;
import java.util.ArrayList;

import entities.ObtainOption;

public abstract class Obtainable {
    abstract public ArrayList<ObtainOption> getDependencies();
    abstract public String getName();

    @Override
    public boolean equals(Object o) {
        if (o instanceof Obtainable) {
            Obtainable ob = (Obtainable) o;
            return this.getName().equals(ob.getName());
        }
        return false;
    }
}