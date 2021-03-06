/*
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
package org.bcia.julongchain.core.common.smartcontractprovider;

import com.google.protobuf.ByteString;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.exception.SmartContractException;
import org.bcia.julongchain.common.exception.SysSmartContractException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.core.node.NodeConfigFactory;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.Query;
import org.bcia.julongchain.protos.node.SmartContractPackage;

import java.io.*;

/**
 *　智能合约服务提供者类
 *
 * @author sunianle, sunzongyu
 * @date 3/7/18
 * @company Dingxuan
 */
public class SmartContractProvider {
    private static JulongChainLog log = JulongChainLogFactory.getLog(SmartContractProvider.class);

    public static String smartContractInstallPath = NodeConfigFactory.getNodeConfig().getNode().getFileSystemPath();

    /**
     * 给静态变量smartContractInstallPath设置值为智能合约路径
     */
    public static String getSmartContractPath(String path){
        File file = new File(path);
        //不存在则创建mkdir
        if(!file.exists()){
            if(file.mkdir()){
                throw new RuntimeException("Fail to create smartcontract install path " + path);
            }
        }
        if(!file.isDirectory()){
            throw new RuntimeException("Smartcontract install path is exists but not a dir: " + path);
        }
        smartContractInstallPath = path;
        return path;
    }

    /**
     * 根据SmartContract name、SmartContract version获取到文件系统中存储的SmartContract
     * @param scName SmartContract name
     * @param scVersion SmartContract version
     * @return 文件系统中存储的SmartContract
     */
    public static byte[] getSmartContractPackage(String scName, String scVersion) throws JulongChainException {
        //获取文件路径
        String path = String.format("%s/%s.%s", smartContractInstallPath, scName, scVersion);
        InputStream is = null;
        File file = new File(path);
        try {
            is = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new JulongChainException(e);
        }
        byte[] scBytes = new byte[(int) file.length()];
        try {
            is.read(scBytes);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new JulongChainException(e);
        }
        return scBytes;
    }

    /**
     * 根据SmartContract name、SmartContract version判断智能合约是否存在
     * @param scName SmartContract name
     * @param scVersion SmartContract version
     * @return 智能合约存在性
     * @throws JulongChainException
     */
    public static boolean smartContractPackageExists(String scName, String scVersion) throws JulongChainException {
        String path = String.format("%s/%s.%s", smartContractInstallPath, scName, scVersion);
        File file = new File(path);
        if(file.exists()){
            return true;
        }
        return false;
    }

    /**
	 * 获取打包的智能合约
     */
    public static ISmartContractPackage getSmartContractPackage(byte[] buf) throws JulongChainException {
        ISmartContractPackage smartContractPackage = new SDSPackage();

        try {
            smartContractPackage.initFromBuffer(buf);
        } catch (JulongChainException e) {
            log.warn("try signed CDS");
            smartContractPackage = new SignedSDSPackage();
            smartContractPackage.initFromBuffer(buf);
        }

        return smartContractPackage;
    }

    /**
	 * 在世界状态数据库中获取智能合约包
	 * 依赖couchDB
     */

    public static byte[] extractStateDBArtifactsFromSCPackage(ISmartContractPackage scPack) throws JulongChainException {
        SmartContractPackage.SmartContractDeploymentSpec sds = scPack.getDepSpec();
        ByteString bytes = sds.getCodePackage();
        return bytes.toByteArray();
    }

	/**
	 * 在文件系统中获取智能合约
	 */
    public static ISmartContractPackage getSmartContractFromFS(String name, String version) throws JulongChainException {
        ISmartContractPackage pack = null;
        try {
            pack = new SDSPackage();
            pack.initFromFS(name, version);
        } catch (JulongChainException e) {
            try {
                pack = new SignedSDSPackage();
                pack.initFromFS(name, version);
            } catch (JulongChainException e1) {
                throw e;
            }
        }
        return pack;
    }

	/**
	 * 获取已安装的智能合约
	 */
	public static Query.SmartContractQueryResponse getInstalledSmartcontracts() throws JulongChainException {
        File scInstallDir = new File(smartContractInstallPath);
        File[] files = scInstallDir.listFiles();
        Query.SmartContractQueryResponse.Builder builder = Query.SmartContractQueryResponse.newBuilder();
        for(File file : files){
            String[] fileNameArray = file.getName().split("\\.", 2);
            if(fileNameArray.length == 2){
                String scName = fileNameArray[0];
                String scVersion = fileNameArray[1];
                ISmartContractPackage scPackage;
                try {
                    scPackage = getSmartContractFromFS(scName, scVersion);
                } catch (JulongChainException e) {
                    log.error("Unreadable smartcontract file found on filesystem: " + file.getName());
                    continue;
                }
                SmartContractPackage.SmartContractDeploymentSpec scds = scPackage.getDepSpec();
                String name = scds.getSmartContractSpec().getSmartContractId().getName();
                String version = scds.getSmartContractSpec().getSmartContractId().getVersion();
                if(!name.equals(scName) || !version.equals(scVersion)){
                    log.error("Smartcontract file's name/version has been modified on the file system: " + file.getName());
                    continue;
                }
                String path = scds.getSmartContractSpec().getSmartContractId().getPath();
                String input = "";
                String essc = "";
                String vssc = "";
                Query.SmartContractInfo scInfo = Query.SmartContractInfo.newBuilder()
                        .setName(name)
                        .setVersion(version)
                        .setPath(path)
                        .setInput(input)
                        .setEssc(essc)
                        .setVssc(vssc)
                        .setId(ByteString.copyFrom(scPackage.getId()))
                        .build();
                builder.addSmartContracts(scInfo);
            }
        }
        return builder.build();
    }

    /**
	 * 判断智能合约是否已经部署
     */
    public static boolean isSmartContractDeployed(String groupID,String smartContractName,
                                                  String smartContractVersion,
                                                  byte[] smartContractHash)throws SmartContractException {
        return true;
    }

    /**
	 * 提取智能合约
     */
    public static byte[] extractStatedbArtifactsForSmartContract(String scName, String scVersion) throws JulongChainException {
        ISmartContractPackage scPackage;
        try {
            scPackage = getSmartContractFromFS(scName, scVersion);
        } catch (JulongChainException e) {
            log.info("Error while loading installation package for scname {}, scversion {}. Err {}", scName, scVersion, e.getMessage());
            return null;
        }
        return extractStateDBArtifactsFromSCPackage(scPackage);
    }
	/**
	 * 获取智能合约上下文
	 */
    public static SmartContractContext getSCContext(String groupID, String name, String version, String txid, boolean syssc, ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal proposal) throws SysSmartContractException{
		if (StringUtils.isEmpty(version)) {
			log.error("---empty version---");
			log.error("group=[{}], sc=[{}], txid=[{}]", groupID, name, txid);
			throw new SysSmartContractException("Got empty version when getSCContext");
		}
		SmartContractContext sccid = new SmartContractContext(groupID,
				name,
				version,
				txid,
				syssc,
				signedProp,
				proposal);
		sccid.setCanonicalName(name + ":" + version);
		log.debug("NewSCCC (group=[{}], sc=[{}], version=[{}], txid=[{}], canName=[{}]", groupID, name, version, txid, name + ":" + version);
		return sccid;
	}

}
