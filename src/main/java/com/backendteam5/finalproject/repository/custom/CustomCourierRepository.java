package com.backendteam5.finalproject.repository.custom;

import com.backendteam5.finalproject.dto.*;
import com.backendteam5.finalproject.entity.Account;

import java.util.List;

public interface CustomCourierRepository {

    List<CourierDto> searchByUsernameAndState(Account account, String state, String username);
    Long countUsernameAndState(Account account, String state, String username);
    List<CourierDto> searchCustomer(String customer);


    List<CountDirectDto> countUsernameDirect(Account account, String date);
    Long countUsernameTemp(Account account, String date);

    void updateByCourierId(Long courierId, String deliveryPerson);
    List<RouteCountDto> countRouteState(String area);
    List<CountDirectDto> countUsernameDirect(Account account);
    Long countUsernameTemp(Account account);
    List<AdminCourierDto> searchByDetail(String username,String area, SearchReqDto searchReqDto);
    List<AdminCourierDto> searchByCouriers(List<Long> couriers);


    String setUpdateStateDelay(List<Long> couriers);
    String setDeliveryPerson(List<Long> couriers, String username);

    String setReady();

    String getNowDate();
}
