import java.sql.Connection;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Template {

    public static void reader_mitarbeiter(Connection conn) {
        //Initializierung
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bitte geben Sie im folgenden die Daten für den neu anzulegenden Mitarbeiter ein!");

        //Datenaufnahme
        System.out.print("Bitte geben Sie den Namen des neuen Mitarbeiters ein: ");
        String name = scanner.nextLine();

        System.out.print("Bitte geben Sie den Vornamen des neuen Mitarbeiters ein: ");
        String vorname = scanner.nextLine();

        int urlaubstage = Main.parseHandlerInt("Bitte geben Sie die Anzahl der Urlaubstage des neuen Mitarbeiters ein (int): ");

        int wochenstunden = Main.parseHandlerInt("Bitte geben Sie die Anzahl der Wochenstunden des neuen Mitarbeiters ein (int): ");

        System.out.print("Bitte legen Sie ein Passwort für den neuen Mitarbeiters fest: ");
        String passwort = scanner.nextLine();

        System.out.print("Bitte geben Sie die Funktion des neuen Mitarbeiters an: ");
        String funktion = scanner.nextLine();

        Date date = Main.parseHandlerDate("Bitte geben Sie das Einstelldatum des Mitarbeiters im Format YYYY-MM-DD ein: ");

        //Ausführung
        SQL.insertIntoTabelle(conn, "mitarbeiter", Template.mitarbeiter(name, vorname, urlaubstage, wochenstunden, passwort, funktion, date));
    }

    public static void reader_projekt(Connection conn) {
        //Initializierung
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bitte geben Sie im folgenden die Daten für das neue Projekt ein!");

        //Datenaufnahme
        System.out.print("Bitte geben Sie den Namen des neuen Projekts ein: ");
        String name = scanner.nextLine();

        int budget = Main.parseHandlerInt("Bitte geben Sie das Budget ein: ");

        int minMA = Main.parseHandlerInt("Bitte geben Sie die minimal vorzuhaltende Anzahl an Mitarbeitenden ein: ");

        SQL.insertIntoTabelle(conn, "projekt", projekt(name, budget, minMA));
    }

    public static void reader_arbeitszeit(Connection conn) {
        //Initializierung
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bitte geben Sie im folgenden die Daten für die einzutragende Arbeitszeit ein!");

        //Datenaufnahme
        String begin = Main.parseHandlerUhrzeit("Um welche Uhrzeit haben Sie angefangen zu arbeiten? (Format HH:mm:ss): ");

        String end = Main.parseHandlerUhrzeit("Um welche Uhrzeit haben Sie aufgehört zu arbeiten? (Format HH:mm:ss): ");

        Date datum = Main.parseHandlerDate("Auf welches Datum bezieht sich dieser Eintrag? ");

        int maId = Main.parseHandlerInt("Wie lautet Ihre Mitarbeiter-ID? ");

        int projektId = Main.parseHandlerInt("Wie lautet die zugehörige Projekt-ID? ");

        String typ = "Standard";
        int i = Main.parseHandlerInt("Welcher Art ist dieser AZ-Eintrag? [1] Standard [2] Dienstreise [3] Fortbildung [4] Sonstige Freistellung ");
        switch (i){
            case 1: break;
            case 2: typ="Dienstreise"; break;
            case 3: typ="Fortbildung"; break;
            case 4: typ="Freistellung"; break;
        }
        SQL.insertIntoTabelle(conn, "arbeitszeit", arbeitszeit(maId, projektId, begin, end, datum, typ));
    }
    public static Map mitarbeiter(String name, String vorname, int urlaubstage, int wochenstunden, String passwort, String funktion, Date einstellungsdatum)    {
        Map<String, Object> spaltenWerte = Map.of(
                "name", name,
                "vorname", vorname,
                "urlaubstage", urlaubstage,
                "wochenstunden", wochenstunden,
                "passwort", passwort,
                "funktion", funktion,
                "einstellungsdatum", einstellungsdatum
        );
        return spaltenWerte;
    }

    public static Map projekt(String name, int budget, int minMA)   {
        Map<String, Object> spaltenWerte = Map.of(
                "name", name,
                "budget", budget,
                "minMA", minMA
        );
        return spaltenWerte;
    }

    public static Map arbeitszeit(int maId, int projektId, String begin, String end, Date datum, String typ)  {
        Map<String, Object> spaltenWerte = Map.of(
                "begin", begin,
                "end", end,
                "duration", Main.berechneUndFormatiereZeitdauer(begin, end),
                "datum", datum,
                "mitarbeiterid", maId,
                "projektid", projektId,
                "type", typ
        );
        return spaltenWerte;
    }


}
