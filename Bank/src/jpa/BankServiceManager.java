package jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class BankServiceManager {
    static EntityManagerFactory emf;
    static EntityManager em;

    public BankServiceManager() {
        emf = Persistence.createEntityManagerFactory("JPATest");
        em = emf.createEntityManager();
    }

    public void createNewUser(Scanner sc) {
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

    public void addNewCount(Scanner sc) {
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

    public void makeConvertation(Scanner sc) {
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

    public void addCurrencies() {
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

    public void replenishTheCount(Scanner sc) {
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

    public void viewAllFunds(Scanner sc) {
        User user = chooseUser(sc);
        user.calculateAllFunds();
    }

    public User chooseUser(Scanner sc) throws NumberFormatException {
        List<User> users = em.createQuery("SELECT e FROM User e").getResultList();
        Comparator<User> comparator = (a, b) -> (int) (a.getId() - b.getId());
        users.sort(comparator);
        System.out.println("Enter user's ID: ");
        for (User user : users) {
            System.out.println(user.getId() + ". " + user.getName());
        }
        User user = em.getReference(User.class, Long.parseLong(sc.nextLine()));
        if (user == null)
            throw new NumberFormatException();
        return user;
    }

    public void getAllCounts(Scanner sc) {
        User user = chooseUser(sc);
        for (Count count : user.getCounts()) {
            System.out.println(count.getId() + ". " + count.getValuta().getName());
        }
        System.out.println("Amount of counts: " + user.getCounts().size());
    }

    public void sendMoney(Scanner sc) {
        System.out.println("Choose sender.");
        User user1 = chooseUser(sc);
        System.out.println("Choose sender's count: ");
        for (Count count : user1.getCounts()) {
            System.out.println(count.getId() + ". "+ count.getBalance() + count.getValuta().getSymbol());
        }
        Long countID = 0L;
        try {
            countID = Long.parseLong(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println(e);
            return;
        }
        Count from = null;
        Count to = null;
        for (Count count : user1.getCounts()) {
            if (count.getId().equals(countID)) {
                from = count;
            }
        }
        System.out.println("Choose receiver.");
        User user2 = chooseUser(sc);
        if (user1 == user2) {
            System.out.println("Error! You must choose another user!");
            return;
        }
        while (true) {
            System.out.println("Choose receiver's count: ");
            for (Count count : user2.getCounts()) {
                System.out.println(count.getId() + ". "+ count.getBalance() + count.getValuta().getSymbol());
            }
            try {
                countID = Long.parseLong(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(e);
                return;
            }

            for (Count count : user2.getCounts()) {
                if (count.getId().equals(countID)) {
                    to = count;
                }
            }
            if (from.getValuta().getName().equals(to.getValuta().getName())) {
                break;
            } else {
                System.out.println("Counts mismatch. Choose receiver's count in the same valuta.");
                return;
            }
        }
        Double sum = 0D;
        System.out.println("Enter the amount you want to transfer: ");
        try {
            sum = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println(e);
            return;
        }
        if (sum > from.getBalance()) {
            System.out.println("Not enough funds!");
            return;
        }
        Transaction trans = new Transaction(from, to, sum);
        trans.makeTransaction();
        em.getTransaction().begin();
        em.persist(trans);
        em.getTransaction().commit();
        System.out.println("Transaction complete!");
    }

    public void close(){
        em.close();
        emf.close();
    }

    public User createDefaultUserWithAllCounts() {
        User user = new User("Name" + (Math.random() * 10));
        Count uah = new Count(user, em.getReference(Currency.class, 1L));
        Count usd = new Count(user, em.getReference(Currency.class, 2L));
        Count eur = new Count(user, em.getReference(Currency.class, 3L));
        uah.setBalance(1000D);
        usd.setBalance(1000D);
        eur.setBalance(1000D);
        user.getCounts().add(uah);
        user.getCounts().add(usd);
        user.getCounts().add(eur);
        em.getTransaction().begin();
        try {
            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
        return user;
    }

    public User createDefaultUserWithUahAndUsdCounts() {
        User user = new User("Name" + (Math.random() * 10));
        Count uah = new Count(user, em.getReference(Currency.class, 1L));
        Count usd = new Count(user, em.getReference(Currency.class, 2L));
        uah.setBalance(1000D);
        usd.setBalance(1000D);
        user.getCounts().add(uah);
        user.getCounts().add(usd);
        em.getTransaction().begin();
        try {
            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
        return user;
    }

    public User createDefaultUserWithUahAndEurCounts() {
        User user = new User("Name" + (Math.random() * 10));
        Count uah = new Count(user, em.getReference(Currency.class, 1L));
        Count eur = new Count(user, em.getReference(Currency.class, 3L));
        uah.setBalance(1000D);
        eur.setBalance(1000D);
        user.getCounts().add(uah);
        user.getCounts().add(eur);
        em.getTransaction().begin();
        try {
            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
        return user;
    }
}
