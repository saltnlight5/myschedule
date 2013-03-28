package myschedule.quartz.extra.util;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Zemian Deng
 */
public class ScriptingUtilsTest {
    @Test
    public void testRunScriptText() throws Exception {
        String text = "1 + 1;";
        Object result = ScriptingUtils.runScriptText("JavaScript", text, null);
        Assert.assertThat(result, Matchers.instanceOf(Number.class));
        int resultInt = ((Number)result).intValue();
        Assert.assertThat(resultInt, Matchers.is(2));

        text = "importClass(Packages.myschedule.quartz.extra.job.LoggerJob);\n" +
                "var job = new LoggerJob();\n" +
                "job.toString();";
        result = ScriptingUtils.runScriptText("JavaScript", text, null);
        Assert.assertThat(result, Matchers.instanceOf(String.class));
        Assert.assertThat((String) result, Matchers.startsWith("myschedule.quartz.extra.job.LoggerJob"));
    }

    @Test
    public void testRunScriptFile() throws Exception {
        Object result = ScriptingUtils.runScriptFile("JavaScript",
                "classpath:myschedule/quartz/extra/util/ScriptingUtils-test.js", null);
        Assert.assertThat(result, Matchers.instanceOf(String.class));
        Assert.assertThat((String) result, Matchers.is(System.getProperty("user.name")));
    }
}
