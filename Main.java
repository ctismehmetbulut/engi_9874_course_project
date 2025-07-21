import javax.swing.SwingUtilities;

import dao.MemoryStore;

public class Main {
    public static void main(String[] args) {
        MemoryStore.initializeAll();
        SwingUtilities.invokeLater(MainAppFrame::new);
    }
}
