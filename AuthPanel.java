import javax.swing.*;

import dao.CustomerDAO;

import java.awt.*;

public class AuthPanel extends JPanel {
    private final String role;

    public AuthPanel(MainAppFrame app, String role) {
        this.role = role.toLowerCase();

        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(this.role.toUpperCase() + " LOGIN", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.add(new JLabel("Username:"));
        form.add(userField);
        form.add(new JLabel("Password:"));
        form.add(passField);

        JButton signup = new JButton("Sign Up");
        JButton login = new JButton("Login");
        JButton back = new JButton("Back");

        // Common Login Button
        login.addActionListener(_ -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (role.equals("merchant")) {
                if ("admin".equals(username) && "password".equals(password)) {
                    JOptionPane.showMessageDialog(this, "Merchant login successful!");
                    switchToDashboard(app, 0); // 0 is a placeholder for merchant ID
                } else {
                    showError("Invalid merchant credentials.");
                }
            } else if (role.equals("customer")) {
                if (!isValidInput(username, password))
                    return;

                Integer customerId = CustomerDAO.authenticateCustomer(username, password);
                if (customerId != null) {
                    JOptionPane.showMessageDialog(this, "Customer login successful!");
                    switchToDashboard(app, customerId);
                } else {
                    showError("Invalid username or PIN.");
                }
            }

        });

        // Customer Sign-Up
        signup.addActionListener(_ -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (!isValidInput(username, password))
                return;

            boolean success = CustomerDAO.signupCustomer(username, password);
            if (success) {
                Integer customerId = CustomerDAO.authenticateCustomer(username, password); // Reuse login method
                if (customerId != null) {
                    JOptionPane.showMessageDialog(this, "Signup successful! You are now logged in.");
                    switchToDashboard(app, customerId);
                }
            } else {
                showError("Signup failed. Username already exist.");
            }
        });

        back.addActionListener(_ -> app.showView("menu"));

        // Layout setup
        JPanel buttons = new JPanel();
        if (role.equals("customer")) {
            buttons.add(signup);
        }
        buttons.add(login);
        buttons.add(back);

        add(title, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private boolean isValidInput(String user, String pass) {
        if (user.isEmpty() || !pass.matches("\\d{4}")) {
            showError("Username required and password must be a 4-digit PIN.");
            return false;
        }
        return true;
    }

    private void switchToDashboard(MainAppFrame app, int customerId) {
        JFrame dashboard = switch (role) {
            case "merchant" -> MerchantFrame.getInstance();
            case "customer" -> CustomerFrame.getInstance(customerId); // <- use customerId here
            default -> {
                showError("Unknown role: " + role);
                yield null;
            }
        };
        if (dashboard != null) {
            dashboard.setVisible(true);
            app.showView("menu");
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
