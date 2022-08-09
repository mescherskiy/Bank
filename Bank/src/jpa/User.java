package jpa;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Count> counts;

    public User() {
        counts = new HashSet<>();
    }

    public User(String name) {
        this.name = name;
        counts = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCounts(HashSet<Count> counts) {
        this.counts = counts;
    }

    public Set<Count> getCounts() {
        return counts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void convert(EntityManager em, Count from, Count to, Double amount) {
        Double convertedSum = 0D;
        if (from.getBalance() < amount) {
            System.out.println("Not enough funds");
            return;
        }
        from.setBalance(from.getBalance() - amount);
        if (from.getValuta().getName().equals("UAH")) {
            convertedSum = amount / to.getValuta().getValue();
        } else if (to.getValuta().getName().equals("UAH")) {
            convertedSum = amount * from.getValuta().getValue();
        } else {
            convertedSum = amount * from.getValuta().getValue() / to.getValuta().getValue();
        }
        String sum = String.format("%.2f", convertedSum);
        sum = sum.replace(',', '.');
        convertedSum = Double.parseDouble(sum);
        to.setBalance(to.getBalance() + (convertedSum));
        System.out.println(convertedSum + to.getValuta().getSymbol() + " was successfully transfered");
        try {
            em.getTransaction().begin();
            em.persist(from);
            em.persist(to);
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }

    public void calculateAllFunds() {
        Double sum = 0D;
        for (Count count : this.getCounts()) {
            if (count.getValuta().getName().equals("UAH"))
            sum += count.getBalance();
            else {
                sum += count.getBalance() * count.getValuta().getValue();
            }
        }
        System.out.println("Total funds: " + String.format("%.2f", sum) + "(UAH)");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!id.equals(user.id)) return false;
        if (!name.equals(user.name)) return false;
        return counts != null ? counts.equals(user.counts) : user.counts == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (counts != null ? counts.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}