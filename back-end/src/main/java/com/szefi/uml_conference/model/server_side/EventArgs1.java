/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.server_side;

public class EventArgs1<T1> extends EventArgs {
    T1 _arg1;
    public EventArgs1(Object _sender, T1 _arg1) {
        super(_sender);
        this._arg1 = _arg1;
    }

    public  T1 arg1(){
        return _arg1;
    }
}
