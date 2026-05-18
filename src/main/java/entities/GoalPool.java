package entities;

import interface_adapters.Obtainable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GoalPool {
    private ArrayList<Obtainable> poolElements;

    public GoalPool(ArrayList<Obtainable> goals) {
        poolElements = new ArrayList<>(goals);
    }

    public GoalPool() {
        poolElements = new ArrayList<>();
    }

    public static GoalPool poolFromStrings(List<Obtainable> elements, String[] majors) {
        GoalPool pool = new GoalPool();
        for (Obtainable element : elements) {
            for (String major : majors) {
                if (element.getName().equals(major)) {
                    pool.add(element);
                }
            }
        }
        return pool;
    }


    public ArrayList<Obtainable> getElements() {
        return poolElements;
    }

    public boolean contains(Obtainable element) {
        for (Obtainable o : poolElements) {
            if (o.equals(element)) return true;
        }
        return false;
    }

    public void retainAll(GoalPool g2) {
        Iterator<Obtainable> iterator = poolElements.iterator();

        while (iterator.hasNext()) {
            Obtainable o = iterator.next();
            if (!g2.contains(o)) {
                iterator.remove();
            }
        }
    }

    public boolean add(Obtainable element) {
        for (Obtainable o : poolElements) {
            if (o.equals(element)) return true;
        }
        poolElements.add(element);
        return true;
    }

    public boolean remove(Obtainable element) {
        Iterator<Obtainable> iterator = poolElements.iterator();

        while (iterator.hasNext()) {
            Obtainable o = iterator.next();
            if (o.equals(element)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public int size() {
        return poolElements.size();
    }

    public boolean isEmpty() {
        return poolElements.isEmpty();
    }

    public GoalPool intersection(GoalPool g2) {
        GoalPool intersection = new GoalPool();
        for (Obtainable o : poolElements) {
            if (g2.contains(o)) {
                intersection.add(o);
            }
        }
        return intersection;
    }
}
