package amberdb.query;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class PropertyQueryAssemblerTest {
    @Test
    public void sql(){
        List<WorkProperty> list = new ArrayList<>();
        list.add(new WorkProperty("title", "abc", true));
        list.add(new WorkProperty("collection", "nla.pic"));
        list.add(new WorkProperty("recordSource", "voyager"));
        PropertyQueryAssembler assembler = new PropertyQueryAssembler(list);
        assertThat(assembler.sql(), is("select id from work where title=? and collection=? and recordSource=? "));
    }
}