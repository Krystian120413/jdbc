import java.sql.*;

public class ConnectToSQLLite {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String url = "jdbc:sqlite:BAZA_PRODUKTOW.db3";
        String user = "root";
        String password = "admin123";
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection(url, user, password);
        Statement stmt = con.createStatement();
        ResultSet result = stmt.executeQuery("select * from PRODUKT");
        ResultSetMetaData meta = result.getMetaData();

        for (int i = 0; i < meta.getColumnCount(); i++){
            System.out.print(meta.getColumnName(i+1) + " ");
        }
        System.out.println();

        while (result.next()){
            for (int i = 0; i < meta.getColumnCount(); i++){
                System.out.print(result.getString(i+1) + " ");
            }
            System.out.println();
        }
    }
}
