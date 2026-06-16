package pe.edu.pe.pucp.proyecto.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBManager {
    private static DBManager instance;
    private final HikariDataSource dataSource;
    private static final String DB_CREDENTIALS_FILE = "db.properties";

    private DBManager() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(DB_CREDENTIALS_FILE)) {
            properties.load(inputStream);
        } catch (IOException ex) {
            throw new RuntimeException("Error al cargar " + DB_CREDENTIALS_FILE + ": " + ex.getMessage(), ex);
        }

        String host = properties.getProperty("host");
        String port = properties.getProperty("port");
        String database = properties.getProperty("database");
        // Mismos parámetros de antes (para que la BD lea tildes).
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(properties.getProperty("user"));
        config.setPassword(properties.getProperty("password"));
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000); // 30s esperando una conexión libre
        config.setPoolName("BunkiPool");

        this.dataSource = new HikariDataSource(config);
    }

    public static synchronized DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    // Devuelve una conexión DEL POOL. Los DAO la cierran con try-with-resources,
    // lo que la devuelve al pool (no la cierra de verdad).
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
