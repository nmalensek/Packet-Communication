package cs445.overlay.wireformats.eventfactory;

public class EventFactory {

    private static EventFactory instance = null;

    protected EventFactory() {

    }

    public static EventFactory getInstance() {
        if (instance == null) {
            instance = new EventFactory();
        }
        return instance;
    }
}
