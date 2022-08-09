package jpa;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private Date date;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sender_id")
    private Count sender;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "receiver_id")
    private Count receiver;

    @Column
    private Double sum;

    public Count getSender() {
        return sender;
    }

    public void setSender(Count sender) {
        this.sender = sender;
    }

    public Count getReceiver() {
        return receiver;
    }

    public void setReceiver(Count receiver) {
        this.receiver = receiver;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Transaction() {
        this.date = new Date();
    }

    public Transaction(Count from, Count to, Double sum) {
        this.date = new Date();
        this.sender = from;
        this.receiver = to;
        this.sum = sum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void makeTransaction() {
        this.sender.setBalance(this.sender.getBalance() - this.getSum());
        this.receiver.setBalance(this.receiver.getBalance() + this.getSum());
        this.sender.getOutgoingTransactions().add(this);
        this.receiver.getIncomingTransactions().add(this);
    }

}