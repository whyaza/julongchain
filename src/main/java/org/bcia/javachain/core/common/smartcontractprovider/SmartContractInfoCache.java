/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.core.common.smartcontractprovider;

import org.bcia.javachain.common.exception.SmartContractException;
import org.bcia.javachain.protos.node.SmartContractDataPackage;

import java.util.Map;

/**
 * SmartContractInfoCache implements in-memory cache for SmartContractData
* needed by endorser to verify if the local instantiation policy
* matches the instantiation policy on a channel before honoring
* an invoke
 * @author sunianle
 * @date 5/10/18
 * @company Dingxuan
 */
public class SmartContractInfoCache {
    private Map<String,SmartContractDataPackage.SmartContractData> cache;
    private ISmartContractCacheSupport cacheSuppot;

    public SmartContractInfoCache(ISmartContractCacheSupport cacheSuppot) {
        this.cacheSuppot = cacheSuppot;
    }

    SmartContractDataPackage.SmartContractData getSmartContractData(String name,String version) throws SmartContractException{
        return null;
    }
}
