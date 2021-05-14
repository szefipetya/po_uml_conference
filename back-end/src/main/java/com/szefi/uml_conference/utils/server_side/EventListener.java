/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.utils.server_side;

public interface EventListener<E extends EventArgs> {
    void action(E args);
}

