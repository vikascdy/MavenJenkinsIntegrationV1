package com.edifecs.epp.flexfields.api;

import com.edifecs.epp.flexfields.model.*;

import java.util.Map;

/**
 * Created by sandeep.kath on 5/2/2014.
 */
public interface IFlexFieldManager {

    /**
     * Set FlexField Value
     * @param flexFieldValue
     * @returns FlexFieldValue
     * @throws Exception
     */
    FlexFieldValue setFlexFieldValue(FlexFieldValue flexFieldValue) throws Exception;


    /**
     * Get FlexField Value
     * @param id
     * @return FlexFieldValue object
     * @throws Exception
     */
    FlexFieldValue getFlexFieldValue(long id) throws Exception;


    /**
     * Get FlexField Values for given Flex Group
     * @param flexGroup
     * @return Map of Flex Field Definition as Key and FlexFieldValue as value object
     * @throws Exception
     */
    Map<FlexFieldDefinition, FlexFieldValue> getFlexFieldValues(FlexGroup flexGroup) throws Exception;



}
