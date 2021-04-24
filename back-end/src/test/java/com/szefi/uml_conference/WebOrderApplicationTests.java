package com.szefi.uml_conference;

import com.szefi.uml_conference.editor.model.do_related.line.BreakPoint;
import com.szefi.uml_conference.model.converter.BreakPointListConverter;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
public class WebOrderApplicationTests {
   
 
  BreakPointListConverter conv=new BreakPointListConverter();
  @Test
  void testBreakpointConvertToString(){
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

}
