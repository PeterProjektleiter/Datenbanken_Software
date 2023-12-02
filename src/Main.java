import javax.sound.midi.SysexMessage;
import javax.xml.transform.Result;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        //Verbindung herstellen
        System.out.println("Verbindung zur Datenbank wird hergestellt...");
        Connection conn = SQL.connect("jdbc:mysql://db4free.net:3306/koschnik", "peterschroeder", "3J57bfHOEbtOxXij");
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println("Was möchten Sie tun? [R] Einträge abrufen \t [D] Eintrag löschen \t [W] Eintrag ergänzen \t [AZ] Arbeitszeit eintragen \t [Q] Schließen");
            String action = sc.nextLine();
            action = action.toUpperCase();
            switch (action) {

                //Abruf-Menü
                case "R": {
                    System.out.println("Welche Tabelle möchten Sie abrufen? [M] Mitarbeiter \t [AZ] Arbeitszeit \t [AW] Abwesenheit \t [P] Projekt \t [MP] Mitarbeiter_Projekt \t [C] SQL-Eingabe \t [Q] Zurück");
                    String abr_action = sc.nextLine();
                    abr_action = abr_action.toUpperCase();
                    switch (abr_action) {
                        case "M":
                            SQL.sqlAbfrage("SELECT * FROM mitarbeiter", conn, true); break;
                        case "AZ":
                            SQL.sqlAbfrage("SELECT * FROM arbeitszeit", conn, true); break;
                        case "AW":
                            SQL.sqlAbfrage("SELECT * FROM abwesenheit", conn, true); break;
                        case "P":
                            SQL.sqlAbfrage("SELECT * FROM projekt", conn, true); break;
                        case "MP":
                            SQL.sqlAbfrage("SELECT * FROM mitarbeiter_projekt", conn, true); break;
                        case "C": {
                            System.out.print("Bitte geben Sie den korrekten SQL-Befehl ein: ");
                            String sql = sc.nextLine();
                            System.out.println(sql);
                            SQL.sqlAbfrage(sql, conn, true);
                            break;
                        }
                        case "Q":
                            break;
                        default:
                            System.out.println("Falsche Eingabe. Bitte versuchen Sie es erneut.");
                            break;
                    }
                    break;
                }

                //Löschen-Menü
                case "D":
                    SQL.reader_EintragLoeschen(conn);
                    break;

                //Eintrag erstellen-Menü TODO
                case "W": {
                    System.out.println("Welche Tabelle möchten Sie bearbeiten? [M] Mitarbeiter \t [AZ] Arbeitszeit \t [AW] Abwesenheit \t [P] Projekt \t [MP] Mitarbeiter_Projekt \t [C] SQL-Eingabe \t [Q] Zurück");
                    String abr_action = sc.nextLine();
                    abr_action = abr_action.toUpperCase();
                    switch (abr_action) {
                        case "M":
                            Template.reader_mitarbeiter(conn); break;
                        case "AZ":
                            Template.reader_arbeitszeit(conn); break;
                        case "AW":
                            System.out.println("Die Entwickler waren leider zu faul."); break;
                        case "P":
                            Template.reader_projekt(conn); break;
                        case "MP":
                            System.out.println("Die Entwickler waren leider zu faul."); break;
                        case "C": {
                            System.out.print("Bitte geben Sie den korrekten SQL-Befehl ein: ");
                            String sql = sc.nextLine();
                            SQL.executeSqlStatement(conn, sql);
                            break;
                        }
                        case "Q":
                            break;
                        default:
                            System.out.println("Falsche Eingabe. Bitte versuchen Sie es erneut.");
                            break;
                    }
                    break;
                }

                //Direkt zu Arbeitszeit eintragen
                case "AZ": Template.reader_arbeitszeit(conn); break;

                //Schließen
                case "Q": System.exit(0); break;

                //Quatsch-Eingabe
                default:
                    System.out.println("Falsche Eingabe. Bitte versuchen Sie es erneut.");
                    break;

            }
        } while (true);
    }

    //Methoden
    public static void ausgabeMatrix(Object[][] matrix) {
        for (Object[] zeile : matrix) {
            for (Object zelle : zeile) {
                System.out.print(zelle + "\t \t");
            }
            System.out.println();
        }
    }

    public static int parseHandlerInt(String message) {
        Scanner sc = new Scanner(System.in);
        int ausg;
        do {
            try {
                System.out.print(message);
                ausg = Integer.parseInt(sc.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("Ihre Eingabe scheint nicht zu dem vorgegebenen Format zu passen. Bitte geben Sie den Wert erneut ein. Error: " + e.getMessage());
            }
        } while (true);
        return ausg;
    }

    public static Date parseHandlerDate(String message) {
        Scanner sc = new Scanner(System.in);
        Date ausg;
        do {
            try {
                System.out.print(message);
                ausg = Date.valueOf(sc.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("Ihre Eingabe scheint nicht zu dem vorgegebenen Format zu passen. Bitte geben Sie den Wert erneut ein. Error: " + e.getMessage());
            }
        } while (true);
        return ausg;
    }

    public static String parseHandlerUhrzeit(String message) {
        String uhrzeit;
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.print(message);
            String uhrzeitEingabe = scanner.nextLine();

            try {
                // SimpleDateFormat für das Eingabeformat erstellen
                SimpleDateFormat eingabeFormat = new SimpleDateFormat("HH:mm:ss");

                // SimpleDateFormat für das Ausgabeformat erstellen
                SimpleDateFormat ausgabeFormat = new SimpleDateFormat("HH:mm:ss");

                // Uhrzeit parsen und in das gewünschte Ausgabeformat umwandeln
                uhrzeit = ausgabeFormat.format(eingabeFormat.parse(uhrzeitEingabe));
                break;
            } catch (ParseException e) {
                System.err.println("Ungültiges Uhrzeitformat. Verwenden Sie das Format HH:mm:ss.");
            }
        } while (true);
        return uhrzeit;
    }

    public static String berechneUndFormatiereZeitdauer(String startZeit, String endZeit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Uhrzeiten in LocalTime-Objekte konvertieren
        LocalTime start = LocalTime.parse(startZeit, formatter);
        LocalTime end = LocalTime.parse(endZeit, formatter);

        // Zeitdauer zwischen den beiden Uhrzeiten berechnen
        Duration duration = Duration.between(start, end);

        // Zeitdauer in Stunden, Minuten und Sekunden aufteilen
        long stunden = duration.toHours();
        long minuten = duration.toMinutesPart();
        long sekunden = duration.toSecondsPart();

        // Zeitdauer im Format "HH:MM:SS" zurückgeben
        return String.format("%02d:%02d:%02d", stunden, minuten, sekunden);
    }
}