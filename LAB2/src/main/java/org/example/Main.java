package org.example;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Model model = null;
        View view = null;
        Controller controller = null;
        try {
            model = new Model();
            view = new View();
            controller = new Controller(model, view);

            while(true)
                try {
                    controller.mainMenu();
                } catch (Exception e) {
                    controller.handleException(e);
                }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}