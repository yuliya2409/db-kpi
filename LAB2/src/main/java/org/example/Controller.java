package org.example;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Controller {

    private Model model;

    private View view;

    private DB db;


    Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        db = new DB();
    }


    public void mainMenu() throws Exception {
        int option = view.mainMenu();
        switch (option) {
            case 1 -> this.insert();
            case 2 -> this.update();
            case 3 -> this.delete();
            case 4 -> this.generate();
            case 5 -> this.search();
            case 6 -> {
                model.disconnect();
                System.exit(0);
            }
        }
    }

    private void search() {
        int searchOption = view.searchOption();
        if(searchOption < 1 || searchOption > 3) {
            view.showError(new Exception("Invalid search option"));
            return;
        }
        List<String> searchParams =
        switch (searchOption) {
            case 1 ->
                    List.of("first_name");
            case 2 ->
                    List.of("events");
            case 3 ->
                    List.of("first", "last");
            default -> {
                view.showError(new Exception("Invalid search option"));
                yield null;
            }
        };
        Map<String, String> params = view.columns("Search params", searchParams);
        List<String> rows = model.search(params, searchOption);
        view.rows(rows);
    }

    private void generate() {
        String table = view.table(List.of("manager", "event"));
        Map<String, String> val = view.columns("Dataset", List.of("Size"));
        model.generate(table, Integer.parseInt(val.get("Size")));
    }

    private void delete() throws ParseException {
        String t = view.table(this.db.tables());
        if(t.isEmpty()) {
            view.showError(new Exception("Invalid table"));
            return;
        }
        Map<String, String> pk = view.columns("Primary key",this.db.primaryKeys().get(t));
        model.delete(t, pk);
    }

    public void insert() throws Exception {
        String t = view.table(this.db.tables());
        if(t.isEmpty()) {
            view.showError(new Exception("Invalid table"));
            return;
        }
        Map<String, String> c = view.columns("Columns",this.db.columns().get(t));
        model.insert(t, c);
    }
    
    public void update() throws ParseException {
        String t = view.table(this.db.tables());
        if(t.isEmpty()) {
            view.showError(new Exception("Invalid table"));
            return;
        }
        Map<String, String> pk = view.columns("Primary key",this.db.primaryKeys().get(t));
        Map<String, String> c = view.columns("Columns", this.db.columns().get(t));
        model.update(t, pk, c);
    }

    public void handleException(Exception e) {
        view.showError(e);
    }
}
