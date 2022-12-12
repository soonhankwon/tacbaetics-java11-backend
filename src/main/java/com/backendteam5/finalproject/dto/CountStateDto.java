package com.backendteam5.finalproject.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CountStateDto {
    private String username;
    private List<String> state;
    private List<Long> count;
    @QueryProjection
    public CountStateDto(String username, List<String> state, List<Long> count){
        this.username = username;
        this.state = state;
        this.count = count;
    }
}
