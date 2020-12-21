package pl.coderslab.entity;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.utils.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class UserDao {

    private static final String CREATE = "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";

    private static final String READ = "SELECT * FROM users where id = ?";

    private static final String UPDATE = "UPDATE users SET username = ?, email = ?, password = ? where id = ?";

    private static final String DELETE = "DELETE FROM users WHERE id = ?";

    private static final String SHOW_ALL = "SELECT * FROM users";

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private pl.coderslab.entity.User[] addToArray (pl.coderslab.entity.User u, pl.coderslab.entity.User[] users){
        pl.coderslab.entity.User[] tmpUsers = Arrays.copyOf(users, users.length+1);
        tmpUsers[users.length] = u;
        return tmpUsers;
    }

    public pl.coderslab.entity.User create(pl.coderslab.entity.User user) {
        try (Connection connection = DbUtil.getConnection()) {
            PreparedStatement preStmt = connection.prepareStatement(CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
            preStmt.setString(1, user.getUserName());
            preStmt.setString(2, user.getEmail());
            preStmt.setString(3, hashPassword(user.getPassword()));
            preStmt.executeUpdate();

            ResultSet resultSet = preStmt.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            return user;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public pl.coderslab.entity.User read (int userID){
        try (Connection connection = DbUtil.getConnection()) {
            PreparedStatement preStmt = connection.prepareStatement(READ);
            preStmt.setInt(1, userID);

            ResultSet resultSet = preStmt.executeQuery();

            if (resultSet.next()){
                pl.coderslab.entity.User user = new pl.coderslab.entity.User();
                user.setId(resultSet.getInt("id"));
                user.setUserName(resultSet.getString("username"));
                user.setEmail(resultSet.getString(("email")));
                user.setPassword(resultSet.getString("password"));
                return user;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }return null;
}

    public void update (pl.coderslab.entity.User user){
        try (Connection connection = DbUtil.getConnection()){
            PreparedStatement preStmt = connection.prepareStatement(UPDATE);
            preStmt.setString(1, user.getUserName());
            preStmt.setString(2, user.getEmail());
            preStmt.setString(3, hashPassword(user.getPassword()));
            preStmt.setInt(4,user.getId());
            preStmt.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }


    }

    public pl.coderslab.entity.User[] showAll () {
        try (Connection connection = DbUtil.getConnection()) {
            pl.coderslab.entity.User[] users = new pl.coderslab.entity.User[0];
            PreparedStatement preStmt = connection.prepareStatement(SHOW_ALL);
            ResultSet resultSet = preStmt.executeQuery();

            while (resultSet.next()){
                pl.coderslab.entity.User user = new pl.coderslab.entity.User();
                user.setId(resultSet.getInt("id"));
                user.setUserName(resultSet.getString("Username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("Password"));
                users = addToArray(user, users);
            } return users;


        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
}

    public void delete (int id){
        try (Connection connection = DbUtil.getConnection()) {
            PreparedStatement preStmt = connection.prepareStatement(DELETE);
            preStmt.setInt(1, id);
            preStmt.executeUpdate();

    } catch (SQLException exception) {
            exception.printStackTrace();
        }
}}
