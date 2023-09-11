import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JDialog {
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private JPanel loginPanel;
    private User user;

    public LoginForm(JFrame parent){
        super (parent);
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = String.valueOf(pfPassword.getPassword());

                try {
                    user = getAuthenticatedUser(email,
                            password);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                if (user!=null){
                    dispose();
                }
                else{
                    JOptionPane.showMessageDialog(LoginForm.this,
                                "Email or Password invalid",
                                "Try again",
                    JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        setVisible(true);
    }
    private User getAuthenticatedUser(String email, String password) throws SQLException {
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost/mystore?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try{
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE email =? AND password=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                user = new User();
                user.name = resultSet.getString("name");
                user.fathername = resultSet.getString("fathername");
                user.course = resultSet.getString("course");
                user.rollNo = resultSet.getString("rollNo");
                user.password = resultSet.getString("password");
            }

            stmt.close();
            conn.close();

        }catch(Exception e){
            e.printStackTrace();
        }

        return user;
    }

    public static void main(String[] args) {
        LoginForm loginForm = new LoginForm(null);
        User user = loginForm.user;
        if (user!=null) {
            System.out.println("Successful Authentication of: "+ user.name);
            System.out.println("           name: " +user.name);
            System.out.println("           fathername: " +user.fathername);
            System.out.println("           course: " +user.course);
        }
        else {
            System.out.println("Authentication Canceled");
        }
    }
}
