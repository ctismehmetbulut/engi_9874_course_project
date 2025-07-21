import javax.swing.*;

import dao.TransactionDAO;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HistoryFrame extends JFrame {
    private static HistoryFrame instance;
    private final int customerId;

    private HistoryFrame(int customerId) {
        super("Transaction History");
        this.customerId = customerId;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(createDepositsPanel());
        mainPanel.add(createPurchasesPanel());

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(_ -> this.dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(backBtn);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    public static HistoryFrame getInstance(int customerId) {
        if (instance == null || !instance.customerIdEquals(customerId)) {
            instance = new HistoryFrame(customerId);
        }
        return instance;
    }

    private boolean customerIdEquals(int id) {
        return this.customerId == id;
    }

    private JPanel createDepositsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Deposits"));

        try {
            ResultSet rs = TransactionDAO.getDepositsForCustomer(customerId);
            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                double amount = rs.getDouble("total_deposit");
                String date = rs.getString("deposit_date");

                JLabel label = new JLabel(String.format("→ %.2f CAD on %s", amount, date));
                panel.add(label);
            }

            if (!hasData) {
                panel.add(new JLabel("No deposit records."));
            }

        } catch (SQLException e) {
            panel.add(new JLabel("Error loading deposits."));
        }

        return panel;
    }

    private JPanel createPurchasesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Purchases"));

        try {
            ResultSet rs = TransactionDAO.getPurchasesForCustomer(customerId);
            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                String name = rs.getString("product_name");
                double price = rs.getDouble("product_price");
                String date = rs.getString("purchase_date");

                JLabel label = new JLabel(String.format("← %s (%.2f CAD) on %s", name, price, date));
                panel.add(label);
            }

            if (!hasData) {
                panel.add(new JLabel("No purchase records."));
            }

        } catch (SQLException e) {
            panel.add(new JLabel("Error loading purchases."));
        }

        return panel;
    }
}
