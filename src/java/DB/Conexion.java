package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author carlosrodf
 */
public class Conexion {

    private final String host;
    private final String port;
    private final String db;
    private final String user;
    private final String password;
    private final String classForName;
    private final String driver;

    private final String url;

    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    public Conexion() {
        this.host = "localhost";
        //this.host = "ec2-54-191-108-207.us-west-2.compute.amazonaws.com";
        this.port = "5432";
        this.db = "profile";
        this.user = "postgres";
        this.password = "admin";
        this.classForName = "org.postgresql.Driver";
        this.driver = "jdbc:postgresql";

        this.url = driver + "://" + host + ":" + port + "/" + db;

        this.conn = null;
    }

    private Connection getConexion() {
        try {
            Class.forName(this.classForName);
            Connection conn = DriverManager.getConnection(this.url, this.user, this.password);
            return conn;
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public void ejecutarUpdate(String query) throws SQLException {
        conn = getConexion();

        conn.setAutoCommit(false);
        stmt = conn.createStatement();
        stmt.executeUpdate(query);
        stmt.close();
        conn.commit();
        conn.close();
    }

    public ResultSet ejecutarQuery(String query) throws SQLException {
        conn = getConexion();

        conn.setAutoCommit(false);
        stmt = conn.createStatement();
        rs = stmt.executeQuery(query);
        return rs;
    }

    public void insertarBitacora(String user, accion_log accion, String descripcion) {
        int acc = accion.ordinal() + 1;
        descripcion = descripcion.replace("\n", "");
        String query = "INSERT INTO bitacora(id_accion,fecha,id_usuario,descripcion) "
                + "VALUES(" + acc + ",CURRENT_TIMESTAMP," + user + ",'" + descripcion + "');";

        try {
            conn = getConexion();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
            close();
        } catch (SQLException ex) {
            System.out.println(query);
            System.out.println("BITACORA: " + ex.getMessage());
        }

    }

    public void close() {
        try {
            this.conn.commit();
            this.stmt.close();
            this.conn.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Error al cerrar objetos en clase Conexion.");
        }
    }

}
