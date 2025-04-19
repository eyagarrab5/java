package tests;

import models.User;
import services.CrudUser;

public class mainUser {
    public static void main(String[] args) {
        CrudUser crudUser = new CrudUser();
        boolean allTestsPassed = true;

        // Test 1: Ajout
        System.out.println("=== TEST AJOUT ===");
        User newUser = new User(
                "eya.ayari@esprit.tn",
                "motdepasse123",
                "ROLE_USER",
                false,
                "sofienzzz",
                "ayari",
                "28740885",
                "206"
        );
        ;
        crudUser.add(newUser);

        if(newUser.getId() <= 0) {
            System.err.println("❌ Échec ajout - ID non généré");
            allTestsPassed = false;
        } else {
            System.out.println("✔ Ajout réussi - ID: " + newUser.getId());
        }

        System.out.println("\n=== TEST GET BY ID ===");
        User fetchedUser = crudUser.getById(newUser.getId());

        if(fetchedUser == null) {
            System.err.println("❌ Utilisateur non trouvé");
            allTestsPassed = false;
        } else if(!fetchedUser.getEmail().equals(newUser.getEmail())) {
            System.err.println("❌ Incohérence de données");
            allTestsPassed = false;
        } else {
            System.out.println("✔ Récupération OK - Email: " + fetchedUser.getEmail());
        }

        System.out.println("\n=== RÉSULTATS ===");
        if(allTestsPassed) {
            System.out.println("✅ TOUS LES TESTS ONT RÉUSSI");
        } else {
            System.err.println("❌ CERTAINS TESTS ONT ÉCHOUÉ");
        }
    }}