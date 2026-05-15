import entities.*;
import utilities.*;
import interface_adapters.IObtainable;
import view.GeneratorUI;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Main {
    public static void main(String[] args) {
        InputStream inputStream = Main.class
                .getClassLoader()
                .getResourceAsStream("hollow_knight_goals.json");

        if (inputStream == null) {
            throw new RuntimeException("Resource not found");
        }

        GeneratorUI.main(args);
    }
}

