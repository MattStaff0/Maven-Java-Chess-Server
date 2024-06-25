package chess;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;


public class LoginPage extends JFrame {
    private LoginHandler loginHandler;

    private JPanel mainLoginPanel = new JPanel(new BorderLayout());
    private BackgroundPanel backgroundPanel = new BackgroundPanel("/Users/matt_staff/Desktop/Java Chess Game Maven/chessgame/src/main/resources/Login.jpeg");

    private JLabel usernameLabel = new JLabel("Username:");
    private JLabel passwordLabel = new JLabel("Password: ");
    static JLabel passwordStrength = new JLabel("Password Strength: ");
    private JLabel title = new JLabel("Chess");

    private JTextField usernameInput = new JTextField(15);
    private JPasswordField passwordInput = new JPasswordField(15);

    private JButton loginButton = new JButton("Login");
    private JButton newUserButton = new JButton("New Here? Create Account!");

    private char[] specialChars = {
        '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+', '=', '[', ']', '{', '}', ',', '.', '/', '\\',
        '|', '<', '>', '?', '~', ';', ' '
    };
    private char[] passSpecialChars = {
        '=', '[', ']', '{', '}', '/', '\\',
        '|', '<', '>', '~', ' ', '.', ','
    };

    public LoginPage() {
        this.makeFrame();
        this.makeLoginPanel();
        this.setVisible(true);
    }

    void makeFrame() {
        this.setTitle("Chess");
        this.setSize(new Dimension(730, 450));
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    void makeLoginPanel() {
        //  settings
        this.mainLoginPanel.setPreferredSize(new Dimension(730, 450));

        //  background
        this.backgroundPanel.setLayout(new BorderLayout());
        //JLabel background = new JLabel(new ImageIcon("/Users/matt_staff/Desktop/Java Chess Game Maven/chessgame/src/main/resources/Login.jpeg"));
        //background.setLayout(new BorderLayout());

        // add to mainPanel
        this.mainLoginPanel.add(backgroundPanel);

        // make login area 
        JPanel loginArea = new JPanel(new GridBagLayout());
        loginArea.setOpaque(false); // Make panel transparent

        // title creation
        this.title.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 24));
        this.title.setForeground(Color.BLACK);
        
        // Label settings
        this.passwordLabel.setForeground(Color.BLACK);
        this.usernameLabel.setForeground(Color.BLACK);
        LoginPage.passwordStrength.setForeground(Color.BLACK);

        this.passwordLabel.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 14));
        this.usernameLabel.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 14));
        LoginPage.passwordStrength.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 14));

        // Key Listener and Thread Creation
        this.passwordInput.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                String password = new String(passwordInput.getPassword());
                PasswordChecker checker = new PasswordChecker(password);
                String strength = checker.passwordStrength();
                LoginPage.passwordStrength.setText("Password Strength: " + strength);
            }
        });

        // Button Listener
        this.loginButton.addActionListener(e -> handleLogin(e));
        this.newUserButton.addActionListener(e -> handleLogin(e));

        RealTimePassChecker realTimeChecker = new RealTimePassChecker(this.passwordInput);
        realTimeChecker.start();

        // GBC Constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER;
        loginArea.add(this.title, gbc);

        // Username label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        loginArea.add(this.usernameLabel, gbc);

        // Username input
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginArea.add(this.usernameInput, gbc);

        // Password label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        loginArea.add(this.passwordLabel, gbc);

        // Password input
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginArea.add(this.passwordInput, gbc);

        // Password strength
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        loginArea.add(LoginPage.passwordStrength, gbc);

        // Login button
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        loginArea.add(this.loginButton, gbc);

        // New user button
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginArea.add(this.newUserButton, gbc);

        this.backgroundPanel.add(loginArea, BorderLayout.NORTH);
        this.add(this.mainLoginPanel);
    }

    private void handleLogin(ActionEvent source){
        if (source.getSource().equals(this.loginButton)){
            //  checks to make sure input is valid
            String username = this.usernameInput.getText();
            char[] password = this.passwordInput.getPassword();

            if (username.isEmpty() || password.length == 0) {
                JOptionPane.showMessageDialog(this, "Please provide both username and password.");
            }

            else if(!DBConnect.containsSpecialChars(username, this.specialChars) || !DBConnect.containsSpecialChars(password, this.passSpecialChars)){
                this.loginHandler = new LoginHandler(this, username, password);
            } else{JOptionPane.showMessageDialog(this, "Particular special characters not allowed.");}
        }

        else if (source.getSource().equals(this.newUserButton)){
            String username = this.usernameInput.getText();
            char[] password = this.passwordInput.getPassword();

            if (username.isEmpty() || password.length == 0) {
                JOptionPane.showMessageDialog(this, "Please provide both username and password.");
            }
            else if(!DBConnect.containsSpecialChars(username, this.specialChars) || !DBConnect.containsSpecialChars(password, this.passSpecialChars)){
                if ((LoginPage.passwordStrength.getText().equals("Password Strength: Strong") || LoginPage.passwordStrength.getText().equals("Password Strength: Moderate"))){
                this.loginHandler = new LoginHandler(this, username, password, true);
                } else {JOptionPane.showMessageDialog(this, "Weak Password.");}
            } else{JOptionPane.showMessageDialog(this, "Particular special characters not allowed.");}

        }
    }
}

class LoginHandler{
    private LoginPage parentComponent;
    private String username;
    private char[] password;
    private String hashedPassword;
    private boolean newUser = false;

