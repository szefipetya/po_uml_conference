/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.model.jwt;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author h9pbcl
 */
@Entity
@Table(name="jwt_black_list")
public class BlackListedJwtCollectorEntity {
    @GeneratedValue
    @Id
    Integer id;
    @OneToMany(mappedBy = "collector",cascade = CascadeType.ALL)
    List<JwtEntity> blackListedJwts=new ArrayList<>();

    public BlackListedJwtCollectorEntity(){}
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<JwtEntity> getJwts() {
        return blackListedJwts;
    }

    public void setJwts(List<JwtEntity> jwts) {
        this.blackListedJwts = jwts;
    }
}
