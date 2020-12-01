import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Osoby {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        DBAcess con = new DBAcess();
        String[][] data = con.writeTable();
        String[] columnNames = {"ID", "Imię", "Nazwisko", "Ulica", "nr_lokalu", "plec", "kod_pocztowy", "miejscowosc", "województwo"};
        final JTable[] table = {new JTable(data, columnNames)};

        table[0].setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 1; i < table[0].getColumnCount(); i++)
            table[0].getColumnModel().getColumn(i).setPreferredWidth(130);

        table[0].setPreferredScrollableViewportSize(new Dimension(table[0].getPreferredSize().width, 600));

        final JScrollPane[] scrollPane = {new JScrollPane(table[0])};
        JButton addPerson = new JButton("Dodaj osobę");
        JButton changePerson = new JButton("Zmien dane osoby");
        JButton deletePerson = new JButton("Usuń osobę");
        JFrame frame = new JFrame("Baza danych");
        JPanel panel = new JPanel();

        scrollPane[0].getVerticalScrollBar().setPreferredSize(new Dimension(70, Integer.MAX_VALUE));

        panel.add(addPerson);
        panel.add(changePerson);
        panel.add(deletePerson);
        panel.add(scrollPane[0]);

        frame.add(panel);
        frame.setMinimumSize(new Dimension(1200, 700));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);


        changePerson.addActionListener(e -> {
            panel.remove(scrollPane[0]);
            JScrollPane scrollPaneUpdate = new JScrollPane(table[0]);
            JTable tableUpdate = new JTable(data, columnNames);
            try {
                int row = 0;
                if (table[0].getSelectedRow() >= 0) row = table[0].getSelectedRow();

                    Object oldId = table[0].getValueAt(row, 0);
                    Object oldName = table[0].getValueAt(row, 1);
                    Object oldSurname = table[0].getValueAt(row, 2);
                    Object oldStreet = table[0].getValueAt(row, 3);
                    Object oldLocation = table[0].getValueAt(row, 4);
                    Object oldSex = table[0].getValueAt(row, 5);
                    Object oldCode = table[0].getValueAt(row, 6);
                    con.updatePerson(oldId, oldName, oldSurname, oldStreet, oldLocation, oldSex, oldCode);
                    String[][] dataUpdate = con.writeTable();
                    tableUpdate = new JTable(dataUpdate, columnNames);
                    scrollPaneUpdate = new JScrollPane(tableUpdate);
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }

            table[0] = tableUpdate;
            tableUpdate.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            for (int i = 1; i < tableUpdate.getColumnCount(); i++)
                tableUpdate.getColumnModel().getColumn(i).setPreferredWidth(130);

            tableUpdate.setPreferredScrollableViewportSize(new Dimension(table[0].getPreferredSize().width, 600));
            scrollPane[0] = scrollPaneUpdate;
            scrollPane[0].getVerticalScrollBar().setPreferredSize(new Dimension(70, Integer.MAX_VALUE));
            panel.add(scrollPane[0]);
            panel.updateUI();
            frame.repaint();
        });


        deletePerson.addActionListener(e -> {
            panel.remove(scrollPane[0]);
            JScrollPane scrollPaneUpdate = new JScrollPane(table[0]);
            JTable tableUpdate = new JTable(data, columnNames);
            try {
                int row = 0;
                if (table[0].getSelectedRow() >= 0) row = table[0].getSelectedRow();

                Object oldName = table[0].getValueAt(row, 1);
                Object oldSurname = table[0].getValueAt(row, 2);
                Object oldLocation = table[0].getValueAt(row, 4);
                con.removeExisting(oldName, oldSurname, oldLocation);
                String[][] dataUpdate = con.writeTable();
                tableUpdate = new JTable(dataUpdate, columnNames);
                scrollPaneUpdate = new JScrollPane(tableUpdate);
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }

            table[0] = tableUpdate;
            tableUpdate.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            for (int i = 1; i < tableUpdate.getColumnCount(); i++)
                tableUpdate.getColumnModel().getColumn(i).setPreferredWidth(130);

            tableUpdate.setPreferredScrollableViewportSize(new Dimension(table[0].getPreferredSize().width, 600));
            scrollPane[0] = scrollPaneUpdate;
            scrollPane[0].getVerticalScrollBar().setPreferredSize(new Dimension(70, Integer.MAX_VALUE));
            panel.add(scrollPane[0]);
            panel.updateUI();
            frame.repaint();
        });


        addPerson.addActionListener(e -> {
            panel.remove(scrollPane[0]);
            JScrollPane scrollPaneUpdate = new JScrollPane(table[0]);
            JTable tableUpdate = new JTable(data, columnNames);
            try {
                con.insertNew();
                String[][] dataUpdate = con.writeTable();
                tableUpdate = new JTable(dataUpdate, columnNames);
                scrollPaneUpdate = new JScrollPane(tableUpdate);
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
            table[0] = tableUpdate;
            tableUpdate.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            for (int i = 1; i < tableUpdate.getColumnCount(); i++)
                tableUpdate.getColumnModel().getColumn(i).setPreferredWidth(130);

            tableUpdate.setPreferredScrollableViewportSize(new Dimension(table[0].getPreferredSize().width, 600));
            scrollPane[0] = scrollPaneUpdate;
            scrollPane[0].getVerticalScrollBar().setPreferredSize(new Dimension(70, Integer.MAX_VALUE));
            panel.add(scrollPane[0]);
            panel.updateUI();
            frame.repaint();
        });
    }

    public static class DBAcess {
        Statement stmt;
        ResultSet result, res;
        ResultSetMetaData meta, met;

        public String[][] writeTable () throws SQLException, ClassNotFoundException {
            String url = "jdbc:sqlite:BAZA_OSOB.db3";
            Class.forName("org.sqlite.JDBC");
            Connection con = DriverManager.getConnection(url);
            this.stmt = con.createStatement();

            result = stmt.executeQuery("select OSOBA.id_osoby, OSOBA.imie, OSOBA.nazwisko, OSOBA.ulica, OSOBA.nr_lokalu, PLEC.plec, KOD_POCZTOWY.kod, MIEJSCOWOSC.nazwa_miejscowosci, WOJEWODZTWO.nazwa_wojewodztwa " +
                    "from OSOBA " +
                    "inner join PLEC " +
                    "on OSOBA.id_plci=PLEC.id_plci " +
                    "inner join KOD_POCZTOWY " +
                    "on OSOBA.id_kodu_pocztowego=KOD_POCZTOWY.id_kodu_pocztowego " +
                    "inner join MIEJSCOWOSC " +
                    "on KOD_POCZTOWY.id_miejscowosci=MIEJSCOWOSC.id_miejscowosci " +
                    "inner join WOJEWODZTWO " +
                    "on MIEJSCOWOSC.id_wojewodztwa=WOJEWODZTWO.id_wojewodztwa ");
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
            con.close();
            return data;
        }

        public void insertNew () throws ClassNotFoundException {

            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField surnameField = new JTextField();
            JTextField streetField = new JTextField();
            JTextField locationField = new JTextField();
            String[] sexItems = {"", "M", "K", "Helikopter_Szturmowy"};
            JComboBox<String> sexCombo = new JComboBox<>(sexItems);
            JTextField codeField = new JTextField();
            JPanel queryWindow = new JPanel(new GridLayout(0, 1));

            queryWindow.add(new JLabel("ID:"));
            queryWindow.add(idField);
            queryWindow.add(new JLabel("Imię:"));
            queryWindow.add(nameField);
            queryWindow.add(new JLabel("Nazwisko:"));
            queryWindow.add(surnameField);
            queryWindow.add(new Label("Ulica:"));
            queryWindow.add(streetField);
            queryWindow.add(new JLabel("Numer lokalu"));
            queryWindow.add(locationField);
            queryWindow.add(new JLabel("Płeć"));
            queryWindow.add(sexCombo);
            queryWindow.add(new JLabel("Kod pocztowy"));
            queryWindow.add(codeField);

            int result = JOptionPane.showConfirmDialog(null, queryWindow, "Dodaj osobę", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int id;
                    String name, surname, street, location, sex, code;
                    if (!(Integer.parseInt(idField.getText()) == 0 &&
                            nameField.getText().equals("") &&
                            surnameField.getText().equals("") &&
                            streetField.getText().equals("") &&
                            locationField.getText().equals("") &&
                            sexCombo.getSelectedItem().toString().equals("") &&
                            codeField.getText().equals(""))) {

                        id = Integer.parseInt(idField.getText());
                        name = nameField.getText();
                        surname = surnameField.getText();
                        street = streetField.getText();
                        location = locationField.getText();
                        sex = sexCombo.getSelectedItem().toString();
                        code = codeField.getText();

                        String url = "jdbc:sqlite:BAZA_OSOB.db3";
                        Class.forName("org.sqlite.JDBC");
                        Connection con = DriverManager.getConnection(url);
                        this.stmt = con.createStatement();

                        String query = "select id_kodu_pocztowego from KOD_POCZTOWY where kod = '" + code + "'";
                        res = stmt.executeQuery(query);
                        int codeId = 0;
                        met = res.getMetaData();

                        while (res.next()) {
                            for (int i = 0; i < met.getColumnCount(); i++) {
                                codeId = Integer.parseInt(res.getString(i + 1));
                            }
                            System.out.println();
                        }

                        query = "select id_plci from PLEC where plec = '" + sex + "'";
                        res = stmt.executeQuery(query);
                        met = res.getMetaData();

                        int idSex = 0;
                        while (res.next()) {
                            for (int i = 0; i < met.getColumnCount(); i++) {
                                idSex = Integer.parseInt(res.getString(i + 1));
                            }
                            System.out.println();
                        }

                        query = "insert into OSOBA values (" + id + ", '" + name + "', '" + surname + "', '" + street + "', '" + location + "', " + idSex + ", " + codeId + ")";
                        stmt.executeUpdate(query);

                        con.close();
                    }
                }
                catch (SQLException s){
                    JOptionPane.showMessageDialog(null, "Błąd zapytania SQL");
                }
            } else {
                System.out.println("Cancelled");
            }
        }

        public void removeExisting (Object oldName, Object oldSurname, Object oldLocation) throws SQLException, ClassNotFoundException {
            String url = "jdbc:sqlite:BAZA_OSOB.db3";
            Class.forName("org.sqlite.JDBC");
            Connection con = DriverManager.getConnection(url);
            this.stmt = con.createStatement();

            String query = "delete from OSOBA where imie = '" + oldName + "' and nazwisko = '" + oldSurname + "' and nr_lokalu = '" + oldLocation + "' ";
            stmt.executeUpdate(query);

            con.close();
        }

        public void updatePerson (Object oldId, Object oldName, Object oldSurname, Object oldStreet, Object oldLocation, Object oldSex, Object oldCode) throws ClassNotFoundException {

            JTextField nameField = new JTextField(oldName.toString());
            JTextField surnameField = new JTextField(oldSurname.toString());
            JTextField streetField = new JTextField(oldStreet.toString());
            JTextField locationField = new JTextField(oldLocation.toString());
            String[] sexItems = {"", "M", "K", "Helikopter_Szturmowy"};
            JComboBox<String> sexCombo = new JComboBox<>(sexItems);
            if(oldSex.toString().equals("M")) sexCombo.setSelectedIndex(1);
            else if(oldSex.toString().equals("K")) sexCombo.setSelectedIndex(2);
            else sexCombo.setSelectedIndex(3);
            JTextField codeField = new JTextField(oldCode.toString());
            JPanel queryWindow = new JPanel(new GridLayout(0, 1));

            queryWindow.add(new JLabel("ID: " + oldId));
            queryWindow.add(new JLabel("Imię:"));
            queryWindow.add(nameField);
            queryWindow.add(new JLabel("Nazwisko:"));
            queryWindow.add(surnameField);
            queryWindow.add(new Label("Ulica:"));
            queryWindow.add(streetField);
            queryWindow.add(new JLabel("Numer lokalu"));
            queryWindow.add(locationField);
            queryWindow.add(new JLabel("Płeć"));
            queryWindow.add(sexCombo);
            queryWindow.add(new JLabel("Kod pocztowy"));
            queryWindow.add(codeField);

            int result = JOptionPane.showConfirmDialog(null, queryWindow, ("Zmiana danych osoby o ID:" + oldId), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String name, surname, street, location, sex, code;
                    if (!(nameField.getText().equals("") &&
                            surnameField.getText().equals("") &&
                            streetField.getText().equals("") &&
                            locationField.getText().equals("") &&
                            sexCombo.getSelectedItem().toString().equals("") &&
                            codeField.getText().equals(""))) {

                        name = nameField.getText();
                        surname = surnameField.getText();
                        street = streetField.getText();
                        location = locationField.getText();
                        sex = sexCombo.getSelectedItem().toString();
                        code = codeField.getText();

                        String url = "jdbc:sqlite:BAZA_OSOB.db3";
                        Class.forName("org.sqlite.JDBC");
                        Connection con = DriverManager.getConnection(url);
                        this.stmt = con.createStatement();

                        String query = "select id_kodu_pocztowego from KOD_POCZTOWY where kod = '" + code + "'";
                        res = stmt.executeQuery(query);
                        int codeId = 0;
                        met = res.getMetaData();

                        while (res.next()) {
                            for (int i = 0; i < met.getColumnCount(); i++) {
                                codeId = Integer.parseInt(res.getString(i + 1));
                            }
                            System.out.println();
                        }

                        query = "select id_plci from PLEC where plec = '" + sex + "'";
                        res = stmt.executeQuery(query);
                        met = res.getMetaData();

                        int idSex = 0;
                        while (res.next()) {
                            for (int i = 0; i < met.getColumnCount(); i++) {
                                idSex = Integer.parseInt(res.getString(i + 1));
                            }
                            System.out.println();
                        }

                        query = "update OSOBA " +
                                "set imie = '" + name + "', " +
                                "nazwisko = '" + surname + "', " +
                                "ulica = '" + street + "', " +
                                "nr_lokalu = '" + location + "', " +
                                "id_plci = '" + idSex + "', " +
                                "id_kodu_pocztowego = '" + codeId + "' " +
                                "where id_osoby = " + oldId;
                        stmt.executeUpdate(query);

                        con.close();
                    }
                }
                catch (SQLException s){
                    JOptionPane.showMessageDialog(null, "Błąd zapytania SQL");
                }
            } else {
                System.out.println("Cancelled");
            }
        }
    }
}
