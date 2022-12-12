package com.backendteam5.finalproject.dto;

import com.backendteam5.finalproject.entity.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AdminMainDto {
    private List<Account> userlist;
    private List<Long> tempAssignment;
    private List<CountStateDto> directAssignment;
    private List<RouteCountDto> routeCount;
}