/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.server_side;

public class EventArgs {
    protected Object _sender;

    public EventArgs(Object _sender) {
        this._sender = _sender;
    }

    public Object sender() {
        return _sender;
    }
}
