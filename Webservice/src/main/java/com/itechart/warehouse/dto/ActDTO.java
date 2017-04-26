package com.itechart.warehouse.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Data transfer object for act entity.
 */
@Setter
@Getter
@lombok.ToString(exclude = "goodsList")
public class ActDTO {
    private List<Long> goodsList;
    private String type;

}
