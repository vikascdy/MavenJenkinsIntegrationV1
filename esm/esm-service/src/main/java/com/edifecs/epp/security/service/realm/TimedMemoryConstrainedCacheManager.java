// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------

package com.edifecs.epp.security.service.realm;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.MapCache;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.util.SoftHashMap;

/**
 * Created by willclem on 3/11/14.
 */
public class TimedMemoryConstrainedCacheManager extends MemoryConstrainedCacheManager {

    /**
     * Returns a new {@link org.apache.shiro.cache.MapCache MapCache} instance backed by a {@link org.apache.shiro.util.SoftHashMap}.
     *
     * @param name the name of the cache
     * @return a new {@link org.apache.shiro.cache.MapCache MapCache} instance backed by a {@link org.apache.shiro.util.SoftHashMap}.
     */
    @Override
    protected Cache createCache(String name) {
        return new MapCache<Object, Object>(name, new SoftHashMap<Object, Object>());
    }


}
