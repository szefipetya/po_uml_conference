/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.repository;

import com.szefi.uml_conference.security.model.UserEntity;
import com.szefi.uml_conference.security.model.jwt.BlackListedJwtCollectorEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author h9pbcl
 */
public interface BlackListedJwtCollectorEntityRepository extends JpaRepository<BlackListedJwtCollectorEntity, Integer> {   
      @Query(value = "SELECT TOP 1 * FROM jwt_black_list ", nativeQuery = true)
    BlackListedJwtCollectorEntity getBlackList();
    
}
