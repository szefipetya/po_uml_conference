/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.unit.converters;

import com.szefi.uml_conference.editor.model.do_related.PackageElement;
import com.szefi.uml_conference.editor.model.do_related.line.BreakPoint;
import com.szefi.uml_conference.editor.model.converter.BreakPointListConverter;
import com.szefi.uml_conference.editor.model.converter.PackageElementsListConverter;
import java.util.List;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author h9pbcl
 */

@SpringBootTest
public class Convertertests {
    @Test
  void testBreakpointConvertToString(){
       BreakPointListConverter conv=new BreakPointListConverter();
      String in="[\n" +
"    {\n" +
"        \"_type\": \"BreakPoint\",\n" +
"        \"point\": {\n" +
"            \"x\": 255,\n" +
"            \"y\": 338\n" +
"        }\n" +
"    },\n" +
"    {\n" +
"        \"_type\": \"BreakPoint\",\n" +
"        \"point\": {\n" +
"            \"x\": 271,\n" +
"            \"y\": 311\n" +
"        }\n" +
"    }\n" +
"]";
     List<BreakPoint> out=conv.convertToEntityAttribute(in);
      Assertions.assertEquals(255,out.get(0).getPoint().getX());
  }
  
     @Test
  void test_PackageElementListConverter(){
         PackageElementsListConverter conv=new PackageElementsListConverter();
      String in="[{\"icon\":\"PROJECT_CLASS\",\"referencedObjectId\":19,\"name\":\"New Class\",\"edit\":false}]";
     List<PackageElement> out=conv.convertToEntityAttribute(in);
      Assertions.assertEquals(1,out.size());
         System.out.println(out.get(0).getName());
         Assertions.assertEquals(in,conv.convertToDatabaseColumn(out));
      Assertions.assertEquals("New Class",out.get(0).getName());
  }
  
  
}
