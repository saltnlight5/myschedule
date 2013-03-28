package myschedule.quartz.extra.util;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author Zemian Deng
 */
public class UtilsTest {

    @Test
    public void testToMap() throws Exception {
        Map<String, Object> map = Utils.toMap("a", "one", "b", "two", "c", "three");
        Assert.assertThat(map.size(), Matchers.is(3));
        Assert.assertThat(map.keySet(), Matchers.hasItems("a", "b", "c"));
        Assert.assertThat(map.values(), Matchers.hasItems((Object)"one", "two", "three"));

        map = Utils.toMap();
        Assert.assertThat(map.size(), Matchers.is(0));
    }
}
