/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.services;

import com.paolodoom.sppcontroller.models.Automation;
import com.paolodoom.sppcontroller.models.AutomationType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PaoloDooM
 */
public class PersistanceService {

    static final String dbUrl = "jdbc:sqlite:sppcontroller.db";
    static final String dbClass = "org.sqlite.JDBC";

    public static Connection connectDB() throws Exception {
        Connection conn = DriverManager.getConnection(dbUrl);
        if (conn != null) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("connected to DB: " + meta.getDriverName());
            createTablesIfNotExists();
            return conn;
        } else {
            throw new Exception("Error connecting to database");
        }
    }

    public static void createTablesIfNotExists() throws SQLException {
        Connection conn = DriverManager.getConnection(dbUrl);
        Statement stmt = conn.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS Automations (\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	type TEXT CHECK( type IN ("
                + "                         '" + AutomationType.executable.toString() + "',"
                + "                         '" + AutomationType.keyCombination.toString() + "'"
                + "                     ) ) NOT NULL DEFAULT '" + AutomationType.executable.toString() + "',\n"
                + "	path TEXT NULL DEFAULT NULL,\n"
                + "	button TEXT NOT NULL DEFAULT ''\n"
                + ");";
        stmt.execute(sql);
        sql = "CREATE TABLE IF NOT EXISTS KeyCombinations (\n"
                + "	id INTEGER,\n"
                + "	key TEXT,\n"
                + "     PRIMARY KEY (id, key)\n"
                + ");";
        stmt.execute(sql);
        stmt.close();
        conn.close();
    }

    public static void insertAutomation(Automation automation) throws Exception {
        if (automation.getType() == AutomationType.executable) {
            String sql = "INSERT INTO Automations (type, button, path) VALUES (?, ?, ?);";
            Connection conn = connectDB();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, automation.getType().toString());
            pstmt.setString(2, automation.getButton());
            pstmt.setString(3, automation.getPath());
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } else {
            String sql = "INSERT INTO Automations (type, button) VALUES (?, ?);";
            Connection conn = connectDB();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, automation.getType().toString());
            pstmt.setString(2, automation.getButton());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            pstmt.close();
            if (rs.next()) {
                int id = rs.getInt(1);
                sql = "INSERT INTO KeyCombinations (id, key) VALUES (?, ?);";
                for (String key : automation.getKeyCombination()) {
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, id);
                    pstmt.setString(2, key);
                    pstmt.executeUpdate();
                    pstmt.close();
                }
            }
            conn.commit();
            conn.close();
        }
    }

    public static List<Automation> getAllAutomations() throws Exception {
        String sql = "SELECT * FROM Automations;";
        Connection conn = connectDB();
        Statement stmt = conn.createStatement();
        boolean isRs = stmt.execute(sql);
        List<Automation> automations = new ArrayList<>();
        if (isRs) {
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                if (rs.getString("type").equals(AutomationType.executable.toString())) {
                    automations.add(new Automation(rs.getInt("id"), AutomationType.executable, rs.getString("path"), rs.getString("button")));
                } else {
                    automations.add(new Automation(rs.getInt("id"), AutomationType.keyCombination, getKeyCombinations(rs.getInt("id")), rs.getString("button")));
                }
            }
        }
        stmt.close();
        conn.close();
        return automations;
    }

    public static Automation getAutomation(int id) throws Exception {
        String sql = "SELECT * FROM Automations WHERE id = ?;";
        Connection conn = connectDB();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        Automation automation = null;
        if (rs.next()) {
            if (rs.getString("type").equals(AutomationType.executable.toString())) {
                automation = new Automation(rs.getInt("id"), AutomationType.executable, rs.getString("path"), rs.getString("button"));
            } else {
                automation = new Automation(rs.getInt("id"), AutomationType.keyCombination, getKeyCombinations(rs.getInt("id")), rs.getString("button"));
            }
        }
        pstmt.close();
        conn.close();
        return automation;
    }

    public static List<String> getKeyCombinations(int id) throws Exception {
        String sql = "SELECT * FROM KeyCombinations WHERE id = ?;";
        Connection conn = connectDB();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        List<String> keyCombinations = new ArrayList<>();
        while (rs.next()) {
            keyCombinations.add(rs.getString("key"));
        }
        pstmt.close();
        conn.close();
        return keyCombinations;
    }

    public static void updateAutomation(Automation automation) throws Exception {
        String sql = "UPDATE Automations SET type = ?, path = ?, button = ? WHERE id = ?;";
        Connection conn = connectDB();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, automation.getType().toString());
        pstmt.setString(2, automation.getPath() == null ? "" : automation.getPath());
        pstmt.setString(3, automation.getButton() == null ? "" : automation.getButton());
        pstmt.setInt(4, automation.getId());
        pstmt.executeUpdate();
        pstmt.close();
        deleteKeyCombinations(automation.getId());
        if (automation.getType() == AutomationType.keyCombination) {
            sql = "INSERT INTO KeyCombinations (id, key) VALUES (?, ?);";
            for (String key : automation.getKeyCombination()) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, automation.getId());
                pstmt.setString(2, key);
                pstmt.executeUpdate();
                pstmt.close();
            }
        }
        conn.close();
    }

    public static void deleteAutomation(Automation automation) throws Exception {
        String sql = "DELETE FROM Automations WHERE id = ?;";
        Connection conn = connectDB();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, automation.getId());
        pstmt.executeUpdate();
        if (automation.getType() == AutomationType.keyCombination) {
            deleteKeyCombinations(automation.getId());
        }
        pstmt.close();
        conn.close();
    }

    public static void deleteKeyCombinations(int id) throws Exception {
        String sql = "DELETE FROM KeyCombinations WHERE id = ?;";
        Connection conn = connectDB();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
        pstmt.close();
        conn.close();
    }
}
