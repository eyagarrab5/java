package services;

import interfaces.IServiceCrud;
import models.User;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CrudUser implements IServiceCrud<User> {
    private static final Logger LOGGER = Logger.getLogger(CrudUser.class.getName());
    private final Connection conn = MyDatabase.getInstance().getConnection();

    @Override
    public User getById(int id) {
        String query = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            LOGGER.warning("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
        }
        return null;
    }

    @Override
    public void add(User user) {
        String qry = "INSERT INTO `user` (`email`,`password_hash`,`role`,`verified`,`first_name`, `last_name`,`telephone`, `vehicule`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(qry, Statement.RETURN_GENERATED_KEYS)) {
            // Le mot de passe est déjà haché dans SignupController
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getPassword_hash());
            statement.setString(3, user.getRole());
            statement.setInt(4, user.isVerified() ? 1 : 0);
            statement.setString(5, user.getFirst_name());
            statement.setString(6, user.getLast_name());
            statement.setString(7, user.getTelephone());
            statement.setString(8, user.getVehicule());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        System.out.println("✅ Utilisateur '" + user.getEmail() + "' ajouté avec succès.");
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM `user`";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }

        return users;
    }

    @Override
    public void update(User user) {
        String qry = "UPDATE `user` SET `first_name` = ?, `last_name` = ?, `role` = ?, `verified` = ?, `vehicule` = ?, `email` = ?, `password_hash` = ?, `telephone` = ? WHERE `id` = ?";

        try (PreparedStatement statement = conn.prepareStatement(qry)) {
            statement.setString(1, user.getFirst_name());
            statement.setString(2, user.getLast_name());
            statement.setString(3, user.getRole());
            statement.setInt(4, user.isVerified() ? 1 : 0);
            statement.setString(5, user.getVehicule());
            statement.setString(6, user.getEmail());
            statement.setString(7, user.getPassword_hash()); // On suppose qu'il est déjà haché si modifié
            statement.setString(8, user.getTelephone());
            statement.setInt(9, user.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Utilisateur mis à jour avec succès.");
            } else {
                System.out.println("❌ Aucun utilisateur trouvé avec l'ID : " + user.getId());
            }
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String qry = "DELETE FROM `user` WHERE `id` = ?";
        try (PreparedStatement statement = conn.prepareStatement(qry)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("🗑️ Utilisateur supprimé avec succès.");
        } catch (SQLException e) {
            LOGGER.severe("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String email = rs.getString("email");
        String password_hash = rs.getString("password_hash");
        String role = rs.getString("role");
        boolean verified = rs.getBoolean("verified");
        String first_name = rs.getString("first_name");
        String last_name = rs.getString("last_name");
        String telephone = rs.getString("telephone");
        String vehicule = rs.getString("vehicule");

        User user = new User(email, password_hash, role, verified, first_name, last_name, telephone, vehicule);
        user.setId(id);
        return user;
    }
    public User findByEmailOrPhone(String input) {
        User user = null;
        String sql = "SELECT * FROM user WHERE email = ? OR telephone = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, input);
            stmt.setString(2, input);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setTelephone(rs.getString("phone_number")); // ou "telephone" si c'est le nom réel dans ta DB
                user.setPassword_hash(rs.getString("password_hash"));
                user.setFirst_name(rs.getString("first_name"));
                user.setLast_name(rs.getString("last_name"));
                user.setRole(rs.getString("role"));
                user.setVehicule(rs.getString("vehicule"));
                user.setVerified(rs.getBoolean("verified"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }


}
