package uk.co.ticklethepanda.health.weight.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.co.ticklethepanda.health.weight.WeightService;
import uk.co.ticklethepanda.health.weight.domain.model.EntryMeridiemPeriod;
import uk.co.ticklethepanda.health.weight.domain.entities.Weight;
import uk.co.ticklethepanda.health.weight.dtos.log.WeightForDayDto;
import uk.co.ticklethepanda.health.weight.dtos.log.WeightForMeridiemPeriodDto;
import uk.co.ticklethepanda.health.weight.dtos.log.WeightValueDto;

import java.time.LocalDate;
import java.util.List;

import static uk.co.ticklethepanda.health.weight.transformers.WeightTransformers.WEIGHT_TO_WEIGHT_DTO;
import static uk.co.ticklethepanda.health.weight.transformers.WeightTransformers.transformToPeriod;

@Controller
@RequestMapping(value = "/health/weight/log")
public class WeightLogController {

    private final WeightService weightService;

    public WeightLogController(
            @Autowired WeightService weightService) {
        this.weightService = weightService;
    }

    @GetMapping
    @ResponseBody
    public List<WeightForDayDto> getWeight() {
        return WEIGHT_TO_WEIGHT_DTO.transformList(weightService.getAllWeight());
    }

    @PutMapping(value = "/{date}/{meridiem}")
    @ResponseBody
    public WeightForMeridiemPeriodDto saveWeightForDate(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("meridiem") EntryMeridiemPeriod meridiem,
            @RequestBody WeightValueDto weightInput
    ) {
        Weight weight = weightService.createWeightEntryForPeriod(date, meridiem, weightInput.weight);
        return transformToPeriod(weight, meridiem);
    }

    @GetMapping(value = "/{date}/{meridiem}")
    @ResponseBody
    public WeightForMeridiemPeriodDto saveWeightForDate(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("meridiem") EntryMeridiemPeriod meridiem
    ) {
        Weight weight = weightService.getWeightForDate(date);
        return transformToPeriod(weight, meridiem);
    }


}
