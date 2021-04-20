/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.top;

import java.util.List;

/**
 *
 * @author h9pbcl
 * @param <T>
 */

public interface DynamicSerialContainer_I<T extends DynamicSerialObject> {
    List<T> container();
}
