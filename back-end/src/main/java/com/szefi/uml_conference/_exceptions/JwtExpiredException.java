/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference._exceptions;

/**
 *
 * @author h9pbcl
 */
public class JwtExpiredException extends JwtException {
      public JwtExpiredException(String msg){
        super(msg);
    }
}
