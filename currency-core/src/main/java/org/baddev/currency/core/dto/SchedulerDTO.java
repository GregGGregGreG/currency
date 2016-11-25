package org.baddev.currency.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerDTO {
    @Range(min = 0, max = 9999999999L)
    private double amount;
    @Size(min = 3, max = 3)
    private String from;
    @Size(min = 3, max = 3)
    private String to;
    private String cron;
}