import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DbAcess {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        ConnectionSQL con = new ConnectionSQL();
        String[][] data = con.writeTable();
        String[] columnNames = {"Nazwa", "Cena"};
        final JTable[] table = {new JTable(data, columnNames)};
        final JScrollPane[] scrollPane = {new JScrollPane(table[0])};
        JButton changeName = new JButton("Zmien nazwe produktu");
        JButton changePrice = new JButton("Zmien cenę produktu");
        JButton addProduct = new JButton("Wstaw nowy produkt");
        JButton deleteProduct = new JButton("Usuń produkt");
        JButton findProductByName = new JButton("Wyszukaj produkt po nazwie");
        JButton findProductByPrice = new JButton("Wyszukaj produkt po cenie");
        JFrame frame = new JFrame("Baza danych");
        JPanel panel = new JPanel();
        panel.add(changeName);
        panel.add(changePrice);
        panel.add(addProduct);
        panel.add(deleteProduct);
        panel.add(findProductByName);
        panel.add(findProductByPrice);
        panel.add(scrollPane[0]);
        frame.add(panel);
        frame.setMinimumSize(new Dimension(640, 640));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        changeName.addActionListener(e -> {
            panel.remove(scrollPane[0]);
            JScrollPane scrollPaneUpdate = new JScrollPane(table[0]);
            JTable tableUpdate = new JTable(data, columnNames);
            try {
                int row = table[0].getSelectedRow();
                Object oldPrice = table[0].getValueAt(row, 1);
                con.updateName(oldPrice);
                String[][] dataUpdate = con.writeTable();
                tableUpdate = new JTable(dataUpdate, columnNames);
                scrollPaneUpdate = new JScrollPane(tableUpdate);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            table[0] = tableUpdate;
            scrollPane[0] = scrollPaneUpdate;
            panel.add(scrollPane[0]);
            panel.updateUI();
            frame.repaint();
        });

        changePrice.addActionListener(e -> {
            panel.remove(scrollPane[0]);
            JScrollPane scrollPaneUpdate = new JScrollPane(table[0]);
            JTable tableUpdate = new JTable(data, columnNames);
            try {
                int row = table[0].getSelectedRow();
                Object oldName = table[0].getValueAt(row, 0);
                con.updatePrice(oldName);
                String[][] dataUpdate = con.writeTable();
                tableUpdate = new JTable(dataUpdate, columnNames);
                scrollPaneUpdate = new JScrollPane(tableUpdate);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            table[0] = tableUpdate;
            scrollPane[0] = scrollPaneUpdate;
            panel.add(scrollPane[0]);
            panel.updateUI();
            frame.repaint();
        });

        deleteProduct.addActionListener(e -> {
            panel.remove(scrollPane[0]);
            JScrollPane scrollPaneUpdate = new JScrollPane(table[0]);
            JTable tableUpdate = new JTable(data, columnNames);
            try {
                int row = table[0].getSelectedRow();
                Object oldName = table[0].getValueAt(row, 0);
                Object oldPrice = table[0].getValueAt(row, 1);
                con.removeExisting(oldName, oldPrice);
                String[][] dataUpdate = con.writeTable();
                tableUpdate = new JTable(dataUpdate, columnNames);
                scrollPaneUpdate = new JScrollPane(tableUpdate);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            table[0] = tableUpdate;
            scrollPane[0] = scrollPaneUpdate;
            panel.add(scrollPane[0]);
            panel.updateUI();
            frame.repaint();
        });

        addProduct.addActionListener(e -> {
            panel.remove(scrollPane[0]);
            JScrollPane scrollPaneUpdate = new JScrollPane(table[0]);
            JTable tableUpdate = new JTable(data, columnNames);
            try {
                con.insertNew();
                String[][] dataUpdate = con.writeTable();
                tableUpdate = new JTable(dataUpdate, columnNames);
                scrollPaneUpdate = new JScrollPane(tableUpdate);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            table[0] = tableUpdate;
            scrollPane[0] = scrollPaneUpdate;
            panel.add(scrollPane[0]);
            panel.updateUI();
            frame.repaint();
        });

        findProductByName.addActionListener(e -> {
            try {
                String findName = JOptionPane.showInputDialog("Wprowadź nazwę szukanego produktu");
                con.searchName(findName);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        findProductByPrice.addActionListener(e -> {
            try {
                double findPrice = Double.parseDouble(JOptionPane.showInputDialog("Wprowadź cenę szukanego produktu"));
                con.searchPrice(findPrice);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static class ConnectionSQL {
        Statement stmt;
        ResultSet result;
        ResultSetMetaData meta;

        public ConnectionSQL () throws ClassNotFoundException, SQLException {
            String url = "jdbc:sqlite:baza_p.db3";
            Class.forName("org.sqlite.JDBC");
            Connection con = DriverManager.getConnection(url);
            this.stmt = con.createStatement();
        }
        public String[][] writeTable () throws SQLException {
            result = stmt.executeQuery("select * from PRODUKT");
            meta = result.getMetaData();
            String[][] data = new String[100][meta.getColumnCount()];
            int j = 0;
            while (result.next()) {
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    data[j][i] = result.getString(i+1);
                }
                j++;
                System.out.println();
            }
            return data;
        }

        public void searchName (Object name) throws SQLException {
            result = stmt.executeQuery("select * from PRODUKT");
            meta = result.getMetaData();
            while (result.next()) {
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    if (result.getString(i + 1).equalsIgnoreCase((String) name)) {
                        JOptionPane.showMessageDialog(null, "Znaleziono: " + result.getString("nazwa") +
                                " " + result.getString("cena") + "zł.");
                    }
                }
            }
        }

        public void searchPrice (double price) throws SQLException {
            result = stmt.executeQuery("select * from PRODUKT");
            meta = result.getMetaData();
            while (result.next()) {
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    if (result.getDouble(i + 1) == price) {
                        JOptionPane.showMessageDialog(null, "Znaleziono: " + result.getString("nazwa") +
                                " " + result.getString("cena") + "zł.");
                    }
                }
            }
        }

        public void insertNew () throws SQLException {
            String name;
            double price;
            name = JOptionPane.showInputDialog("Wprowadź nazwę");
            price = Double.parseDouble(JOptionPane.showInputDialog("Wprowadź cenę"));
            if (!name.isBlank() && price > 0.00) {
                String query = "insert into PRODUKT values ('" + name + "', '" + price + "') ";
                stmt.executeUpdate(query);
            }
        }

        public void removeExisting (Object oldName, Object oldPrice) throws SQLException {
            String query = "delete from PRODUKT where nazwa = '"+oldName+"' and cena = '"+oldPrice+"'";
            stmt.executeUpdate(query);
        }

        public void updateName (Object oldPrice) throws SQLException {
            String newName = JOptionPane.showInputDialog("Wprowadź nową nazwę");
            result = stmt.executeQuery("select nazwa from PRODUKT");
            String query = "update PRODUKT set nazwa = '"+newName+"' where cena = '"+oldPrice+"'";
            stmt.executeUpdate(query);
        }

        public void updatePrice (Object oldName) throws SQLException {
            double priceNew = Double.parseDouble(JOptionPane.showInputDialog("Wprowadź nową cenę"));
            result = stmt.executeQuery("select cena from PRODUKT");
            String query = "update PRODUKT set cena = '"+priceNew+"' where nazwa = '"+oldName+"'";
            stmt.executeUpdate(query);
        }
    }
}