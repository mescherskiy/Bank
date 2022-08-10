package jpa;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        BankServiceManager bsm = new BankServiceManager();
        Scanner sc = new Scanner(System.in);
        try {
            bsm.addCurrencies();
            try {
                while (true) {
                    System.out.println("1. Create new user");
                    System.out.println("2. Add new count");
                    System.out.println("3. Replenish the count");
                    System.out.println("4. Convert money");
                    System.out.println("5. Calculate all user's funds");
                    System.out.println("6. Show user's counts");
                    System.out.println("7. Make a transaction between two users");
                    System.out.println("8. Create default user (with all counts and money on them)");
                    System.out.println("9. Create default user (with UAH and USD counts and money on them)");
                    System.out.println("10. Create default user (with UAH and EUR counts and money on them)");
                    System.out.print("-> ");

                    switch(sc.nextLine()) {
                        case "1":
                            bsm.createNewUser(sc);
                            break;
                        case "2":
                            bsm.addNewCount(sc);
                            break;
                        case "3":
                            bsm.replenishTheCount(sc);
                            break;
                        case "4":
                            bsm.makeConvertation(sc);
                            break;
                        case "5":
                            bsm.viewAllFunds(sc);
                            break;
                        case "6":
                            bsm.getAllCounts(sc);
                            break;
                        case "7":
                            bsm.sendMoney(sc);
                            break;
                        case "8":
                            bsm.createDefaultUserWithAllCounts();
                            break;
                        case "9":
                            bsm.createDefaultUserWithUahAndUsdCounts();
                            break;
                        case "10":
                            bsm.createDefaultUserWithUahAndEurCounts();
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                bsm.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }
}







