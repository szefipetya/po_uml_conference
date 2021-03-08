/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.service;

import com.clarmont.orderentities.model.dto.CartItemDto;
import com.clarmont.orderentities.model.entity.ShopOrderEntity;
import com.clarmont.orderentities.model.dto.ShopOrderDto;
import com.clarmont.orderentities.model.entity.CartItemEntity;
import com.szefi.uml_conference.repository.OrderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author h9pbcl
 */

@Service
public class DtoTransformer {
    @Autowired
    OrderRepository repo;
    
    public ShopOrderEntity shopOrderDtoToEntity(ShopOrderDto dto){
        Optional<ShopOrderEntity> optionalEnt=repo.findById(dto.getId());
        ShopOrderEntity ent=optionalEnt.get();
        boolean isNew=false;
        if(ent==null){
            isNew=true;
           ent=new ShopOrderEntity();
           
        }
         
        ent.setAll(dto.getUserId(), dto.getFamilyName(), dto.getGivenName(), dto.getEmail(), dto.getComment(), dto.getShippingAddress(), dto.getBillingAddress(), dto.getPaymentMethodId(), dto.getDeliveryMethodId(), dto.getDate());
         
            for(CartItemDto dtoItem:dto.getProducts()){
                 if(isNew){
                   ent.setProducts(new ArrayList<>());
                   ent.getProducts().add(new CartItemEntity(dtoItem.getName(),ent,dtoItem.getAmount(),dtoItem.getPrice(),dtoItem.getUnit()));
               }else{
                ent.getCartItemById(dtoItem.getId()).setName(dtoItem.getName());
                ent.getCartItemById(dtoItem.getId()).setAmount(dtoItem.getAmount());
                ent.getCartItemById(dtoItem.getId()).setPrice(dtoItem.getPrice());
                ent.getCartItemById(dtoItem.getId()).setUnit(dtoItem.getUnit());
                 }
            }             
        return ent;
    }
     public ShopOrderDto  shopOrderEntityToDto(ShopOrderEntity ent){
                    List<CartItemDto> cart=new ArrayList();

        ShopOrderDto dto =new ShopOrderDto(ent.getUserId(), cart, ent.getFamilyName(), ent.getGivenName(), ent.getEmail(), ent.getComment(), ent.getShippingAddress(), ent.getBillingAddress(), ent.getPaymentMethodId(), ent.getDeliveryMethodId(), ent.getDate());
        dto.setId(ent.getId());
            for(CartItemEntity entItem:ent.getProducts()){
               cart.add(cartItemEntityToDto(entItem));
            }
             dto.setProducts(cart);       
        return dto;
    }
      public CartItemEntity cartItemDtoToEntity(CartItemDto dto,ShopOrderEntity order){
          CartItemEntity ent=new CartItemEntity(dto.getName(), order, dto.getAmount(), dto.getPrice(), dto.getUnit());
        
        return ent;
    }
        public CartItemDto cartItemEntityToDto(CartItemEntity ent){
          CartItemDto dto=new CartItemDto(ent.getName(), ent.getAmount(), ent.getPrice(), ent.getUnit());
        
        return dto;
    }
}
