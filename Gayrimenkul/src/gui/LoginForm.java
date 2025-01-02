package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private JPasswordField passwordField;

    // XAMPP MySQL Bağlantı Bilgileri
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoginForm frame = new LoginForm();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LoginForm() {
        setTitle("Gelişim Gayrimenkul Kullanıcı Giriş Paneli");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 331, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("Kullanıcı Giriş Paneli");
        lblNewLabel.setFont(new Font("Calibri", Font.BOLD, 20));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(55, 11, 205, 37);
        contentPane.add(lblNewLabel);

        textField = new JTextField();
        textField.setBounds(110, 76, 150, 20);
        contentPane.add(textField);
        textField.setColumns(10);

        passwordField = new JPasswordField();
        passwordField.setBounds(110, 107, 150, 20);
        contentPane.add(passwordField);

        JLabel lblNewLabel_1 = new JLabel("Kullanıcı Adı:");
        lblNewLabel_1.setFont(new Font("Calibri", Font.PLAIN, 12));
        lblNewLabel_1.setBounds(31, 81, 69, 14);
        contentPane.add(lblNewLabel_1);

        JLabel lblNewLabel_2 = new JLabel("Şifre:");
        lblNewLabel_2.setBounds(33, 110, 46, 14);
        contentPane.add(lblNewLabel_2);

        // Giriş Yap Butonu
        JButton btnLogin = new JButton("Giriş Yap");
        btnLogin.setBounds(160, 150, 100, 23);
        contentPane.add(btnLogin);

        // Kayıt Ol Butonu
        JButton btnRegister = new JButton("Kayıt Ol");
        btnRegister.setBounds(50, 150, 100, 23);
        contentPane.add(btnRegister);

        // Giriş İşlemi
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String kullaniciAdi = textField.getText();
                String sifre = new String(passwordField.getPassword());

                try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, kullaniciAdi);
                    preparedStatement.setString(2, sifre);

                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(contentPane, "Giriş Başarılı! Hoş geldiniz, " + kullaniciAdi);

                        // MenuForm'a geçiş yap
                        EventQueue.invokeLater(() -> {
                            MenuForm menuForm = new MenuForm(kullaniciAdi);
                            menuForm.setVisible(true);
                        });
                        dispose(); // LoginForm'u kapat
                    } else {
                        JOptionPane.showMessageDialog(contentPane, "Giriş Başarısız! Kullanıcı adı veya şifre hatalı.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(contentPane, "Veritabanı bağlantı hatası!");
                }
            }
        });
        
        
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/logo/logo2.png"));
            setIconImage(logoIcon.getImage());
        } catch (Exception e) {
            System.err.println("Logo yüklenemedi: " + e.getMessage());
        }
        
        try {
            // Logoyu yükle
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/logo/logo.png"));

            // JLabel içerisine logoyu ekle
            JLabel logoLabel = new JLabel(logoIcon);

            // Logonun konumunu ve boyutunu ayarla
            logoLabel.setBounds(10, 11, 46, 46); // x, y, genişlik, yükseklik

            // Logoyu panele ekle
            contentPane.add(logoLabel);
        } catch (Exception e) {
            System.err.println("Logo yüklenemedi: " + e.getMessage());
        }

        
        
        // Kayıt Olma İşlemi
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String kullaniciAdi = textField.getText();
                String sifre = new String(passwordField.getPassword());

                try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    String query = "INSERT INTO users (username, password) VALUES (?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, kullaniciAdi);
                    preparedStatement.setString(2, sifre);

                    preparedStatement.executeUpdate();
                    JOptionPane.showMessageDialog(contentPane, "Kayıt Başarılı! Artık giriş yapabilirsiniz.");
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == 1062) { // Kullanıcı adı zaten var
                        JOptionPane.showMessageDialog(contentPane, "Bu kullanıcı adı zaten alınmış. Lütfen başka bir ad deneyin.");
                    } else {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(contentPane, "Veritabanı bağlantı hatası!");
                    }
                }
            }
        });
    }
}
