package org.bcia.javachain.consenter.common.deliver;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.common.localmsp.impl.LocalSigner;
import org.bcia.javachain.common.util.proto.EnvelopeHelper;
import org.bcia.javachain.protos.common.Common;

import org.bcia.javachain.protos.consenter.Ab;
import org.springframework.stereotype.Component;

import static org.bcia.javachain.protos.consenter.AtomicBroadcastGrpc.*;

/**
 * deliver客户端
 *
 * @author zhangmingyang
 * @date 2018-02-23
 * @company Dingxuan
 */
@Component
public class DeliverClient {
    public static void main(String[] args) throws Exception {
        String ip="localhost";
        String message="aba";
        int port=7050;

        System.out.println("begin");

        ManagedChannel managedChannel= ManagedChannelBuilder.forAddress(ip,port).usePlaintext(true).build();
        AtomicBroadcastStub stub= newStub(managedChannel);
        StreamObserver<Common.Envelope> envelopeStreamObserver=stub.deliver(new StreamObserver<Ab.DeliverResponse>() {
            @Override
            public void onNext(Ab.DeliverResponse deliverResponse) {
                if(deliverResponse.getStatusValue()==500){
                    System.out.println("INTERNAL_SERVER_ERROR");
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompled!");
            }
        });

            //客户端以流式的形式向服务器发送数据
           // envelopeStreamObserver.onNext(Common.Envelope.newBuilder().setPayload(ByteString.copyFrom(message.getBytes())).build());

        ILocalSigner localSigner = new LocalSigner();
        Common.GroupHeader  data = EnvelopeHelper.buildGroupHeader(Common.HeaderType.CONFIG_UPDATE_VALUE, 0,
                "myGroup", 30);

        Common.Payload payload = EnvelopeHelper.buildPayload(Common.HeaderType.CONFIG_UPDATE_VALUE, 0, "myGroup", localSigner, data, 30);

        envelopeStreamObserver.onNext(Common.Envelope.newBuilder().setPayload(payload.toByteString()).build());


        Thread.sleep(1000);

    }
}
