package com.backendteam5.finalproject.repository;

import com.backendteam5.finalproject.entity.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourierRepository extends JpaRepository<Courier, Long> {
    List<Courier> findByRoute(String route);
    List<Courier> findByRouteAndSubRoute(String route, int subRoute);
    List<Courier> findByUsernameAndStateOrderByArrivalDateDesc(String username, Boolean state);
    List<Courier> findByUsernameAndStateOrderByArrivalDateAsc(String username, Boolean state);
}
