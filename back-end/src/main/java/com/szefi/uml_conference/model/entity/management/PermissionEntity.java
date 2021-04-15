/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.entity.management;

import com.szefi.uml_conference.model.common.management.ACTION_TYPE;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author h9pbcl
 */
@Entity
public class PermissionEntity implements Serializable {
    @Id
     String perm_tag;
    ACTION_TYPE action_level;
    @ManyToOne
    File_cEntity file;

    protected String getPerm_tag() {
        return perm_tag;
    }

    public void setPerm_tag(String perm_tag) {
        this.perm_tag = perm_tag;
    }

    public ACTION_TYPE getAction_level() {
        return action_level;
    }

    public void setAction_level(ACTION_TYPE action_level) {
        this.action_level = action_level;
    }

    public File_cEntity getFile() {
        return file;
    }

    public void setFile(File_cEntity file) {
        this.file = file;
    }
}
