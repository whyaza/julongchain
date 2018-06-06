package org.bcia.javachain.common.policycheck;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.policycheck.bean.SignedProposal;
import org.bcia.javachain.common.policycheck.policies.GroupPolicyManager;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.common.util.proto.TxUtils;
import org.bcia.javachain.msp.IIdentityDeserializer;
import org.bcia.javachain.msp.mgmt.IMspPrincipalGetter;
import org.bcia.javachain.msp.mgmt.MspManager;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 23/05/18
 * @company Aisino
 */
public class PolicyCheckerTest {
  /*  @Mock
    PolicyManager mockPolicyManager;
    @Mock
    IGroupPolicyManagerGetter iChannelPolicyManagerGetter;
    @Mock
    IIdentityDeserializer localMSP;
    @Mock
    MspPrincipal.MSPPrincipal principalGetter;*/


    @Before
    public void setUp() {


        System.out.println("setup...");
    }

    @Test
    public void checkPolicy() throws InvalidProtocolBufferException, JavaChainException, UnsupportedEncodingException {
        IIdentityDeserializer localMSP = mock(IIdentityDeserializer.class);
        IMspPrincipalGetter principalGetter = mock(IMspPrincipalGetter.class);
        PolicyChecker policyChecker = new PolicyChecker(new GroupPolicyManager(),localMSP,principalGetter);
        ProposalPackage.SignedProposal sp = TxUtils.mockSignedEndorserProposalOrPanic("",
                Smartcontract.SmartContractSpec.newBuilder().build());
        policyChecker.checkPolicy("A", "Admins",sp);


    }

    @Test
    public void checkPolicyBySignedData() {
        IIdentityDeserializer localMSP = mock(IIdentityDeserializer.class);
        IMspPrincipalGetter principalGetter = mock(IMspPrincipalGetter.class);
        PolicyChecker policyChecker = new PolicyChecker(new GroupPolicyManager(),localMSP,principalGetter);
        List<SignedData> sd = new ArrayList<SignedData>();
        //policyChecker.checkPolicyBySignedData("","admin",sd);
        //policyChecker.checkPolicyBySignedData("A","",sd);


    }

    @Test
    public void checkPolicyNoChannel() throws JavaChainException {
        String reader = "readers";
        IIdentityDeserializer localMSP = mock(IIdentityDeserializer.class);
        MspManager mspManager = new MspManager();
        IMspPrincipalGetter principalGetter = mock(IMspPrincipalGetter.class);
        PolicyChecker policyChecker = new PolicyChecker(new GroupPolicyManager(),mspManager,principalGetter);
        ProposalPackage.SignedProposal sp = TxUtils.mockSignedEndorserProposalOrPanic("",
                Smartcontract.SmartContractSpec.newBuilder().build());
        policyChecker.checkPolicyNoGroup("Admins",sp);
    }


}