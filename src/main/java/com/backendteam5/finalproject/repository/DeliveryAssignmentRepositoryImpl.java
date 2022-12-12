package com.backendteam5.finalproject.repository;

import com.backendteam5.finalproject.dto.DeliveryAssignmentDto;
import com.backendteam5.finalproject.repository.custom.CustomDeliveryAssignmentRepository;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.backendteam5.finalproject.entity.QAccount.account;
import static com.backendteam5.finalproject.entity.QAreaIndex.areaIndex;
import static com.backendteam5.finalproject.entity.QDeliveryAssignment.deliveryAssignment;

public class DeliveryAssignmentRepositoryImpl implements CustomDeliveryAssignmentRepository {
    private final JPAQueryFactory queryFactory;

    public DeliveryAssignmentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // Delivery의 account를 업데이트 하는 메소드 (zipcode와 username이 필요함)
    @Modifying(clearAutomatically = true)
    @Transactional
    @Override
    public void updateDelivery(List<Long> areaId, Long accountId){
        queryFactory
                .update(deliveryAssignment)
                .set(deliveryAssignment.account.id, accountId)
                .where(deliveryAssignment.areaIndex.id.in(areaId))
                .execute();
    }

    // Delivery를 수정을 위해서 지역별로 조회하는 메소드(Area와 Route가 필요함)
    // Area는 Context의 account에 등록된 Area를 조회하여 넣을거임
    @Transactional(readOnly = true)
    @Override
    public List<DeliveryAssignmentDto> selectDelivery(String area, String route) {
        return queryFactory
                .select(getDeliveryDto())
                .from(deliveryAssignment)
                .innerJoin(deliveryAssignment.areaIndex, areaIndex)
                .innerJoin(deliveryAssignment.account, account)
                .where(areaIndex.area.eq(area), areaIndex.route.eq(route))
                .orderBy(areaIndex.subRoute.asc())
                .fetch();
    }


    public ConstructorExpression<DeliveryAssignmentDto> getDeliveryDto(){
        return Projections.constructor(DeliveryAssignmentDto.class,
                areaIndex.subRoute,
                areaIndex.zipCode,
                account.username);
    }
}