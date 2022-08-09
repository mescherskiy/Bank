package jpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Entity
@Table(name = "counts")
public class Count {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "currency_id")
    private Currency valuta;

    private String name;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> IncomingTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> OutgoingTransactions = new ArrayList<>();

    @Column
    private Double balance;

    public List<Transaction> getIncomingTransactions() {
        return IncomingTransactions;
    }

    public void setIncomingTransactions(List<Transaction> incomingTransactions) {
        IncomingTransactions = incomingTransactions;
    }

    public List<Transaction> getOutgoingTransactions() {
        return OutgoingTransactions;
    }

    public void setOutgoingTransactions(List<Transaction> outgoingTransactions) {
        OutgoingTransactions = outgoingTransactions;
    }

    public Count() {
        this.setBalance(0.0);
    }

    public Count(User user, Currency valuta) {
        this.user = user;
        this.valuta = valuta;
        this.name = user.getName() + valuta.getName();
        user.getCounts().add(this);
        this.setBalance(0.0);
    }

    public Currency getValuta() {
        return valuta;
    }

    public void setCurrency(Currency valuta) {
        this.valuta = valuta;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setValuta(Currency valuta) {
        this.valuta = valuta;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }


}
