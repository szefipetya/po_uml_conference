/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.repository;

import com.szefi.uml_conference.editor.model.diagram.DiagramEntity;
import com.szefi.uml_conference.editor.model.do_related.AttributeElement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author h9pbcl
 */
@Repository
public interface AttributeElementRepository extends JpaRepository<AttributeElement, Integer> {
    Optional<AttributeElement> findById(Integer id);
    List<AttributeElement> findAllByName(String name);
    
    @Modifying
@Query("delete from AttributeElement t where t.id in :ids")
  void deleteAllNative(@Param("ids")List<Integer> list);
  /*        @Query(value = "UP * FROM files where token=?1", nativeQuery = true)
    Optional<File_cEntity> update(File_cEntity id);*/
    
}