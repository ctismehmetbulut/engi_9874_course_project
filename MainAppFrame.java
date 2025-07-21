import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class MainAppFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final HashMap<String, JPanel> views = new HashMap<>();

    public MainAppFrame() {
        super("CardLayout App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Register cards
        views.put("menu", new MainMenuPanel(this));
        views.put("auth_customer", new AuthPanel(this, "customer"));
        views.put("auth_merchant", new AuthPanel(this, "merchant"));

        // Add all cards to panel
        for (String name : views.keySet()) {
            cardPanel.add(views.get(name), name);
        }

        add(cardPanel);
        setVisible(true);

        showView("menu");
    }

    public void showView(String name) {
        cardLayout.show(cardPanel, name);
    }
}
