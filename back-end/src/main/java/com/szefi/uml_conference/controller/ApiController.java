package com.szefi.uml_conference.controller;

import com.clarmont.orderentities.model.dto.CartItemDto;
import com.clarmont.orderentities.model.dto.ShopOrderDto;
import com.clarmont.orderentities.model.entity.CartItemEntity;
import com.clarmont.orderentities.model.entity.ShopOrderEntity;
import com.szefi.uml_conference.service.OrderService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author peti and tomi
 */
/*@Path("/api/order")*/

public class ApiController {

    @Autowired
    OrderService service;

    @Path("get/{id}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Optional<ShopOrderEntity> getOne(@PathParam("id") UUID id) {
        return service.getById(id);
    }

    @DELETE
    @Path("delete/{id}")
    public Response delete(@PathParam("id") UUID id) {
        if (service.delete(id)) {
            return Response
                    .ok("order deleted")
                    .build();
        } else {
            return Response
                    .notModified("no order found the given id")
                    .build();
        }
    }

    @Path("get")
    @GET
    @Produces({"application/json", "application/xml"})
    public List<ShopOrderDto> getOrders() {
        return service.getOrders();
    }
    
    @Path("test")
    @GET
    @Produces({"application/json", "application/xml"})
    public TestObj test() {
        return new TestObj("name","id");
    }
      @Path("test2")
    @PUT
       @Consumes(MediaType.APPLICATION_JSON)
    @Produces({"application/json", "application/xml"})
    public String test2(Object ob) {
       
            TestObj o=(TestObj)ob;
            return o.getId()+o.getName();
        
       // return "object";
    }

    @Path("create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putOrder(ShopOrderEntity ent) {
        service.getRepo().save(ent);
        return Response
                .ok("order added")
                .build();
    }

    @Path("update/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateOrder(ShopOrderDto dto, @PathParam("id") UUID id) {
        Optional<ShopOrderEntity> entFromDb = service.getById(id);

        if (!entFromDb.isPresent()) {
            return Response
                    .notModified("no order found the given id")
                    .build();
        }

        service.updateFromClient(dto);
        // service.getRepo().save(new ShopOrderEntity(UUID.randomUUID(),null,"ssss","Sanyika","Email1","",null,null,UUID.randomUUID(),UUID.randomUUID(),new Date()));
        return Response
                .ok("order updated ")
                .build();
    }

    @Path("ok")
    @GET
    public Response ping() {
        //String retval = "Ping";
        return Response
                .ok("rendben")
                .build();
    }
}
