/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.generator;

import java.beans.Statement;
import java.io.Serializable;
import java.sql.Connection;
import java.util.UUID;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 *
 * @author h9pbcl
 */
public class IdGenerator implements IdentifierGenerator {



    @Override
    public Serializable generate(SharedSessionContractImplementor ssci, Object o) throws HibernateException {
        
    return UUID.randomUUID().hashCode();

    }

    @Override
    public boolean supportsJdbcBatchInserts() {
        return IdentifierGenerator.super.supportsJdbcBatchInserts(); //To change body of generated methods, choose Tools | Templates.
    }
}