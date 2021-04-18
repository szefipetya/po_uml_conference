package com.szefi.uml_conference.controller;

import com.szefi.uml_conference.model.dto.diagram.Diagram;
import com.szefi.uml_conference.model.dto.management.FolderDto;
import com.szefi.uml_conference.security.model.auth.AuthRequest;
import com.szefi.uml_conference.security.model.auth.LogoutRequest;
import com.szefi.uml_conference.security.service.JwtAuthRequestHandlerService;

import com.szefi.uml_conference.socket.threads.service.SocketSessionService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author peti and tomi
 */
@RequestMapping("/")
@RestController
public class ApiController {

   @Autowired
   JwtAuthRequestHandlerService jwtAuthService;
    @Autowired
    SocketSessionService eService;
    
    @GetMapping("get/dg/{id}")
    public Diagram getOne(@PathParam("id") String id) {
        return eService.getDummyDiagram();
    }
     
        @GetMapping("/")
    public String home() {
        return ("<h1>Welcome</h1>");
    }
       @PostMapping("/test")
    public String test() {
        return ("<h1>Welcome test</h1>");
    }

    @GetMapping("/user")
    public String user() {
        return ("<h1>Welcome User</h1>");
    }

    @GetMapping("/admin")
    public String admin() {
        return ("<h1>Welcome Admin</h1>");
    }
    
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationJwt(@RequestBody AuthRequest req) throws Exception {
       try{
            return ResponseEntity.ok( jwtAuthService.jwtAuth(req));
       }catch(BadCredentialsException ex){
           return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
       }
    }
     @PostMapping("/log_me_out")//angular miatt van ez, bugos
    public ResponseEntity<?> logoutWithJwt(@RequestBody LogoutRequest token) throws Exception {
      try{
            return ResponseEntity.ok( jwtAuthService.jwtLogout(token.getJwt_token()));
       }catch(BadCredentialsException ex){
           return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
       }
    
    }
/*
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
    }*/

    @Path("ok")
    @GET
    public Response ping() {
        //String retval = "Ping";
        return Response
                .ok("rendben")
                .build();
    }
}
