/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.model.jwt;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author h9pbcl
 */
@Entity
@Table(name="black_jwt")
public class JwtEntity {
    @GeneratedValue
    @Id
    private Integer id;
    @ManyToOne
    BlackListedJwtCollectorEntity collector;

    private String token="";

    public BlackListedJwtCollectorEntity getCollector() {
        return collector;
    }

    public void setCollector(BlackListedJwtCollectorEntity collector) {
        this.collector = collector;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
     public JwtEntity() {
       
    }
    public JwtEntity(String token) {
        this.token=token;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof JwtEntity){
            return ((JwtEntity)obj).getToken().equals(this.token);
        }
        else if(obj instanceof String){
            return ((String)obj).equals(this.token);
        }
        return false;
    }
    
}
