import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SQL {

    public static Connection connect(String jdbcUrl, String benutzername, String passwort) {
        try {
            // Laden Sie den JDBC-Treiber
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Stellen Sie die Verbindung zur Datenbank her
            Connection verbindung = DriverManager.getConnection(jdbcUrl, benutzername, passwort);
            System.out.println("Successfully connected to: " + verbindung.toString());

            return verbindung;

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Not connected to Database. Error :" + e.getMessage() +" occurred.");
            return null;
        }
    }

    public static void disconnect(Connection verbindung) throws SQLException {
        // Schließen Sie die Verbindung, wenn Sie fertig sind
        try {
            verbindung.close();
            System.out.println("Successfully disconnected.");
        } catch (SQLException e) {
            System.out.println("Could not disconnect. Error: " + e.getMessage() + " occurred.");
        }
    }

    public static ResultSet sqlAbfrage(String sql, Connection con, boolean print)  {
        //Vorbereiten der Abfrage
        PreparedStatement prompt = null;
        try {
            prompt = con.prepareStatement(sql);
        } catch (SQLException e) {
            System.out.println("Error: Could not parse request. Error: "+ e.getMessage() + " occurred.");
        }

        //Durchführen der Abfrage
        ResultSet ergebnisse = null;
        try {
            ergebnisse = prompt.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error: Could not execute request. Error: "+ e.getMessage() + " occurred.");
        }

        //Ausgabe
        if(print)   {
            try{

                //Ausgabe
                Object[][] t = SQL.resultSetToMatrix(ergebnisse);
                Main.ausgabeMatrix(t);

                //Schließen
                try {
                    prompt.close();
                } catch (SQLException e) {
                    System.out.println("Warning: Could not close preparedStatement. Error: "+ e.getMessage() + " occurred.");
                }
                ergebnisse.close();
            } catch(Exception e)    {
                System.out.println("Warning: Could not print out results. Error: "+ e.getMessage() + " occurred.");
            }
        }
        return ergebnisse;
    }

    public static void insertIntoTabelle(Connection verbindung, String tabellenName, Map<String, Object> spaltenWerte) {
        try {
            // SQL-INSERT-Anweisung vorbereiten
            StringBuilder sqlInsert = new StringBuilder("INSERT INTO ").append(tabellenName).append(" (");

            for (String spaltenName : spaltenWerte.keySet()) {
                sqlInsert.append(spaltenName).append(", ");
            }

            sqlInsert.delete(sqlInsert.length() - 2, sqlInsert.length()); // Letzte Komma löschen
            sqlInsert.append(") VALUES (");

            for (int i = 0; i < spaltenWerte.size(); i++) {
                sqlInsert.append("?, ");
            }

            sqlInsert.delete(sqlInsert.length() - 2, sqlInsert.length()); // Letzte Komma löschen
            sqlInsert.append(")");

            // Vorbereitetes Statement erstellen
            PreparedStatement vorbereiteteEinfuegung = verbindung.prepareStatement(sqlInsert.toString());

            // Werte für die Parameter setzen
            int parameterIndex = 1;
            for (Object wert : spaltenWerte.values()) {
                vorbereiteteEinfuegung.setObject(parameterIndex++, wert);
            }

            // INSERT-Anweisung ausführen
            vorbereiteteEinfuegung.executeUpdate();

            System.out.println("Datensatz erfolgreich eingefügt.");

            // Ressourcen schließen
            vorbereiteteEinfuegung.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Object[][] resultSetToMatrix(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int spaltenAnzahl = metaData.getColumnCount();

        List<Object[]> matrixList = new ArrayList<>();

        while (resultSet.next()) {
            Object[] zeile = new Object[spaltenAnzahl];
            for (int i = 1; i <= spaltenAnzahl; i++) {
                zeile[i - 1] = resultSet.getObject(i);
            }
            matrixList.add(zeile);
        }

        // Konvertieren der List zu einer zweidimensionalen Array
        return matrixList.toArray(new Object[0][]);
    }

    public static void loescheEintrag(Connection verbindung, String tabellenName, String spaltenName, Object zuLoeschenderWert) {
        try {
            // SQL-DELETE-Anweisung vorbereiten
            String sqlDelete = "DELETE FROM " + tabellenName + " WHERE " + spaltenName + " = ?";
            PreparedStatement vorbereiteteLoeschung = verbindung.prepareStatement(sqlDelete);

            // Wert für den Parameter setzen
            vorbereiteteLoeschung.setObject(1, zuLoeschenderWert);

            // DELETE-Anweisung ausführen
            int betroffeneZeilen = vorbereiteteLoeschung.executeUpdate();

            if (betroffeneZeilen > 0) {
                System.out.println("Datensatz erfolgreich gelöscht.");
            } else {
                System.out.println("Datensatz nicht gefunden oder konnte nicht gelöscht werden.");
            }

            // Ressourcen schließen
            vorbereiteteLoeschung.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void reader_EintragLoeschen(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bitte geben Sie im folgenden die Daten für den zu löschenden Eintrag ein!");

        System.out.print("Bitte geben Sie den Namen der Tabelle ein: ");
        String tableName = scanner.nextLine();

        System.out.print("Bitte geben Sie den Namen der Spalte ein (Hinweis: Die Spalte muss den Datentyp Integer haben): ");
        String row = scanner.nextLine();

        int value;
        do {
            try{

                System.out.print("Bitte geben Sie den Wert des Eintrags in der zuvor angegebenen Spalte ein: ");
                value = Integer.parseInt(scanner.nextLine());
                break;
            } catch (Exception e)   {
                System.out.println("Ihre Eingabe scheint nicht zu dem vorgegebenen Format zu passen. Bitte geben Sie den Wert erneut ein. Error: " +e.getMessage());
            }
        } while(true);

        loescheEintrag(conn, tableName, row, value);
    }

    public static void executeSqlStatement(Connection connection, String sqlCommand) {
        try (Statement statement = connection.createStatement()) {
            int rowsAffected = statement.executeUpdate(sqlCommand);

            if (rowsAffected > 0) {
                System.out.println("SQL-Befehl erfolgreich ausgeführt. " + rowsAffected + " Zeilen betroffen.");
            } else {
                System.out.println("SQL-Befehl ausgeführt, aber keine Zeilen betroffen.");
            }

        } catch (SQLException e) {
            System.err.println("Fehler beim Ausführen des SQL-Befehls: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
