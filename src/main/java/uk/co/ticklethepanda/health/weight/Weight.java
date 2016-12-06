package uk.co.ticklethepanda.health.weight;

import org.hibernate.annotations.*;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "WEIGHT",
        uniqueConstraints = @UniqueConstraint(columnNames = {"DATE"}))
@NamedQueries({
        @NamedQuery(name = "weight.findByDate",
                query = "from Weight as weight " +
                        "where weight.date = :date")
})
public class Weight {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "WEIGHT_ID", updatable = false, nullable = false)
    private long id;

    @Column(name = "DATE", updatable = false, nullable = false)
    private LocalDate date;
    @Column(name = "WEIGHT_AM")
    private Double weightAm;
    @Column(name = "WEIGHT_PM")
    private Double weightPm;

    public Weight() {}

    public Weight(LocalDate localDate, Double weightAm, Double weightPm) {
        this.date = localDate;
        this.weightAm = weightAm;
        this.weightPm = weightPm;
    }


    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate localDate) {
        this.date = localDate;
    }

    public Double getWeightAm() {
        return weightAm;
    }

    public void setWeightAm(Double weightAm) {
        this.weightAm = weightAm;
    }

    public Double getWeightPm() {
        return weightPm;
    }

    public void setWeightPm(Double weightPm) {
        this.weightPm = weightPm;
    }

    @Override
    public String toString() {
        return "Weight{" +
                "id=" + id +
                ", date=" + date +
                ", weightAm=" + weightAm +
                ", weightPm=" + weightPm +
                '}';
    }
}
