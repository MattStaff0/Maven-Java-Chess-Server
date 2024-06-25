package chess;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class DBConnect {
    public static Connection connect(){
        Connection con = null;

        try{
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:/Users/matt_staff/Desktop/Java Chess Game Maven/chessgame/src/main/resources/chess.db");
            
        } catch (ClassNotFoundException | SQLException e) {e.printStackTrace();}

        return con;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String hashPassword(String password) throws NoSuchAlgorithmException{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] pass = digest.digest(password.getBytes());
        return bytesToHex(pass);
    }

    public static boolean containsSpecialChars(char[] input, char[] specialChars) {
        Set<Character> specialCharsSet = new HashSet<>();
        for (char c : specialChars) {
            specialCharsSet.add(c);
        }

        for (char c : input) {
            if (specialCharsSet.contains(c)) {
                return true; // Found a special character
            }
        }
        return false; // No special characters found
    }

    public static boolean containsSpecialChars(String input, char[] specialChars) {
        Set<Character> specialCharsSet = new HashSet<>();
        for (char c : specialChars) {
            specialCharsSet.add(c);
        }

        for (char c : input.toCharArray()) {
            if (specialCharsSet.contains(c)) {
                return true; // Found a special character
            }
        }
        return false; // No special characters found
    }


}
