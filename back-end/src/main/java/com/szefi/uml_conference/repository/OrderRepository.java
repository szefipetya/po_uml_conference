/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.repository;

import com.clarmont.orderentities.model.entity.CartItemEntity;
import com.clarmont.orderentities.model.entity.ShopOrderEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author peti
 */

@Repository
public interface OrderRepository extends CrudRepository<ShopOrderEntity,UUID> {
    List<ShopOrderEntity> findAll();
   
}
