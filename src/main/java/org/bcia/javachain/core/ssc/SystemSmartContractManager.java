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
package org.bcia.javachain.core.ssc;

import org.bcia.javachain.common.exception.SysSmartContractException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ssc.cssc.CSSC;
import org.bcia.javachain.core.ssc.essc.ESSC;
import org.bcia.javachain.core.ssc.lssc.LSSC;
import org.bcia.javachain.core.ssc.qssc.QSSC;
import org.bcia.javachain.core.ssc.vssc.VSSC;
import org.bcia.javachain.protos.node.Smartcontract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统智能合约管理器,整合管理功能函数接口
 * * 各接口被调用的时机：
 * 　1.Node启动时，调用registerSysSmartContracts()
 * 2.（SystemSmartContractManager内部）,调用loadSysSmartContracts()加载外部系统合约，与本地系统合约形成系统合约集合；
 * ３.（SystemSmartContractManager内部）,对每个系统合约，调用registerSysSmartContract(String smartContractID)
 * ４.调用deploySysSmartContracts("");
 * ５.(SystemSmartContractManager内部)deploySysSmartContracts调用buildSysSmartContracts编译智能合约，形成智能合约部署规范(DeploymentSpec);
 * ６.分别为每个组，调用deploySysSmartContracts(groupID);
 * ７.(SystemSmartContractManager内部)deploySysSmartContracts(groupID)调用buildSysSmartContracts编译智能合约，形成智能合约部署规范(DeploymentSpec);
 *
 * @author sunianle
 * @date 3/6/18
 * @company Dingxuan
 */

@Component
public class SystemSmartContractManager implements ISystemSmartContractManager {
    private SystemSmartContractDescriptor[] embedContractDescriptors = new SystemSmartContractDescriptor[5];
    private Map<String, ISystemSmartContract> map = new HashMap<String, ISystemSmartContract>();
    private static JavaChainLog log = JavaChainLogFactory.getLog(SystemSmartContractManager.class);
    @Autowired
    private CSSC cssc;
    @Autowired
    private ESSC essc;
    @Autowired
    private LSSC lssc;
    @Autowired
    private QSSC qssc;
    @Autowired
    private VSSC vssc;

    @Autowired
    public SystemSmartContractManager() {
        log.debug("Construct systemSmartContractManager");
    }

    @PostConstruct
    private void init() {
        log.debug("Init systemSmartContractManager");
        String[] args = new String[0];
        embedContractDescriptors[0] = new SystemSmartContractDescriptor(
                "cssc",
                "core/ssc/cssc",
                args,
                true,
                false,
                true
        );
        embedContractDescriptors[1] = new SystemSmartContractDescriptor(
                "essc",
                "core/ssc/essc",
                args,
                false,
                false,
                true
        );
        embedContractDescriptors[2] = new SystemSmartContractDescriptor(
                "lssc",
                "core/ssc/lssc",
                args,
                true,
                true,
                true
        );
        embedContractDescriptors[3] = new SystemSmartContractDescriptor(
                "qssc",
                "core/ssc/qssc",
                args,
                true,
                true,
                true
        );
        embedContractDescriptors[4] = new SystemSmartContractDescriptor(
                "vssc",
                "core/ssc/vssc",
                args,
                false,
                false,
                true
        );
        cssc.setSystemSmartContractDescriptor(embedContractDescriptors[0]);
        essc.setSystemSmartContractDescriptor(embedContractDescriptors[1]);
        lssc.setSystemSmartContractDescriptor(embedContractDescriptors[2]);
        qssc.setSystemSmartContractDescriptor(embedContractDescriptors[3]);
        vssc.setSystemSmartContractDescriptor(embedContractDescriptors[4]);
    }


    @Override
    public void registerSysSmartContracts() {
        log.info("Register system contracts");
    }

    /**
     * 注册系统智能合约,相当于应用智能合约的Install
     *
     * @param contract 要注册的系统合约
     * @return 是否注册成功
     */
    private boolean registerSysSmartContract(ISystemSmartContract contract) {
        String contractID = contract.getSmartContractID();
        log.info("Register system contract [%s]", contractID);
        map.put(contractID, contract);
        return true;
    }

    //相当于应用智能合约的Instantiate
    @Override
    public void deploySysSmartContracts(String groupID) {

    }

    @Override
    public void deDeploySysSmartContracts(String groupID) {

    }

    @Override
    public boolean isSysSmartContract(String smartContractID) {
        return false;
    }

    @Override
    public boolean isWhitelisted(ISystemSmartContract contract) {
        return false;
    }

    @Override
    public ISystemSmartContract getSystemSmartContract(String smartContractID) {
        return null;
    }


    //编译智能合约，形成智能合约部署规范(DeploymentSpec);
    private Smartcontract.SmartContractDeploymentSpec buildSysSmartContract(Smartcontract.SmartContractSpec spec)
            throws SysSmartContractException {
        return null;
    }

    //加载外部系统智能合约插件
    private void loadSysSmartContracts() {

    }
}
