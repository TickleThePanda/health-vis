package uk.co.ticklethepanda.health.weight.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.co.ticklethepanda.health.weight.domain.entities.Weight;

import java.time.LocalDate;
import java.util.List;

public interface WeightRepo extends JpaRepository<Weight, Long> {

    @Query("from Weight as weight where weight.date = :date")
    Weight findByDate(
            @Param("date") LocalDate date);

    @Query("from Weight as weight"
            + " where weight.weightAm is not null"
            + " or weight.weightPm is not null"
            + " order by weight.date asc")
    List<Weight> findWhereNotEmpty();

    @Query("from Weight as weight where weight.date = (select max(w.date) from Weight w)")
    Weight findLatest();

    @Query("" +
            "  from Weight as weight " +
            " where (:startDate is null or weight.date >= :startDate)" +
            "   and (:endDate is null or weight.date <= :endDate)" +
            "   and (weight.weightAm is not null or weight.weightPm is not null)" +
            " order by weight.date asc")
    List<Weight> findWithinDateRange(
            @Param("startDate") LocalDate start,
            @Param("endDate") LocalDate end);
}
