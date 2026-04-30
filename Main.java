import java.util.ArrayList;
import java.util.List;

public class Main {
    Goal dreamNail = new Goal(
        "Dream Nail",
        new ArrayList<>(),
        new ArrayList<>()
    );

    Goal xero = new Goal(
        "Xero",
        new ArrayList<>(List.of(dreamNail)),
        new ArrayList<>()
    );

    Goal collect500Essence = new Goal(
        "Collect 500 essence",
        new ArrayList<>(List.of(dreamNail, xero)),
        new ArrayList<>()
    );

    Goal dreamWielder = new Goal(
        "Dream Wielder",
        new ArrayList<>(List.of(collect500Essence)),
        new ArrayList<>()
    );

    Goal desolateDive = new Goal(
        "Desolate Dive",
        new ArrayList<>(),
        new ArrayList<>()
    );

    Goal descendingDark = new Goal(
        "Descending Dark",
        new ArrayList<>(List.of(desolateDive)),
        new ArrayList<>()
    );
}
