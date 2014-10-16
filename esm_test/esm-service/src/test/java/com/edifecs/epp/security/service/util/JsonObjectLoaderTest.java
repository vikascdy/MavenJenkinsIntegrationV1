package com.edifecs.epp.security.service.util;

import com.edifecs.epp.security.service.customfield.CustomFieldConfig;
import com.edifecs.epp.security.service.customfield.CustomFieldDef;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class JsonObjectLoaderTest {
    @Test
    public void testLoadStream() {
        InputStream stream = this.getClass().getResourceAsStream("/custom-fields.json");
        CustomFieldConfig config = JsonObjectLoader.load(stream, CustomFieldConfig.class);
        Assert.assertNotNull(config);
        Assert.assertSame(1, config.getCustomFieldDefs().size());

        CustomFieldDef def = config.getCustomFieldDefs().get(0);
        Assert.assertSame(2, def.getEventConfigs().size());
    }

    @Test
    public void testLoadFile() throws Exception {
        URL url = this.getClass().getResource("/custom-fields.json");
        File file = new File(url.toURI());
        CustomFieldConfig config = JsonObjectLoader.load(file, CustomFieldConfig.class);
        Assert.assertNotNull(config);
    }

    @Test
    public void testLoadFileNotFound() throws Exception {
        File file = new File("somebogusfile" + hashCode());
        CustomFieldConfig config = JsonObjectLoader.load(file, CustomFieldConfig.class);
        Assert.assertNull(config);
    }
}