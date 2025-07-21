import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {
    public MainMenuPanel(MainAppFrame app) {
        setLayout(new GridLayout(2, 1, 10, 10));

        JButton customer = new JButton("Customer Login");
        JButton merchant = new JButton("Merchant Login");

        customer.addActionListener(_ -> app.showView("auth_customer"));
        merchant.addActionListener(_ -> app.showView("auth_merchant"));

        add(customer);
        add(merchant);
    }
}
