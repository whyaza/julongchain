/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.javachain.node.entity;

import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.genesis.GenesisBlockFactory;
import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.common.localmsp.impl.LocalSigner;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.CommConstant;
import org.bcia.javachain.common.util.FileUtils;
import org.bcia.javachain.common.util.proto.ProposalUtils;
import org.bcia.javachain.consenter.common.broadcast.BroadCastClient;
import org.bcia.javachain.common.util.proto.EnvelopeHelper;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.javachain.core.ssc.cssc.CSSC;
import org.bcia.javachain.msp.ISigningIdentity;
import org.bcia.javachain.msp.mgmt.GlobalMspManagement;
import org.bcia.javachain.node.Node;
import org.bcia.javachain.node.common.client.*;
import org.bcia.javachain.node.common.helper.SpecHelper;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.protos.consenter.Ab;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.Query;
import org.bcia.javachain.protos.node.Smartcontract;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfigFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 节点群组能力
 *
 * @author zhouhui wanglei
 * @date 2018/2/23
 * @company Dingxuan
 */
@Component
public class NodeGroup {
    private static JavaChainLog log = JavaChainLogFactory.getLog(NodeGroup.class);

    private static final String PROFILE_CREATE_GROUP = "SampleSingleMSPGroup";

    private Node node;

    public NodeGroup() {
    }

    public NodeGroup(Node node) {
        this.node = node;
    }

