/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.debug;

import com.clarmont.orderentities.model.entity.CartItemEntity;
import com.clarmont.orderentities.model.entity.ShippingAddressEntity;
import com.clarmont.orderentities.model.entity.BillingAddressEntity;
import com.clarmont.orderentities.model.entity.ShopOrderEntity;
import com.szefi.uml_conference.service.OrderService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.util.UUID;


/**
 *
 * @author peti and tomi
 */
@Component
public class ApplicatoinStartupRunner {
      /* @Autowired
        OrderService service;
       @PostConstruct*/
       
       public void putDummyData(){
          //TODO
                 //create some entities  
          //  new UUID(), products, familyName, givenName, email, comment, shippingAddress, billingAddress, paymentMethodId, deliveryMethodId, date))
       //TODO: ide ird a cuccokat
       List<CartItemEntity> list1=new ArrayList();

       List<CartItemEntity> list2=new ArrayList();
       List<CartItemEntity> list3=new ArrayList();
       List<CartItemEntity> list4=new ArrayList();
       
       ShopOrderEntity ent1=new ShopOrderEntity(UUID.randomUUID(),list1,"Pool","Peti","Email1","",null,null,UUID.randomUUID(),UUID.randomUUID(),new Date());
       ShopOrderEntity ent2=new ShopOrderEntity(UUID.randomUUID(),list2,"Marcsi","Nagy","Email2","",null,null,UUID.randomUUID(),UUID.randomUUID(),new Date());
       ShopOrderEntity ent3=new ShopOrderEntity(UUID.randomUUID(),list3,"Kati","Kiss","Email3","",null,null,UUID.randomUUID(),UUID.randomUUID(),new Date());
       ShopOrderEntity ent4=new ShopOrderEntity(UUID.randomUUID(),list4,"Feri","Gyurcsány","Email4","",null,null,UUID.randomUUID(),UUID.randomUUID(),new Date());
       
          //  service.deleteAll();
list1.add(new CartItemEntity("alma",ent1,2,280,"kg"));
list1.add(new CartItemEntity("banan",ent1,2,450,"kg"));
list1.add(new CartItemEntity("körte",ent1,2,599,"kg"));


list2.add(new CartItemEntity("wc tisztító",ent2,2,1200,"db"));
list2.add(new CartItemEntity("domestos",ent2,1,890,"db"));
list2.add(new CartItemEntity("súrolószer",ent2,3,699,"db"));

list3.add(new CartItemEntity("wckefe",ent3,2,1200,"db"));
list3.add(new CartItemEntity("persil",ent3,1,890,"db"));
list3.add(new CartItemEntity("tölcsér",ent3,3,699,"db"));

list4.add(new CartItemEntity("'Én a kékfrankosra esküszöm'-bor",ent4,4,1200,"db"));
list4.add(new CartItemEntity("szürkebarát",ent4,1,350,"db"));
list4.add(new CartItemEntity("szilva pálesz",ent4,3,4200,"db"));


            ShopOrderEntity[] shopOrderEntitys = {     
            ent1,ent2,ent3,ent4
            };
            
            //service.saveAll(Arrays.asList(shopOrderEntitys));
       }
}
