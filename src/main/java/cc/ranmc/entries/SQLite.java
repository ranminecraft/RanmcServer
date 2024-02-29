package cc.ranmc.entries;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SQLite {
    private Connection connection;

    public SQLite(String file) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + file);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        this.createTable();
    }

    public void close() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void createTable() {
        this.runCommand("CREATE TABLE PLAYER (ID INTEGER PRIMARY KEY AUTOINCREMENT, Player TEXT NOT NULL, Sex TEXT, QQCode TEXT, Location TEXT, Marry TEXT, JoinDate TEXT, QuitDate TEXT, MissDate TEXT, SignDate TEXT, GuildDate TEXT, Password TEXT, Guild TEXT, Title TEXT, Money TEXT)");
        this.runCommand("CREATE TABLE MARRY (ID INTEGER PRIMARY KEY AUTOINCREMENT, Husband TEXT NOT NULL, Wife TEXT NOT NULL, Value TEXT, Date TEXT, Home TEXT)");
        this.runCommand("CREATE TABLE WARP (ID INTEGER PRIMARY KEY AUTOINCREMENT, Date TEXT, Count TEXT, Residence TEXT, Player TEXT, Message TEXT)");
        this.runCommand("CREATE TABLE PREFIX (ID INTEGER PRIMARY KEY AUTOINCREMENT, Player TEXT, Prefix TEXT, Buff TEXT, Second_Buff TEXT)");
        this.runCommand("CREATE TABLE ITEMNAME (ID INTEGER PRIMARY KEY AUTOINCREMENT, Player TEXT, Name TEXT, Count TEXT)");
        this.runCommand("CREATE TABLE IPADDRESS (ID INTEGER PRIMARY KEY AUTOINCREMENT, Date TEXT, Player TEXT, Login TEXT, Address TEXT)");
        this.runCommand("CREATE TABLE FIGHT (ID INTEGER PRIMARY KEY AUTOINCREMENT, Player TEXT, Points INTEGER, Count INTEGER, Win INTEGER)");
        this.runCommand("CREATE TABLE HOMESET (ID INTEGER PRIMARY KEY AUTOINCREMENT, Player TEXT, Name TEXT, Location TEXT)");
        this.runCommand("CREATE TABLE BANLIST (ID INTEGER PRIMARY KEY AUTOINCREMENT, Player TEXT, Time TEXT, Address TEXT, Admin TEXT, Date TEXT, Reason TEXT)");
        this.runCommand("CREATE TABLE MUTELIST (ID INTEGER PRIMARY KEY AUTOINCREMENT, Player TEXT, Time TEXT)");
        this.runCommand("CREATE TABLE GUILD (ID INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, Points TEXT, Admin TEXT, Date TEXT, Invite TEXT, Material TEXT, Max INTEGER, Prefix TEXT, Valid TEXT, Attribute TEXT, Title TEXT, Location TEXT)");
        this.runCommand("CREATE TABLE TEAR (ID INTEGER PRIMARY KEY AUTOINCREMENT, Player TEXT, Price INTEGER, Type TEXT, Date TEXT)");
        this.runCommand("CREATE TABLE TEARLOG (ID INTEGER PRIMARY KEY AUTOINCREMENT, Seller TEXT, Buyer TEXT, Price INTEGER, Done TEXT, Date TEXT)");
        this.runCommand("CREATE TABLE TITLE (ID INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, Type TEXT, Price INTEGER, Buff TEXT, Second_Buff TEXT, Hide TEXT, Material TEXT, Text TEXT)");
        this.runCommand("CREATE TABLE TITLEOWN (ID INTEGER PRIMARY KEY AUTOINCREMENT, Player TEXT, Title TEXT, Time INTEGER)");
    }

    public int insert(String table, String name, String value) {
        return this.runCommandGetId("INSERT INTO " + table.toUpperCase() + " (" + name + ") VALUES ('" + value.replace(",", "','") + "');");
    }

    public int insert(String table, Map<String, String> map) {
        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();
        Iterator var5 = map.keySet().iterator();

        while(var5.hasNext()) {
            String key = (String)var5.next();
            name.append(key);
            name.append(",");
            value.append((String)map.get(key));
            value.append("','");
        }

        if (name.length() >= 1) {
            name.deleteCharAt(name.length() - 1);
        }

        if (value.length() >= 3) {
            value.delete(value.length() - 3, value.length());
        }

        String var10001 = table.toUpperCase();
        return this.runCommandGetId("INSERT INTO " + var10001 + " (" + String.valueOf(name) + ") VALUES ('" + String.valueOf(value) + "');");
    }

    public Map<String, String> getMap(Map<String, String> map, String command) {
        ResultSet rs = null;

        try {
            rs = this.connection.createStatement().executeQuery(command);
            if (!rs.isClosed()) {
                ResultSetMetaData md = rs.getMetaData();

                for(int i = 1; i <= md.getColumnCount(); ++i) {
                    if (rs.getString(i) != null) {
                        map.put(md.getColumnName(i), rs.getString(i));
                    }
                }
            }
        } catch (Exception var14) {
            var14.printStackTrace();
        } finally {
            try {
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException var13) {
                var13.printStackTrace();
            }

        }

        return map;
    }

    public Map<String, String> findMap(String table, String name, String value) {
        Map<String, String> map = new HashMap();
        String command = "SELECT * FROM " + table.toUpperCase() + " WHERE " + name + " LIKE '" + value + "'";
        return this.getMap(map, command);
    }

    public Map<String, String> findMap(String table) {
        Map<String, String> map = new HashMap();
        String command = "SELECT * FROM " + table;
        return this.getMap(map, command);
    }

    public List<Map<String, String>> findList(String table, String name, String value) {
        return this.selectList("SELECT * FROM " + table.toUpperCase() + " WHERE " + name + " LIKE '" + value + "'");
    }

    public List<Map<String, String>> findList(String table) {
        return this.selectList("SELECT * FROM " + table.toUpperCase());
    }

    public List<Map<String, String>> selectList(String command) {
        List<Map<String, String>> list = new ArrayList();
        ResultSet rs = null;

        try {
            rs = this.connection.createStatement().executeQuery(command);

            while(true) {
                do {
                    if (!rs.next()) {
                        return list;
                    }
                } while(rs.isClosed());

                Map<String, String> map = new HashMap();
                ResultSetMetaData md = rs.getMetaData();

                for(int i = 1; i <= md.getColumnCount(); ++i) {
                    if (rs.getString(i) != null) {
                        map.put(md.getColumnName(i), rs.getString(i));
                    }
                }

                list.add(map);
            }
        } catch (Exception var15) {
            var15.printStackTrace();
        } finally {
            try {
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException var14) {
                var14.printStackTrace();
            }

        }

        return list;
    }

    public void update(String table, String id, String key, String value) {
        this.runCommand("UPDATE " + table.toUpperCase() + " SET " + key + " = '" + value + "' WHERE ID=" + id);
    }

    public void update(String table, String id, String value) {
        this.runCommand("UPDATE " + table.toUpperCase() + " SET " + value + " = null WHERE ID=" + id);
    }

    public void delete(String table, String id) {
        String var10001 = table.toUpperCase();
        this.runCommand("DELETE FROM " + var10001 + " WHERE ID=" + id);
    }

    public void runCommand(String command) {
        try {
            this.connection.createStatement().executeUpdate(command);
        } catch (SQLException var3) {
            if (!command.contains("CREATE TABLE")) {
                var3.printStackTrace();
            }
        }

    }

    public int runCommandGetId(String command) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(command, 1);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException var5) {
            var5.printStackTrace();
        }

        return -1;
    }
}
