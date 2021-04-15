/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 *
 * @author h9pbcl
 */
//@Entity
public enum ROLE {
    
    
  ROLE_USER("ROLE_USER"),
ROLE_ADMIN("ROLE_ADMIN");
  ROLE(final String text) {
        this.text = text;
    }

 
 //@Id
 //@GeneratedValue
 private Integer id;

    public Integer getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setId(Integer id) {
        this.id = id;
    }
 private final String text;
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
