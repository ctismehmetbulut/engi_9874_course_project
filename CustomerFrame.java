import javax.swing.*;

import dao.CustomerDAO;
import dao.MemoryStore;
import dao.PurchaseResult;
import dao.TransactionDAO;
import model.Campaign;
import model.Product;
import observer.MemoryChangeListener;
import observer.MemoryChangeNotifier;

import java.awt.*;

public class CustomerFrame extends JFrame implements MemoryChangeListener {
    private static CustomerFrame instance;
    private static int customerId; // <-- store current customer ID

    private final JLabel balanceLabel = new JLabel("Balance: Loading...");

    private CustomerFrame(int id) {
        super("Customer Dashboard");
        customerId = id;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent e) {
                MemoryChangeNotifier.unregisterListener(CustomerFrame.this);
            }
        });

        setSize(400, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Create a horizontal panel for balance + history button
        JPanel balancePanel = new JPanel();
        balancePanel.setLayout(new BoxLayout(balancePanel, BoxLayout.X_AXIS));
        balancePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton historyBtn = new JButton("History");
        historyBtn.addActionListener(_ -> HistoryFrame.getInstance(customerId).setVisible(true));

        balancePanel.add(balanceLabel);
        balancePanel.add(Box.createHorizontalGlue());
        balancePanel.add(historyBtn);

        // This ensures proper layout behavior inside BoxLayout
        balancePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        contentPanel.add(balancePanel);
        contentPanel.add(createCampaignList());
        contentPanel.add(createProductList());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);

        loadBalance();

        MemoryChangeNotifier.registerListener(this);
    }

    @Override
    public void onMemoryChanged() {
        SwingUtilities.invokeLater(this::refreshAll);
    }

    public static CustomerFrame getInstance(int id) {
        if (instance == null) {
            instance = new CustomerFrame(id);
        } else {
            instance.setExtendedState(JFrame.NORMAL);
            instance.toFront();
            instance.requestFocus();
        }
        customerId = id;
        instance.refreshAll();
        return instance;
    }

    private void refreshAll() {
        getContentPane().removeAll();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Rebuild balance panel
        JPanel balancePanel = new JPanel();
        balancePanel.setLayout(new BoxLayout(balancePanel, BoxLayout.X_AXIS));
        balancePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        balanceLabel.setText("Balance: Loading...");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton historyBtn = new JButton("History");
        historyBtn.addActionListener(_ -> HistoryFrame.getInstance(customerId).setVisible(true));

        balancePanel.add(balanceLabel);
        balancePanel.add(Box.createHorizontalGlue());
        balancePanel.add(historyBtn);
        balancePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        contentPanel.add(balancePanel);
        contentPanel.add(createCampaignList());
        contentPanel.add(createProductList());

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        revalidate();
        repaint();
        loadBalance();
    }

    private void loadBalance() {
        try {
            double balance = CustomerDAO.getCustomerBalance(customerId);
            balanceLabel.setText(String.format("Balance: %.2f CAD", balance));
        } catch (Exception e) {
            balanceLabel.setText("Balance: Error loading");
            JOptionPane.showMessageDialog(this, "Failed to load balance: " + e.getMessage());
        }
    }

    private JPanel createCampaignList() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Campaigns"));

        for (Campaign campaign : MemoryStore.campaigns.values()) {
            int id = campaign.getId();
            double deposit = campaign.getDepositAmount();
            double bonus = campaign.getBonusAmount();
            double total = deposit + bonus;

            String text = String.format("ID %03d: Deposit %.2f → Get %.2f CAD", id, deposit, total);
            JButton depositBtn = new JButton("Deposit");

            JPanel row = new JPanel(new BorderLayout());
            row.add(new JLabel(text), BorderLayout.CENTER);
            row.add(depositBtn, BorderLayout.EAST);

            depositBtn.addActionListener(_ -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                        String.format("Deposit %.2f and get %.2f?", deposit, total),
                        "Confirm Deposit",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = TransactionDAO.depositToCustomer(total, customerId);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Deposit successful.");
                        loadBalance();

                        // ✅ Broadcast memory change to notify MerchantFrame
                        MemoryStore.initializeAll();
                        MemoryChangeNotifier.notifyMemoryChanged();
                    } else {
                        JOptionPane.showMessageDialog(this, "Deposit failed.");
                    }
                }
            });

            panel.add(row);
        }

        return panel;
    }

    private JPanel createProductList() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Products"));

        for (Product product : MemoryStore.products.values()) {
            int id = product.getId();
            String name = product.getName();
            double price = product.getPrice();
            int stock = product.getStock();

            String text = String.format("ID %04d: %s - %.2f CAD (Stock: %d)", id, name, price, stock);
            JButton buyBtn = new JButton("Purchase");

            JPanel row = new JPanel(new BorderLayout());
            row.add(new JLabel(text), BorderLayout.CENTER);
            row.add(buyBtn, BorderLayout.EAST);

            buyBtn.addActionListener(_ -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                        String.format("Purchase %s for %.2f CAD?", name, price),
                        "Confirm Purchase",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    PurchaseResult result = TransactionDAO.purchaseProduct(name, price, customerId);
                    switch (result) {
                        case SUCCESS:
                            JOptionPane.showMessageDialog(this, "Purchase successful.");
                            loadBalance();

                            // ✅ Broadcast memory change to notify MerchantFrame
                            MemoryStore.initializeAll();
                            MemoryChangeNotifier.notifyMemoryChanged();

                            refreshAll(); // <-- refreshes product list with updated stock
                            break;
                        case INSUFFICIENT_BALANCE:
                            JOptionPane.showMessageDialog(this, "Insufficient balance.");
                            break;
                        case CUSTOMER_NOT_FOUND:
                            JOptionPane.showMessageDialog(this, "Customer not found.");
                            break;
                        case OUT_OF_STOCK:
                            JOptionPane.showMessageDialog(this, "Product is out of stock.");
                            break;
                        case PRODUCT_NOT_FOUND:
                            JOptionPane.showMessageDialog(this, "Product not found.");
                            break;
                        default:
                            JOptionPane.showMessageDialog(this, "Purchase failed.");
                    }
                }
            });

            panel.add(row);
        }

        return panel;
    }

}
