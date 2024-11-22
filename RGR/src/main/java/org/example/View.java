package org.example;

import javax.print.DocFlavor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class View {

    private Scanner scanner = new Scanner(System.in);

    public void showError(Exception e) {
        System.out.println(e.getMessage());
    }

    public int mainMenu() {
        System.out.println("Main menu");
        System.out.println("1. Insert");
        System.out.println("2. Update");
        System.out.println("3. Delete");
        System.out.println("4. Generate");
        System.out.println("5. Search");
        System.out.println("6. Exit");

        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.next();
            this.showError(new Exception("Invalid input"));
            return -1;
        }
    }

    public String table(List<String> tables) {
        System.out.println("Table:");
        int i = 1;
        for (String t : tables) {
            System.out.println(i++ + ". " + t);
        }
        try {
            return tables.get(scanner.nextInt() - 1);
        } catch (Exception e) {
            scanner.next();
            return "";
        }
    }

    public Map<String, String> columns(String msg, List<String> columnsNames) {
        Map<String, String> columns = new HashMap<>();
        System.out.println(msg + ":");
        for (String c : columnsNames) {
            System.out.println(c + ": ");
            columns.put(c, scanner.next());
        }
        return columns;
    }

    public int searchOption() {
        System.out.println("Select a search query:");
        System.out.println("1. Search count of events by manager's name");
        System.out.println("2. Search venues by count of events");
        System.out.println("3. Search venues by date");
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.next();
            return 0;
        }
    }

    public void rows(List<String> rows) {
        for(String row : rows) {
            System.out.println(row);
        }
    }
}
