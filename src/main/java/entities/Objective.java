package entities;
import interface_adapters.IObtainable;
import java.util.ArrayList;
import java.util.List;

public class Objective implements IObtainable{

    private String name;
    private ArrayList<ObtainOption> dependencies;
    public Objective(String name, List<ObtainOption> dependencies) {
        this.name = name;
        this.dependencies = (ArrayList<ObtainOption>) dependencies;
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
