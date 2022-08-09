package jpa;

import com.mysql.cj.exceptions.WrongArgumentException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "currency")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private final static List<String> names = new ArrayList<>(List.of("UAH", "USD", "EUR"));

    @Column(nullable = false)
    private String name;

    @Column
    private String symbol;

    @Column(nullable = false)
    private Double value;

    public Currency() {
    }

    public Currency(String name, Double value) {
        this.name = name;
        this.value = value;
        if (this.name.equals("UAH")) {
            this.symbol = "₴";
        } else if (this.name.equals("USD")) {
            this.symbol = "$";
        } else if (this.name.equals("EUR")){
            this.symbol = "€";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    //    public Double convert(Double amount, Enum<Valuta> name) {
//
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Currency currency = (Currency) o;

        if (!id.equals(currency.id)) return false;
        if (!name.equals(currency.name)) return false;
        return value.equals(currency.value);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", value=" + value +
                '}';
    }


}