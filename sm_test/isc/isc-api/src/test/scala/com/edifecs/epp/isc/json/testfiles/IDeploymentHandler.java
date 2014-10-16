package com.edifecs.epp.isc.json.testfiles;

import com.edifecs.epp.isc.annotations.CommandHandler;
import com.edifecs.epp.isc.annotations.JsonSerialization;
import com.edifecs.epp.isc.annotations.TypeAdapter;

/**
 * Created by willclem on 6/5/2014.
 */

@CommandHandler (
        name = "something"
)
@JsonSerialization(adapters = {
    @TypeAdapter(value = DeploymentSerializer.class)
})
public class IDeploymentHandler {

}
