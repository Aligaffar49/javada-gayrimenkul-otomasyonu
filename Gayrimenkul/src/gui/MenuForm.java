package gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.sql.*;

public class MenuForm extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private String username; // Kullanıcı adı saklanıyor
    

    public MenuForm(String username) {
    	
    	
    	
    	try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/logo/logo2.png"));
            setIconImage(logoIcon.getImage());
        } catch (Exception e) {
            System.err.println("Logo yüklenemedi: " + e.getMessage());
        }
    	
    	
        this.username = username;
        setTitle("Gayrimenkul Yönetim Paneli");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblWelcome = new JLabel("Hoş geldiniz, " + username + "!");
        lblWelcome.setFont(new Font("Calibri", Font.BOLD, 16));
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblWelcome.setBounds(10, 11, 414, 30);
        contentPane.add(lblWelcome);

        JButton btnAddProperty = new JButton("Gayrimenkul Ekle");
        btnAddProperty.setBounds(150, 70, 150, 30);
        contentPane.add(btnAddProperty);

        JButton btnViewProperties = new JButton("Gayrimenkul Listele");
        btnViewProperties.setBounds(150, 120, 150, 30);
        contentPane.add(btnViewProperties);

        JButton btnDeleteProperty = new JButton("Gayrimenkul Sil");
        btnDeleteProperty.setBounds(150, 170, 150, 30);
        contentPane.add(btnDeleteProperty);

        btnAddProperty.addActionListener(e -> addProperty());
        btnViewProperties.addActionListener(e -> viewProperties());
        btnDeleteProperty.addActionListener(e -> deleteProperty());
    }

    
    private void addProperty() {
        JTextField propertyNameField = new JTextField();
        JTextField propertyLocationField = new JTextField();
        JTextField propertyPriceField = new JTextField();

        Object[] fields = {
            "Gayrimenkul Adı:", propertyNameField,
            "Lokasyon:", propertyLocationField,
            "Fiyat:", propertyPriceField
        };

        int option = JOptionPane.showConfirmDialog(contentPane, fields, "Gayrimenkul Ekle", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = propertyNameField.getText();
            String location = propertyLocationField.getText();
            String price = propertyPriceField.getText();

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "INSERT INTO properties (name, location, price, added_by) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, location);
                preparedStatement.setDouble(3, Double.parseDouble(price));
                preparedStatement.setString(4, username); // Kullanıcı adı ekleniyor

                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(contentPane, "Gayrimenkul başarıyla eklendi!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(contentPane, "Veritabanı hatası!");
            }
        }
    }

    private void viewProperties() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM properties";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            StringBuilder properties = new StringBuilder("Gayrimenkul Listesi:\n\n");
            while (resultSet.next()) {
                properties.append("ID: ").append(resultSet.getInt("id"))
                        .append(", Ad: ").append(resultSet.getString("name"))
                        .append(", Lokasyon: ").append(resultSet.getString("location"))
                        .append(", Fiyat: ").append(resultSet.getDouble("price"))
                        .append(", Ekleyen: ").append(resultSet.getString("added_by"))
                        .append("\n");
            }

            JOptionPane.showMessageDialog(contentPane, properties.length() > 0 ? properties.toString() : "Hiçbir gayrimenkul bulunamadı.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(contentPane, "Veritabanı hatası!");
        }
    }

    private void deleteProperty() {
        String propertyId = JOptionPane.showInputDialog(contentPane, "Silmek istediğiniz gayrimenkulün ID'sini girin:");

        if (propertyId != null && !propertyId.trim().isEmpty()) {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String queryCheck = "SELECT * FROM properties WHERE id = ? AND added_by = ?";
                PreparedStatement checkStatement = connection.prepareStatement(queryCheck);
                checkStatement.setInt(1, Integer.parseInt(propertyId));
                checkStatement.setString(2, username);

                ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next()) {
                    String queryDelete = "DELETE FROM properties WHERE id = ?";
                    PreparedStatement deleteStatement = connection.prepareStatement(queryDelete);
                    deleteStatement.setInt(1, Integer.parseInt(propertyId));

                    deleteStatement.executeUpdate();
                    JOptionPane.showMessageDialog(contentPane, "Gayrimenkul başarıyla silindi!");
                } else {
                    JOptionPane.showMessageDialog(contentPane, "Bu gayrimenkulü silme yetkiniz yok!");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(contentPane, "Veritabanı hatası!");
            }
        }
    }
    
}
