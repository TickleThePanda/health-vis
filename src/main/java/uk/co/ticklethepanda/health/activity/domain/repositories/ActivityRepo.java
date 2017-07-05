package uk.co.ticklethepanda.health.activity.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import uk.co.ticklethepanda.health.activity.domain.entities.MinuteActivity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.Map;

public interface ActivityRepo extends JpaRepository<MinuteActivity, Long> {

    MinuteActivity findByDateAndTime(
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );

    List<MinuteActivity> findByDate(
            @Param("date") LocalDate date
    );

    int countByDate(
            @Param("date") LocalDate date
    );

    List<MinuteActivity> getAverageDay();

    List<MinuteActivity> getAverageDayBetweenDates(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    List<Map.Entry<DayOfWeek, MinuteActivity>> getAverageDayByWeekday();

    List<Map.Entry<Month, MinuteActivity>> getAverageDayByMonth();

    LocalDate getEarliestDateOfActivity();

    List<Map.Entry<Month, Double>> getSumOfStepsByMonth();

    List<Map.Entry<DayOfWeek, Double>> getSumOfStepsByDayOfWeek();

    Double getSumOfSteps();

    Double getSumOfStepsBetween(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
