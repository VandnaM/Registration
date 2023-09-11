import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class RegistrationForm extends JDialog{
    private JTextField tfName;
    private JTextField tfFatherName;
    private JTextField tfEmail;
    private JTextField tfCourse;
    private JPasswordField pfPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel registerPanel;


    public RegistrationForm(JFrame parent){
        super (parent);
        setTitle("Create a new acoount");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        btnRegister.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
                {
                    try {
                        registerUser();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
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

    private void registerUser() throws SQLException {
        String name = tfName.getText();
        String fathername = tfFatherName.getText();
        String email = tfEmail.getText();
        String course = tfCourse.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmPassword = String.valueOf(pfConfirmPassword.getPassword());


        Component parentComponent;
        Object message;
        if (name.isEmpty() || fathername.isEmpty() || email.isEmpty() || course.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter all fields",
                    "try again", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(  this,
                    " Confirm Password does not match",
                    "Try Again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        user = addUserToDatabase(name, fathername, email, course, password);
        if (user != null){
            dispose();
        }
        else{
            JOptionPane.showMessageDialog(this,
            "Failed to register new user",
            "Try Again",
            JOptionPane.ERROR_MESSAGE);
        }
    }

    public User user;
    private <preparedStatement> User addUserToDatabase(String name, String fathername, String email, String course, String password) throws SQLException {
        User user = null;
        final String DB_URL = "jdbc:mysql://localhost/MyStore?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try{
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO users (name, fathername, email, course, password) "+
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, fathername);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, course);
            preparedStatement.setString(5, password);

            int addedRows = preparedStatement.executeUpdate();
            if (addedRows>0){
                user = new User();
                user.name = name;
                user.fathername = fathername;
                user.email = email;
                user.course = course;
                user.password = password;
            }

            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return user;
    }

    public static void main(String[] args) {
        JFrame parent = null;
        RegistrationForm myForm = new RegistrationForm(parent);
        User user = myForm.user;
        if (user != null){
            System.out.println("Successfull registration of:" + user.name);
        }
        else{
            System.out.println("Registration Canceled");
        }
    }
}
