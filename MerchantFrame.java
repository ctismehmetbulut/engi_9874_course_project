import javax.swing.*;

import dao.CampaignDAO;
import dao.MemoryStore;
import dao.ProductDAO;
import model.Campaign;
import model.Product;
import observer.MemoryChangeNotifier;
import observer.MemoryChangeListener;

import java.awt.*;

public class MerchantFrame extends JFrame implements MemoryChangeListener {
    private static MerchantFrame instance;

    private MerchantFrame() {
        super("Merchant Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        MemoryChangeNotifier.registerListener(this);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent e) {
                MemoryChangeNotifier.unregisterListener(MerchantFrame.this);
            }
        });

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        contentPanel.add(createCampaignAdder());
        contentPanel.add(createCampaignList());
        contentPanel.add(createProductAdder());
        contentPanel.add(createProductList());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void onMemoryChanged() {
        SwingUtilities.invokeLater(() -> refreshAll(false));
    }

    public static MerchantFrame getInstance() {
        if (instance == null) {
            instance = new MerchantFrame();
        } else {
            instance.setExtendedState(JFrame.NORMAL);
            instance.toFront();
            instance.requestFocus();
        }
        instance.refreshAll(false);
        return instance;
    }

    private void refreshAll(boolean broadcastChange) {
        if (broadcastChange) {
        MemoryStore.initializeAll();
        MemoryChangeNotifier.notifyMemoryChanged();
    }
        getContentPane().removeAll();
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(createCampaignAdder());
        contentPanel.add(createCampaignList());
        contentPanel.add(createProductAdder());
        contentPanel.add(createProductList());
        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createCampaignAdder() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Add Campaign"));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField depositField = new JTextField(5);
        JTextField bonusField = new JTextField(5);
        inputPanel.add(new JLabel("Deposit:"));
        inputPanel.add(depositField);
        inputPanel.add(new JLabel("Bonus:"));
        inputPanel.add(bonusField);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        JButton addBtn = new JButton("Add Campaign");
        addBtn.addActionListener(_ -> {
            try {
                double deposit = Double.parseDouble(depositField.getText());
                double bonus = Double.parseDouble(bonusField.getText());
                if (deposit <= 0 || bonus < 0)
                    throw new NumberFormatException();
                CampaignDAO.insertCampaign(deposit, bonus);
                refreshAll(true);
                depositField.setText("");
                bonusField.setText("");
                errorLabel.setText(" ");
            } catch (NumberFormatException ex) {
                errorLabel.setText("Deposit and bonus must be positive numbers.");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addBtn);

        panel.add(inputPanel);
        panel.add(buttonPanel);
        panel.add(errorLabel);

        return panel;
    }

    private JPanel createCampaignList() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Campaigns"));

        JLabel emptyLabel = new JLabel("No Campaigns Listed");
        emptyLabel.setForeground(Color.GRAY);
        emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(emptyLabel);

        boolean hasData = false;

        for (Campaign campaign : MemoryStore.campaigns.values()) {
            hasData = true;
            int id = campaign.getId();
            double deposit = campaign.getDepositAmount();
            double bonus = campaign.getBonusAmount();
            double total = deposit + bonus;

            JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel label = new JLabel(
                    String.format("ID %03d: Deposit %.2f CAD → Get %.2f CAD", id, deposit, total));

            JButton remove = new JButton("Remove");
            remove.addActionListener(_ -> {
                CampaignDAO.deleteCampaign(id);
                refreshAll(true);
            });

            itemPanel.add(label);
            itemPanel.add(remove);
            panel.add(itemPanel);
        }

        emptyLabel.setVisible(!hasData);
        return panel;
    }

    private JPanel createProductAdder() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Add Product"));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField nameField = new JTextField(10);
        JTextField priceField = new JTextField(5);
        JTextField stockField = new JTextField(3);
        inputPanel.add(new JLabel("Product Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Stock:"));
        inputPanel.add(stockField);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        JButton addBtn = new JButton("Add Product");
        addBtn.addActionListener(_ -> {
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText());
                int stock = stockField.getText().isEmpty() ? 1 : Integer.parseInt(stockField.getText());
                if (name.isEmpty() || price <= 0 || stock <= 0)
                    throw new IllegalArgumentException();
                ProductDAO.insertProduct(name, price, stock);
                refreshAll(true);
                nameField.setText("");
                priceField.setText("");
                stockField.setText("");
                errorLabel.setText(" ");
            } catch (Exception ex) {
                errorLabel.setText("Enter valid name, positive price and stock.");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addBtn);

        panel.add(inputPanel);
        panel.add(buttonPanel);
        panel.add(errorLabel);

        return panel;
    }

    private JPanel createProductList() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Products"));

        JLabel emptyLabel = new JLabel("No Products Listed");
        emptyLabel.setForeground(Color.GRAY);
        emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(emptyLabel);

        boolean hasData = false;

        for (Product product : MemoryStore.products.values()) {
            hasData = true;
            int id = product.getId();
            String name = product.getName();
            double price = product.getPrice();
            int stock = product.getStock();

            JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel label = new JLabel(String.format("ID %04d: %s - %.2f CAD (Stock: %d)", id, name, price, stock));

            JButton plus = new JButton("+");
            JButton minus = new JButton("-");

            plus.addActionListener(_ -> {
                ProductDAO.updateProductStock(id, 1);
                refreshAll(true);
            });

            minus.addActionListener(_ -> {
                ProductDAO.updateProductStock(id, -1);
                refreshAll(true);
            });

            itemPanel.add(label);
            itemPanel.add(plus);
            itemPanel.add(minus);
            panel.add(itemPanel);
        }

        emptyLabel.setVisible(!hasData);
        return panel;
    }

}