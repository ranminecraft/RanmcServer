package cc.ranmc.entries;

import cc.ranmc.util.Logger;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLite {

    private Connection connection;

    public SQLite(String file) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+ file);
        } catch (Exception e) {
            Logger.info(e.getMessage());
        }
    }

    /**
     * 关闭数据库连接
     */
    public void close() {
        try {
            if(connection != null && !connection.isClosed()) connection.close();
        } catch (Exception e) {
            Logger.info(e.getMessage());
        }
    }

    /**
     * 新增数据库
     * @param table 表
     * @param name 名称
     * @param value 值
     */
    public int insert(String table, String name, String value) {
        return runCommandGetId("INSERT INTO " + table.toUpperCase() + " ("+name+") VALUES ('" + value.replace(",","','") + "');");
    }

    public int insert(String table, Map<String,String> map) {
        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();
        for (String key : map.keySet()) {
            name.append(key);
            name.append(",");
            value.append(map.get(key));
            value.append("','");
        }
        if (!name.isEmpty()) name.deleteCharAt(name.length() - 1);
        if (value.length() >= 3) value.delete(value.length() - 3, value.length());
        return runCommandGetId("INSERT INTO " + table.toUpperCase() + " ("+name+") VALUES ('" + value + "');");
    }

    /**
     * 分析数据
     * @param map 数据
     * @param command 命令
     * @return 数据
     */
    @Nullable
    private Map<String, String> getMap(Map<String, String> map, String command) {
        ResultSet rs = null;
        try {
            rs = connection.createStatement().executeQuery(command);
            if(!rs.isClosed()) {
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    if (rs.getString(i) != null) {
                        map.put(md.getColumnName(i), rs.getString(i));
                    }
                }
            }
        } catch (Exception e) {
            Logger.info(e.getMessage());
        } finally {
            try {
                if (rs != null && !rs.isClosed()) rs.close();
            } catch (SQLException e) {
                Logger.info(e.getMessage());
            }
        }
        return map;
    }

    /**
     * 查询表数据
     * @param table 表
     * @param name 名称
     * @return 数据
     */
    public Map<String, String> findMap(String table, String name, String value) {
        Map<String, String> map = new HashMap<>();
        String command = "SELECT * FROM " + table.toUpperCase() + " WHERE " + name + " LIKE '" + value + "'";
        return getMap(map, command);
    }

    public Map<String, String> findMap(String table) {
        Map<String, String> map = new HashMap<>();
        String command = "SELECT * FROM " + table;
        return getMap(map, command);
    }

    public List<Map<String, String>> findList(String table, String name, String value) {
        return selectList("SELECT * FROM " + table.toUpperCase() + " WHERE " + name + " LIKE '" + value + "'");
    }

    public List<Map<String, String>> findList(String table) {
        return selectList("SELECT * FROM " + table.toUpperCase());
    }

    public List<Map<String, String>> selectList(String command) {
        List<Map<String, String>> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = connection.createStatement().executeQuery(command);
            while (rs.next()) {
                if(!rs.isClosed()) {
                    Map<String, String> map = new HashMap<>();
                    ResultSetMetaData md = rs.getMetaData();
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        if (rs.getString(i) != null) {
                            map.put(md.getColumnName(i), rs.getString(i));
                        }
                    }
                    list.add(map);
                }
            }
        } catch (Exception e) {
            Logger.info(e.getMessage());
        } finally {
            try {
                if (rs != null && !rs.isClosed()) rs.close();
            } catch (SQLException e) {
                Logger.info(e.getMessage());
            }
        }
        return list;
    }

    /**
     * 更新表数据
     * @param table 表
     * @param id 编号
     * @param key 数据名
     * @param value 值
     */
    public void update(String table,String id, String key, String value) {
        runCommand("UPDATE " + table.toUpperCase() + " SET " + key + " = '" + value + "' WHERE ID=" + id);
    }

    public void update(String table, String id, String value) {
        runCommand("UPDATE " + table.toUpperCase() + " SET " + value + " = null WHERE ID=" + id);
    }

    /**
     * 删除表数据
     * @param table 表
     * @param id 编号
     */
    public void delete(String table,String id) {
        runCommand("DELETE FROM "+table.toUpperCase()+" WHERE ID="+id);
    }

    /**
     * 执行数据库指令
     * @param command 命令
     */
    public void runCommand(String command) {
        try {
            connection.createStatement().executeUpdate(command);
        } catch (SQLException e) {
            if (!command.contains("CREATE TABLE")) {
                Logger.info(e.getMessage());
            }
        }
    }

    public int runCommandGetId(String command) {
        try {
            PreparedStatement statement = connection.prepareStatement(command, Statement.RETURN_GENERATED_KEYS);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            Logger.info(e.getMessage());
        }
        return -1;
    }

}
