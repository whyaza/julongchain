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
 *
 * Modified java_package and other contents by Dingxuan on 2018-08-30
 */
package org.bcia.julongchain.core.smartcontract.shim.impl;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.util.Utils;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContract;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.julongchain.core.smartcontract.shim.ledger.CompositeKey;
import org.bcia.julongchain.core.smartcontract.shim.ledger.IKeyModification;
import org.bcia.julongchain.core.smartcontract.shim.ledger.IKeyValue;
import org.bcia.julongchain.core.smartcontract.shim.ledger.IQueryResultsIterator;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.SmartContractEventPackage;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MockStub is an implementation of ChaincodeStubInterface for unit testing chaincode.
 *  Use this instead of ChaincodeStub in your chaincode's unit test calls to Init or Invoke.
 * @author sunianle
 * @date 3/27/18
 * @company Dingxuan
 */
public class MockStub implements ISmartContractStub {
    private static JulongChainLog log = JulongChainLogFactory.getLog(MockStub.class);
    private List<ByteString> args;
    private String name;
    private ISmartContract smartContract;
    private HashMap<String,byte[]> state;
    private List<String> keys;
    String txID;
    Timestamp txTimeStamp;
    ProposalPackage.SignedProposal signedProposal;
    String groupID;

    public MockStub(String name,ISmartContract smartContract){
        log.debug("MockStub({},{})",name,smartContract.getSmartContractID());
        this.name=name;
        this.smartContract=smartContract;
        state=new HashMap<String,byte[]>();
        keys=new ArrayList<String>();
        signedProposal=null;
    }

    @Override
    public List<byte[]> getArgs() {
        return args.stream().map(x -> x.toByteArray()).collect(Collectors.toList());
    }

    @Override
    public List<String> getStringArgs() {
        return args.stream().map(x -> x.toStringUtf8()).collect(Collectors.toList());
    }

    @Override
    public String getFunction() {
        return null;
    }

    @Override
    public List<String> getParameters() {
        return null;
    }

    @Override
    public String getTxId() {
        return null;
    }

    @Override
    public String getGroupId() {
        return null;
    }

    @Override
    public ISmartContract.SmartContractResponse invokeSmartContract(String smartContractName, List<byte[]> args, String group) {
        return null;
    }

    /**
     * GetState retrieves the value for a given key from the ledger
     * @param key
     * @return
     */
    @Override
    public byte[] getState(String key) {

        byte[] state = null;
        try {
            LedgerManager.initialize(null);
            INodeLedger l = LedgerManager.openLedger("myGroup");

            ITxSimulator simulator = l.newTxSimulator("txID");
            state = simulator.getState("lssc", "mycc");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


	    return state;
    }

    @Override
    public void putState(String key, byte[] value) {
        if(this.txID.equals("")){
            String msg=String.format("cannot PutState without a transactions - call stub.MockTransactionStart()?");
            log.error(msg);
            throw new RuntimeException(msg);
        }
        log.debug("MockStub {} Putting {} {}",this.name,key,value);
        state.put(key,value);
        // insert key into ordered list of keys
        keys.add(key);
        Collections.sort(keys);
    }

    @Override
    public void delState(String key) {
        log.debug("MockStub {} Deleting {} {}",this.name,key,state.get(key));
        state.remove(key);
        for(int i=0;i<keys.size();i++){
            if(keys.get(i).compareTo(key)==0){
                keys.remove(i);
                break;
            }
        }
    }

    @Override
    public IQueryResultsIterator<IKeyValue> getStateByRange(String startKey, String endKey) {
        return null;
    }

    @Override
    public IQueryResultsIterator<IKeyValue> getStateByPartialCompositeKey(String compositeKey) {
        return null;
    }

    @Override
    public CompositeKey createCompositeKey(String objectType, String... attributes) {
        return null;
    }

    @Override
    public CompositeKey splitCompositeKey(String compositeKey) {
        return null;
    }

    @Override
    public IQueryResultsIterator<IKeyValue> getQueryResult(String query) {
        return null;
    }

    @Override
    public IQueryResultsIterator<IKeyModification> getHistoryForKey(String key) {
        return null;
    }

    @Override
    public void setEvent(String name, byte[] payload) {

    }

    @Override
    public ISmartContract.SmartContractResponse invokeSmartContract(String smartContractName, List<byte[]> args) {
        return null;
    }

    @Override
    public ISmartContract.SmartContractResponse invokeSmartContractWithStringArgs(String smartContractName, List<String> args, String group) {
        return null;
    }

    @Override
    public ISmartContract.SmartContractResponse invokeSmartContractWithStringArgs(String smartContractName, List<String> args) {
        return null;
    }

    @Override
    public ISmartContract.SmartContractResponse invokeSmartContractWithStringArgs(String smartContractName, String... args) {
        return null;
    }

    @Override
    public String getStringState(String key) {
        return null;
    }

    @Override
    public void putStringState(String key, String value) {

    }

    @Override
    public SmartContractEventPackage.SmartContractEvent getEvent() {
        return null;
    }

    @Override
    public ProposalPackage.SignedProposal getSignedProposal() {
        return signedProposal;
    }

    @Override
    public Instant getTxTimestamp() {
        return null;
    }

    @Override
    public byte[] getCreator() {
        return new byte[0];
    }

    @Override
    public Map<String, byte[]> getTransient() {
        return null;
    }

    @Override
    public byte[] getBinding() {
        return new byte[0];
    }

    public ISmartContract.SmartContractResponse mockInit(String uuid, List<ByteString> args){
        this.args=args;
        mockTransactionStart(uuid);
        ISmartContract.SmartContractResponse smartContractResponse =this.smartContract.init(this);
        mockTransactionEnd(uuid);
        return smartContractResponse;
    }

    public ISmartContract.SmartContractResponse mockInvoke(String uuid, List<ByteString> args){
        this.args=args;
        mockTransactionStart(uuid);
        ISmartContract.SmartContractResponse smartContractResponse =this.smartContract.invoke(this);
        mockTransactionEnd(uuid);
        return smartContractResponse;
    }

    public ISmartContract.SmartContractResponse mockInvokeWithSignedProposal(String uuid, List<ByteString> args,
                                                              ProposalPackage.SignedProposal proposal){
        this.args=args;
        mockTransactionStart(uuid);
        this.signedProposal=proposal;
        ISmartContract.SmartContractResponse smartContractResponse =this.smartContract.invoke(this);
        mockTransactionEnd(uuid);
        return smartContractResponse;
    }

    private void mockTransactionEnd(String uuid) {
        this.signedProposal=null;
        txID="";
    }

    private void mockTransactionStart(String uuid) {
        this.txID=uuid;
        this.signedProposal=ProposalPackage.SignedProposal.newBuilder().build();
        this.txTimeStamp=Utils.createUtcTimeStamp();
    }
}
