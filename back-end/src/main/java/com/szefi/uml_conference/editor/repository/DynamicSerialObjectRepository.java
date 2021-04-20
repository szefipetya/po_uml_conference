/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.repository;

import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.model.entity.management.File_cEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author h9pbcl
 */
public interface DynamicSerialObjectRepository extends JpaRepository<DynamicSerialObject, Integer> {
    Optional<DynamicSerialObject> findById(Integer id);
    
  /*        @Query(value = "UP * FROM files where token=?1", nativeQuery = true)
    Optional<File_cEntity> update(File_cEntity id);*/
    
}
