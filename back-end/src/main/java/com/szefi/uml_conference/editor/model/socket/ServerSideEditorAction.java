/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.socket;

/**
 *
 * @author h9pbcl
 */
public class ServerSideEditorAction extends EditorAction {
    Object load;

    public Object getLoad() {
        return load;
    }

    public ServerSideEditorAction(ACTION_TYPE type) {
        super(type);
    }

    public void setLoad(Object load) {
        this.load = load;
    }
}
