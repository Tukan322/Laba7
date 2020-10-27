package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class DBworking {

    private static final String url = "jdbc:postgresql://pg:5432/studs";
    private static final String user = "s286561";
    private static final String password = "uiu193";
    private static Connection connection;
    private static PreparedStatement preparedStatement;
    private static ResultSet rs;
    private static ReadWriteLock lock = new ReentrantReadWriteLock();

    public static Boolean ConnectionToDB() throws SQLException, ClassNotFoundException {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful");
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            throw e;

        }
    }



    public static Boolean addNewUser(String user, String password) {
        try {
            preparedStatement = connection.prepareStatement("insert into users ( USERNAME, PASSWORD) values (?, ?)");
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, password);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("SQL ex");
            return false;
        }
    }

    public static boolean isUser(String username, String pass) {
        User user = null;

        try {
            String sql = "SELECT * FROM users WHERE username LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User( resultSet.getString(1), resultSet.getString(2));
            }
            if (user == null){
                return false;
            }
        } catch (SQLException SqlEx) {
            SqlEx.printStackTrace();
            System.out.println("SQL exception");
        }
        if (user!=null)
        return user.getPassword().equals(pass);
        else return false;
    }

    public static boolean addToDb(Organization org, String username) throws SQLException {
        String sql = "INSERT INTO orgs(" +
                "org_id," +
                " name," +
                " coordinate_x," +
                " coordinate_y," +
                " creation_date," +
                " annual_turnover," +
                " full_name," +
                " type," +
                " employees_count," +
                " street," +
                " town," +
                " town_x," +
                " town_y," +
                " town_z," +
                " user_name)" +
                "VALUES(nextval('orgs_sequence'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, org.getName());
        preparedStatement.setDouble(2, Double.parseDouble(String.valueOf(org.getCoordinatesX())));
        preparedStatement.setDouble(3,Double.parseDouble(String.valueOf(org.getCoordinatesY())));
        preparedStatement.setDate(4, Organization.getCreationDate());
        preparedStatement.setInt(5,org.getAnnualTurnover());
        preparedStatement.setString(6,org.getFullName());
        preparedStatement.setString(7,org.getStringType());
        preparedStatement.setLong(8,org.getEmployeesCount());
        preparedStatement.setString(9,org.getStreet());
        preparedStatement.setString(10,org.getTown());
        preparedStatement.setDouble(11, org.getX());
        preparedStatement.setLong(12, org.getY());
        preparedStatement.setDouble(13, org.getZ());
        preparedStatement.setString(14, username);
        preparedStatement.execute();
        /*
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, String.valueOf(org.getId()));
        preparedStatement.setString(2, org.getName());
        preparedStatement.setString(3, String.valueOf(org.getCoordinatesX()));
        preparedStatement.setString(4,String.valueOf(org.getCoordinatesY()));
        preparedStatement.setDate(5, Organization.getSqlCreationDate());
        preparedStatement.setString(6, String.valueOf(org.getAnnualTurnover()));
        preparedStatement.setString(7,org.getFullName());
        preparedStatement.setString(8,org.getStringType());
        preparedStatement.setString(9, String.valueOf(org.getEmployeesCount()));
        preparedStatement.setString(10,org.getStreet());
        preparedStatement.setString(11,org.getTown());
        preparedStatement.setString(12, String.valueOf(org.getX()));
        preparedStatement.setString(13, String.valueOf(org.getY()));
        preparedStatement.setString(14, String.valueOf(org.getZ()));
        preparedStatement.setString(15, username);
        preparedStatement.execute();
         */
        return true;
    }

    public static ArrayList<Organization> getFromDb(ArrayList<Organization> orgs) throws SQLException {
        Organization org = new Organization();
        String sql = "SELECT * FROM orgs;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            org.setId(resultSet.getInt(1));
            org.setName(resultSet.getString(2));
            org.setCoordinatesX((int) resultSet.getDouble(3));
            org.setCoordinatesY((int) resultSet.getDouble(4));
            org.setCreationDate(resultSet.getDate(5));
            org.setAnnualTurnover(resultSet.getInt(6));
            org.setFullName(resultSet.getString(7));
            switch (resultSet.getString(8)){
                case("TRUST"):
                    org.setType(OrganizationType.TRUST);
                    break;
                case ("GOVERNMENT"):
                    org.setType(OrganizationType.GOVERNMENT);
                    break;
                case ("PRIVATE_LIMITED_COMPANY"):
                    org.setType(OrganizationType.PRIVATE_LIMITED_COMPANY);
                    break;
                case ("OPEN_JOINT_STOCK_COMPANY"):
                    org.setType(OrganizationType.OPEN_JOINT_STOCK_COMPANY);
                    break;
            }
            org.setEmployeesCount(resultSet.getLong(9));
            org.setStreet(resultSet.getString(10));
            org.setTown(resultSet.getString(11));
            org.setX((float) resultSet.getDouble(12));
            org.setY(resultSet.getLong(13));
            org.setZ((float) resultSet.getDouble(14));
            orgs.add(org);
        }
        return orgs;
    }

    public static int getSqlId(Organization org) throws SQLException {

        String sql = "SELECT * FROM orgs WHERE full_name LIKE ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, org.getFullName());
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next())
        return resultSet.getInt(1);
        return (-1);
    }

    public static void clearTable() throws SQLException{
        String sql = "DELETE FROM orgs;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.executeUpdate();
    }

    public static String isThisUser(int id) throws SQLException{
        String sql = "SELECT * FROM orgs WHERE org_id=?;";
        String username = "";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next())
            username = resultSet.getString(15);
        return username;
    }

    public static void removeById(int id) throws SQLException {
        String sql = "DELETE FROM orgs WHERE org_id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    public static void removeByType(String type) throws SQLException{
        String sql = "DELETE FROM orgs WHERE type=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, type);
        preparedStatement.executeUpdate();
    }

    public static boolean isBusy(String login) throws SQLException {
        boolean isBusy = false;
        String sql = "SELECT * FROM users ;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            if (resultSet.getString(1).equals(login)) isBusy=true;
        }
        return isBusy;
    }
}