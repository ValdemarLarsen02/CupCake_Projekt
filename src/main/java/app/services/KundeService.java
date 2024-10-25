package app.services;

import app.controllers.DatabaseController;

public class KundeService {
    private static DatabaseController databaseController;

    // Constructor som tager DatabaseController som parameter
    public KundeService(DatabaseController databaseController) {
        this.databaseController = databaseController;
    }

    // Metode til at indsætte beløb på en kundes konto
    public void indsaetBeloeb(int kundeId, double beloeb) {
        databaseController.opdaterSaldo(kundeId, beloeb);  // Kald DatabaseController til at opdatere saldo
    }
}
