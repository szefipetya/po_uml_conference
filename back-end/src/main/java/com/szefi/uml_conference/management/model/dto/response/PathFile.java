/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.model.dto.response;

import com.szefi.uml_conference.management.model.dto.FileHeaderDto;
import com.szefi.uml_conference.management.model.dto.interfaces.FileHeader_I;

/**
 *
 * @author h9pbcl
 */
public class PathFile {

    public int getIndex() {
        return index;
    }

    public PathFile() {
    }

    public PathFile(int index, FileHeader_I file) {
        this.index = index;
        this.file = file;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public FileHeader_I getFile() {
        return file;
    }

    public void setFile(FileHeader_I file) {
        this.file = file;
    }
    int index;
    FileHeader_I file;
}
