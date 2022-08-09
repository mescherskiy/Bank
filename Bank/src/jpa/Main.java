package jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.*;

public class Main {

    static EntityManagerFactory emf;
    static EntityManager em;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            emf = Persistence.createEntityManagerFactory("JPATest");
            em = emf.createEntityManager();
            addCurrencies();
            try {
                while (true) {
                    System.out.println("1. Create new user");
                    System.out.println("2. Add new count");
                    System.out.println("3. Replenish the count");
                    System.out.println("4. Convert money");
                    System.out.println("5. Calculate all user's funds");
                    System.out.println("6. Show user's counts");
                    System.out.print("-> ");

                    switch(sc.nextLine()) {
                        case "1":
                            createNewUser(sc);
                            break;
                        case "2":
                            addNewCount(sc);
                            break;
                        case "3":
                            replenishTheCount(sc);
                            break;
                        case "4":
                            makeConvertation(sc);
                            break;
                        case "5":
                            viewAllFunds(sc);
                            break;
                        case "6":
                            getAllCounts(sc);
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                em.close();
                emf.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }



    public static void createNewUser(Scanner sc) {
        System.out.println("Enter user's name: ");
        User user = new User(sc.nextLine());
        System.out.println("If you want to add a new account to this user, press 'Y'. Else press any button.");
        System.out.print("-> ");
        String input = sc.nextLine();
        if ("Y".equals(input) || "y".equals(input)) {
            System.out.println("Choose currency: ");
            System.out.println("1. UAH");
            System.out.println("2. USD");
            System.out.println("3. EUR");
            System.out.print("-> ");
            String curr = sc.nextLine();
            if (!curr.equals("1") && !curr.equals("2") && !curr.equals("3")) {
                System.out.println("Error! You need to choose only between 1, 2 and 3");
                return;
            }
            user.getCounts().add(new Count(user, em.getReference(Currency.class, Long.parseLong(curr))));
            System.out.println("Done!");
        }
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        System.out.println("User " + user.getName() + " was successfully created!");
    }

    public static void addNewCount(Scanner sc) {
        User user = chooseUser(sc);
        System.out.println("Choose currency: ");
        System.out.println("1. UAH");
        System.out.println("2. USD");
        System.out.println("3. EUR");
        System.out.print("-> ");
        String curr = sc.nextLine();
        if (!curr.equals("1") && !curr.equals("2") && !curr.equals("3")) {
            System.out.println("Error! You need to choose only between 1, 2 and 3");
            return;
        }
        for (Count count : user.getCounts()) {
            if (count.getValuta().getId().equals(Long.parseLong(curr))) {
                System.out.println("The user already have such count");
                return;
            }
        }
        user.getCounts().add(new Count(user, em.getReference(Currency.class, Long.parseLong(curr))));
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        System.out.println("Done!");
    }

    public static void makeConvertation(Scanner sc) {
        User user = chooseUser(sc);
        if (user.getCounts().size() <= 1) {
            System.out.println("User doesn't have multiple counts");
            return;
        } else {
            Count from = null;
            Count to = null;
            System.out.println("Choose valuta to convert from: ");
            for (Count count : user.getCounts()) {
               System.out.println(count.getId() + ". " + count.getValuta().getName());
            }
            System.out.print("-> ");
            try {
                from = em.getReference(Count.class, Long.parseLong(sc.nextLine()));
            } catch (NumberFormatException e) {
                System.out.println(e);
            }
            System.out.println("Choose valuta to convert to: ");
            for (Count count : user.getCounts()) {
                if (!count.equals(from) && count != null) {
                    System.out.println(count.getId() + ". " + count.getValuta().getName());
                }
            }
            System.out.print("-> ");
            try {
                to = em.getReference(Count.class, Long.parseLong(sc.nextLine()));
            } catch (NumberFormatException e) {
                System.out.println(e);
            }
            System.out.println("Enter sum to transfer: ");
            Double sum = 0D;
            try {
                sum = Double.parseDouble(sc.nextLine());
            } catch (Exception e) {
                System.out.println(e);
            }
            user.convert(em, from, to, sum);
        }
    }

    public static void addCurrencies() {
        Currency uah = new Currency("UAH", 1.0);
        Currency usd = new Currency("USD", 36.5686);
        Currency eur = new Currency("EUR", 37.2104);
        em.getTransaction().begin();
        try {
            em.persist(uah);
            em.persist(usd);
            em.persist(eur);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }

    public static void replenishTheCount(Scanner sc) {
        User user = chooseUser(sc);
        while (true) {
            try {
                System.out.println("Choose valuta:");
                System.out.println("1. Hryvna (UAH)");
                System.out.println("2. Dollar (USD)");
                System.out.println("3. Euro (EUR)");
                System.out.print("-> ");
                Count count = null;
                String chose;
                switch (sc.nextLine()) {
                    case "1":
                        chose = "UAH";
                        break;
                    case "2":
                        chose = "USD";
                        break;
                    case "3":
                        chose = "EUR";
                        break;
                    default:
                        return;

                }
                for (Count cnt : user.getCounts()) {
                    if (cnt.getValuta().getName().equals(chose)) {
                        count = cnt;
                        break;
                    }
                }
                if (count == null) {
                    System.out.println("User doesn't have an " + chose + " count");
                    return;
                }
                System.out.println("Enter the amount to replenish the account: ");
                count.setBalance(count.getBalance() + Long.parseLong(sc.nextLine()));
                System.out.println("Replenishing complete. New balance is: " + count.getBalance());
                em.getTransaction().begin();
                try {
                    em.persist(count);
                    em.getTransaction().commit();
                } catch (Exception e) {
                    em.getTransaction().rollback();
                }
                return;
            } catch (NumberFormatException e) {
                System.out.println("Error! Input only numbers");
            }
        }

    }

    public static void viewAllFunds(Scanner sc) {
        User user = chooseUser(sc);
        user.calculateAllFunds();
    }

    public static User chooseUser(Scanner sc) throws NumberFormatException {
        List<User> users = em.createQuery("SELECT e FROM User e").getResultList();
        Comparator<User> comparator = (a, b) -> (int) (a.getId() - b.getId());
        users.sort(comparator);
        for (User user : users) {
            System.out.println(user.getId() + ". " + user.getName());
        }
        System.out.println("Enter user's ID: ");
        User user = em.getReference(User.class, Long.parseLong(sc.nextLine()));
        if (user == null)
            throw new NumberFormatException();
        return user;
    }

    public static void getAllCounts(Scanner sc) {
        User user = chooseUser(sc);
        for (Count count : user.getCounts()) {
            System.out.println(count.getId() + ". " + count.getValuta().getName());
        }
        System.out.println("Amount of counts: " + user.getCounts().size());
    }
}