    public LoginHandler(LoginPage parentComponent, String username, char[] password, boolean newUser){
        this.parentComponent = parentComponent;
        this.username = username;
        this.password = password;
        this.newUser = newUser; 

        try {
            this.hashedPassword = DBConnect.hashPassword(new String(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (isUsernameAvailable()){
            if (addNewUserInfo(this.username, this.hashedPassword)){
                this.parentComponent.dispose();
                // Open the home page
                SwingUtilities.invokeLater(() -> {
                    new ChessHomePage(this.username, this.hashedPassword, true);
                });
                //new HomePage(this.username, this.hashPassword);
            }
            else{JOptionPane.showMessageDialog(this.parentComponent, "Error adding info to database, try again!");}
        
            }
        }

    public LoginHandler(LoginPage parentComponent, String username, char[] password){
        this.parentComponent = parentComponent;
        this.username = username;
        this.password = password;
        
        try {
            this.hashedPassword = DBConnect.hashPassword(new String(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (isValidUsername()){
            if(isValidPassword()){
                this.parentComponent.dispose();
                // Open the home page
                SwingUtilities.invokeLater(() -> {
                    new ChessHomePage(this.username, this.hashedPassword, false);
                });
            } else{JOptionPane.showMessageDialog(this.parentComponent, "Invalid Password for username: " + this.username);}
        } else {JOptionPane.showMessageDialog(this.parentComponent, "Invalid username: " + this.username);}
    }

    private boolean addNewUserInfo(String username, String password) {
        System.out.println(password);
        System.out.println(username);
        String userQuery = "INSERT INTO users(username, password) VALUES(?,?)";
        String winLossQuery = "INSERT INTO WinLoss(Username) VALUES(?)";
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        try (Connection connect = DBConnect.connect()) {
            ps = connect.prepareStatement(userQuery);
            ps.setString(1, username);
            ps.setString(2, password);
            int added = ps.executeUpdate();
            if (added > 0) {
                ps2 = connect.prepareStatement(winLossQuery);
                ps2.setString(1, username);
                int winLossAdded = ps2.executeUpdate();
                return winLossAdded > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (ps2 != null) ps2.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    private boolean isUsernameAvailable() {
        // prepared Statement
        PreparedStatement ps = null;
        String query = "SELECT username FROM users WHERE username = ?";
    
        try (Connection connect = DBConnect.connect()) {
            ps = connect.prepareStatement(query);
            ps.setString(1, this.username);
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next(); // Return true if the username does not exist
            } 
        } catch (SQLException e) {
            return false; // returns false if error occurs
        }
    }
    

    private boolean isValidUsername(){
        // prepared Statement
        PreparedStatement ps = null;
        String query = "SELECT username FROM users WHERE username = ?";

        try(Connection connect = DBConnect.connect()){
            ps = connect.prepareStatement(query);
            ps.setString(1, this.username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Return true if the username does exist
            } 
        } catch (SQLException e){
            return false;  // returns false if error occurs or if username is not in database
        }
    } 

    private boolean isValidPassword(){
        // prepared Statement
        PreparedStatement ps = null;
        String query = "SELECT password FROM users WHERE username = ?";
        try(Connection connect = DBConnect.connect()){
            ps = connect.prepareStatement(query);
            ps.setString(1, this.username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    // Check if the provided password matches the stored password
                    return storedPassword.equals(this.hashedPassword);
                } else {
                    // No such username found in the database
                    return false;
                }
            }
        } catch (SQLException e){ return false;} 

    }
}

class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        this.setLayout(new BorderLayout());
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}







































class PasswordChecker {
    private String password;
    private static final Set<Character> lowercaseLetters = new HashSet<>();
    private static final Set<Character> uppercaseLetters = new HashSet<>();
    private static final Set<Character> numbers = new HashSet<>();
    private static final Set<Character> specialChars = new HashSet<>();

    static {
        for (char c = 'a'; c <= 'z'; c++) {
            lowercaseLetters.add(c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            uppercaseLetters.add(c);
        }
        for (char c = '0'; c <= '9'; c++) {
            numbers.add(c);
        }
        String specialCharacters = "!@#$%^&*()-+";
        for (char c : specialCharacters.toCharArray()) {
            specialChars.add(c);
        }
    }

    public PasswordChecker(String password) {
        this.password = password;
    }

    public String passwordStrength() {
        int passLen = password.length();
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (lowercaseLetters.contains(c)) hasLower = true;
            if (uppercaseLetters.contains(c)) hasUpper = true;
            if (numbers.contains(c)) hasDigit = true;
            if (specialChars.contains(c)) hasSpecial = true;
        }

        if (passLen ==  0) return "";

        if (hasLower && hasUpper && hasDigit && hasSpecial) {
            if (passLen >= 12) {
                return "Strong";
            } else if (passLen > 8) {
                return "Moderate";
            } else {
                return "Weak-Short";
            }
        } else {
            return "Weak";
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


class RealTimePassChecker extends Thread{
    private JPasswordField passwordInput;

    public RealTimePassChecker(JPasswordField pass){
        this.passwordInput = pass;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500); // Check every 500ms
                SwingUtilities.invokeLater(() -> {
                    String password = new String(passwordInput.getPassword());
                    PasswordChecker checker = new PasswordChecker(password);
                    String strength = checker.passwordStrength();
                    LoginPage.passwordStrength.setText("Password Strength: " + strength);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}