    /**
     * 创建群组
     *
     * @param host      共识节点域名或IP
     * @param port      共识节点端口
     * @param groupId   群组ID
     * @param groupFile 群组配置文件
     * @return
     * @throws NodeException
     */
    public NodeGroup createGroup(String host, int port, String groupId, String groupFile) throws NodeException {
        Common.Envelope envelope = null;

        ILocalSigner signer = new LocalSigner();
        if (StringUtils.isNotBlank(groupFile) && FileUtils.isExists(groupFile)) {
            //如果群组文件存在，则直接从文件读取，形成信封对象
            envelope = EnvelopeHelper.readFromFile(groupFile);
        } else if (StringUtils.isBlank(groupFile)) {
            //如果是空文件，则组成一个默认的信封对象
            try {
                envelope = EnvelopeHelper.makeGroupCreateTx(groupId, signer, null, GenesisConfigFactory
                        .loadGenesisConfig().getCompletedProfile(PROFILE_CREATE_GROUP));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new NodeException(e);
            }

        } else {
            //不是空文件，反而是一个错误的文件，则直接报异常（要么不指定文件，要么就指定正确的文件）
            log.error("groupFile is not exists: " + groupFile);
            throw new NodeException("Group File is not exists");
        }

        Common.Envelope signedEnvelope = EnvelopeHelper.sanityCheckAndSignConfigTx(envelope, groupId, signer);
        IBroadcastClient broadcastClient = new BroadcastClient(host, port);
        broadcastClient.send(signedEnvelope, new StreamObserver<Ab.BroadcastResponse>() {
            @Override
            public void onNext(Ab.BroadcastResponse value) {
                log.info("Broadcast onNext");
                //收到响应消息，判断是否是200消息
                if (Common.Status.SUCCESS.equals(value.getStatus())) {
                    getGenesisBlockThenWrite(host, port, groupId);
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error(t.getMessage(), t);
            }

            @Override
            public void onCompleted() {
                log.info("Broadcast completed");
            }
        });

        return null;
    }

    private void getGenesisBlockThenWrite(String ip, int port, String groupId) {
        log.info("getGenesisBlock begin");
        IDeliverClient deliverClient = new DeliverClient(ip, port);
        deliverClient.getSpecifiedBlock(groupId, 0L, new StreamObserver<Ab.DeliverResponse>() {
            @Override
            public void onNext(Ab.DeliverResponse value) {
                log.info("Deliver onNext");

                //测试用
                if(true){
                    try {
                        Common.Block block = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance())
                                .getGenesisBlock(groupId);
                        FileUtils.writeFileBytes(groupId + ".block", block.toByteArray());
                        LedgerManager.createLedger(block);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    } catch (JavaChainException e) {
                        log.error(e.getMessage(), e);
                    }
                }

                if (value.hasBlock()) {
                    Common.Block block = value.getBlock();
                    try {
                        FileUtils.writeFileBytes(groupId + ".block", block.toByteArray());
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                } else {
                    log.info("Deliver status:" + value.getStatus().getNumber());
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error(t.getMessage(), t);
            }

            @Override
            public void onCompleted() {
                log.info("Deliver onCompleted");
            }
        });


    }

    public NodeGroup joinGroup(String blockPath) throws NodeException {
        NodeGroup group = new NodeGroup();

        Smartcontract.SmartContractInvocationSpec spec = null;
        try {
            spec = SpecHelper.buildInvocationSpec(CommConstant.CSSC, CSSC.JOIN_GROUP,
                    FileUtils.readFileBytes(blockPath));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Can not read block file");
        }

        ISigningIdentity identity = null;// new Mgmt().getLocalMsp().getDefaultSigningIdentity();

        //ISigningIdentity identity = new MockSigningIdentity();
        byte[] creator = identity.serialize();

        byte[] nonce = MockCrypto.getRandomNonce();


        String txId = null;
        try {
            txId = ProposalUtils.computeProposalTxID(creator, nonce);
        } catch (JavaChainException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Generate txId fail");
        }

        ProposalPackage.Proposal proposal = ProposalUtils.buildSmartContractProposal(Common.HeaderType.CONFIG,
                "", txId, spec, nonce, creator, null);
        ProposalPackage.SignedProposal signedProposal = ProposalUtils.buildSignedProposal(proposal, identity);

        EndorserClient endorserClient = new EndorserClient("localhost", 7051);
        ProposalResponsePackage.ProposalResponse proposalResponse = endorserClient.sendProcessProposal(signedProposal);

        if (proposalResponse != null && proposalResponse.getResponse() != null && proposalResponse.getResponse()
                .getStatus() == Common.Status.SUCCESS_VALUE) {
            log.info("Join group success");
        } else {
            log.info("Join group fail:" + proposalResponse);
        }

        return group;
    }

    /**
     * 更新群组配置 V0.25
     */
    public NodeGroup updateGroup(String ip, int port, String groupId) {
        NodeGroup group = new NodeGroup();

        BroadCastClient broadCastClient = new BroadCastClient();
        try {
            //broadCastClient.send(ip, port, groupId, this);
            broadCastClient.send(ip, port, Common.Envelope.newBuilder().build(), null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return group;
    }

    /**
     * 加入群组列表 V0.25
     */
    public List<Query.GroupInfo> listGroups(String smartContractName, String action, byte[] content) throws NodeException {
        //生成proposal  Type=ENDORSER_TRANSACTION
        Smartcontract.SmartContractInvocationSpec spec = SpecHelper.buildInvocationSpec(smartContractName, action, content);

        ISigningIdentity identity = GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity();
        byte[] creator = identity.serialize();

        byte[] nonce = MockCrypto.getRandomNonce();

        String txId = null;
        try {
            txId = ProposalUtils.computeProposalTxID(creator, nonce);
        } catch (JavaChainException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Generate txId fail");
        }

        //生成proposal  Type=ENDORSER_TRANSACTION
        ProposalPackage.Proposal proposal = ProposalUtils.buildSmartContractProposal(Common.HeaderType.ENDORSER_TRANSACTION,
                "", txId, spec, nonce, creator, null);
        ProposalPackage.SignedProposal signedProposal = ProposalUtils.buildSignedProposal(proposal, identity);

        //获取背书节点返回信息
        EndorserClient client = new EndorserClient(CSSC.DEFAULT_HOST, CSSC.DEFAULT_PORT);
        ProposalResponsePackage.ProposalResponse proposalResponse = client.sendProcessProposal(signedProposal);

/*        //获取结果中 Payload
        Common.Payload payload = null;
        try {
            payload = Common.Payload.parseFrom(proposalResponse.getResponse().getPayload());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        //获取Payload 中的groupHeader
        Common.GroupHeader groupHeader = null;
        try {
            groupHeader = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        //return groupHeader.getGroupId();
*/
        Query.GroupQueryResponse groupQueryResponse = null;
        try {
            groupQueryResponse = Query.GroupQueryResponse.parseFrom(proposalResponse.getResponse().getPayload());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return groupQueryResponse.getGroupsList();


    }

}
