package com.edifecs.epp.flexfields.api;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by sandeep.kath on 6/2/2014.
 */
public class FlexFieldManifestParsingTest {

    FlexFieldRegistry flexFieldRegistry;

    @Test
    public void manifestFlexFieldParsingTest() throws Exception {
        flexFieldRegistry = new FlexFieldRegistry(null);
        InputStream fileInputStream = new FileInputStream(new File(getClass().getResource("/SAMPLE-MANIFEST_TEST.yaml").toURI()));
        flexFieldRegistry.parseAppManifest(fileInputStream);
    }
}
