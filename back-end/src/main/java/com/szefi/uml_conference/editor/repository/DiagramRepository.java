/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.repository;

import com.szefi.uml_conference.editor.model.diagram.DiagramEntity;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author h9pbcl
 */
@Repository
public interface DiagramRepository extends JpaRepository<DiagramEntity, Integer> {
    Optional<DiagramEntity> findById(Integer id);
    
  /*        @Query(value = "UP * FROM files where token=?1", nativeQuery = true)
    Optional<File_cEntity> update(File_cEntity id);*/
    
}
