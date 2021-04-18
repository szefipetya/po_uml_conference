/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.entity.management;

import com.szefi.uml_conference.model.common.management.ACTION_TYPE;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 *
 * @author h9pbcl
 */
@Entity
public class PermissionEntity implements Serializable {
    @Id
    @GeneratedValue
     Integer id;
    ACTION_TYPE action_level;
    @ManyToMany
     List<File_cEntity> files;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

  

    public ACTION_TYPE getAction_level() {
        return action_level;
    }

    public void setAction_level(ACTION_TYPE action_level) {
        this.action_level = action_level;
    }

    public List<File_cEntity> getFiles() {
        return files;
    }

    public void setFiles(List<File_cEntity> files) {
        this.files = files;
    }

 
}
