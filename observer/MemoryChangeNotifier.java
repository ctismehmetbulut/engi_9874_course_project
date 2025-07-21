package observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MemoryChangeNotifier {
    private static final List<MemoryChangeListener> listeners = new CopyOnWriteArrayList<>();

    public static void registerListener(MemoryChangeListener listener) {
        listeners.add(listener);
    }

    public static void unregisterListener(MemoryChangeListener listener) {
        listeners.remove(listener);
    }

    public static void notifyMemoryChanged() {
        for (MemoryChangeListener listener : listeners) {
            listener.onMemoryChanged();
        }
    }
}
