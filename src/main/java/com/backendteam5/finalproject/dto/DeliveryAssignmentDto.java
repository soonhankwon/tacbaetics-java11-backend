package com.backendteam5.finalproject.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeliveryAssignmentDto {
    private Long id;
    private int subRoute;
    private String zipCode;
    private String username;

    @QueryProjection
    public DeliveryAssignmentDto(Long id, int subRoute, String zipCode, String username){
        this.id = id;
        this.subRoute = subRoute;
        this.zipCode = zipCode;
        this.username = username;
    }
}
