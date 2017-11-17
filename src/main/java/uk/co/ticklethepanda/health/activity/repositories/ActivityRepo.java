package uk.co.ticklethepanda.health.activity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ActivityRepo extends JpaRepository<ActivityEntity, Long> {

    // see orm.xml
    List<ActivityEntity> findAllWithSomeActivityInTheDay();

    ActivityEntity findByDateAndTime(
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );

    List<ActivityEntity> findByDate(
            @Param("date") LocalDate date
    );

    int countByDate(
            @Param("date") LocalDate date
    );

    @Query("SELECT MIN(a.date) FROM ActivityEntity a")
    LocalDate getMinDate();

    @Query("SELECT SUM(steps) FROM ActivityEntity")
    Double getSumOfSteps();

    @Query("SELECT SUM(steps) " +
            "FROM ActivityEntity AS a " +
            "WHERE a.date >= :start AND a.date <= :end")
    Double getSumOfStepsBetween(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
