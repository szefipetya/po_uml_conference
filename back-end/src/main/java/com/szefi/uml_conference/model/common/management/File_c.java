/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.common.management;

import java.util.Date;
import java.util.List;

/**
 *
 * @author h9pbcl
 */
public class File_c {
    String id;
    String name;
    String parent_folder_id ;
    String owner_id;
    List<Permission> permissions;
    Date date;
    ICON icon ;  
}
