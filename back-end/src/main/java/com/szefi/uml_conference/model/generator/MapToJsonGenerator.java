/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 *
 * @author h9pbcl
 * @param <K> Type of the map key
 * @param <V> Type of teh map value
 */
public class MapToJsonGenerator<K,V> implements IdentifierGenerator {

    ObjectMapper mapper=new ObjectMapper();


    @Override
    public Serializable generate(SharedSessionContractImplementor ssci, Object o) throws HibernateException {
            Map<K, V> map = (Map<K, V>) o;
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(MapToJsonGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        
    return UUID.randomUUID().hashCode();

    }

    @Override
    public boolean supportsJdbcBatchInserts() {
        return IdentifierGenerator.super.supportsJdbcBatchInserts(); //To change body of generated methods, choose Tools | Templates.
    }
}