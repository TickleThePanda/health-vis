package uk.co.ticklethepanda.health.weight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WeightRepo extends JpaRepository<Weight, Long> {

    @Query("from Weight as weight where weight.date = :date")
    Weight findByDate(
            @Param("date") LocalDate date);

    @Query("from Weight as weight"
            + " where weight.weightAm is not null"
            + " or weight.weightPm is not null")
    List<Weight> findWhereNotEmpty();

}
