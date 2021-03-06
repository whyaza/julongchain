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
package org.bcia.julongchain.gossip.comm;

import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.core.smartcontract.shim.helper.Channel;
import org.bcia.julongchain.gossip.common.IMessageAcceptor;
import org.bcia.julongchain.gossip.gossip.IReceivedMessage;
import org.bcia.julongchain.gossip.gossip.SignedGossipMessage;

import java.time.Duration;

/**
 * class description
 *
 * @author wanliangbing
 * @date 18-7-24
 * @company Dingxuan
 */
public interface IComm {

    public byte[] getPKIid();

    public void send(SignedGossipMessage msg, RemoteNode... nodes);

    public SendResult[] sendWithAck(SignedGossipMessage msg, Duration timeout, Integer minAck, RemoteNode... nodes);

    public void probe(RemoteNode node) throws GossipException;

    public byte[] handshake(RemoteNode node) throws GossipException;

    public Channel<IReceivedMessage> accept(IMessageAcceptor messageAcceptor);

    public Channel<byte[]> presumedDead();

    public void closeConn(RemoteNode node);

    public void stop();

}
