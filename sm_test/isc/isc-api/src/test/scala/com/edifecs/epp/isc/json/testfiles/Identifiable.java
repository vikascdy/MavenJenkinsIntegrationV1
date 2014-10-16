package com.edifecs.epp.isc.json.testfiles;

import java.io.Serializable;

public interface Identifiable<I> extends Serializable {

    I getId();

}