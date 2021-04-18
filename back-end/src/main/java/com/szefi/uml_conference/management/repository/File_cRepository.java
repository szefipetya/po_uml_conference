/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.repository;

import com.szefi.uml_conference.model.entity.management.File_cEntity;
import com.szefi.uml_conference.security.model.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author h9pbcl
 */
public interface File_cRepository extends JpaRepository<File_cEntity, Integer> {
    Optional<File_cEntity> findById(Integer id);
    
  /*        @Query(value = "UP * FROM files where token=?1", nativeQuery = true)
    Optional<File_cEntity> update(File_cEntity id);*/
    
}
