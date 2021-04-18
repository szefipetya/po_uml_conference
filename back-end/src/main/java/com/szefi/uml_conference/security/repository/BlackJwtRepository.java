/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.repository;

import com.szefi.uml_conference.security.model.jwt.BlackListedJwtCollectorEntity;
import com.szefi.uml_conference.security.model.jwt.JwtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author h9pbcl
 */
public interface BlackJwtRepository extends JpaRepository<JwtEntity, Integer> {   
      @Query(value = "SELECT * FROM black_jwt where token=?1", nativeQuery = true)
    JwtEntity findToken(String token);
    
}