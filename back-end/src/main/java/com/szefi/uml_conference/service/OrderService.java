/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.service;

import com.clarmont.orderentities.model.dto.ShopOrderDto;
import com.clarmont.orderentities.model.entity.CartItemEntity;
import com.clarmont.orderentities.model.entity.ShopOrderEntity;
import com.szefi.uml_conference.repository.OrderRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author peti and tomi
 */
@Service
public class OrderService {
    
    @Autowired
   OrderRepository repo;
    
    @Autowired
    DtoTransformer transformer;

    public OrderRepository getRepo() {
        return repo;
    }

    public List<ShopOrderDto> getOrders() {
         return repo.findAll().stream().map(n -> transformer.shopOrderEntityToDto(n)).collect(Collectors.toList()); 
    }
    
    public void deleteAll(){
        repo.deleteAll();
    }
    
    public boolean delete(UUID id){
        if(repo.existsById(id)){
         repo.deleteById(id);
         return true;
        }
        return false;
    }
    
    public void saveAll(List<ShopOrderEntity> list){
        repo.saveAll(list);
    }
    
    public Optional<ShopOrderEntity> getById(UUID id){
        return repo.findById(id);
    }

    
    public void updateFromClient(ShopOrderDto dto) {
        repo.save(transformer.shopOrderDtoToEntity(dto));
    }
    
    
}
