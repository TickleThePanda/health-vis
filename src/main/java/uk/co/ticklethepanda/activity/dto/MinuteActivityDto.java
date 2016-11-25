package uk.co.ticklethepanda.activity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

/**
 *
 */
public class MinuteActivityDto {

    @JsonFormat(pattern = "HH:mm:ss")
    public final LocalTime time;
    public final double steps;

    public MinuteActivityDto(LocalTime time, double steps) {
        this.time = time;
        this.steps = steps;
    }
}
