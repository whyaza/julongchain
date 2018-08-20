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
package org.bcia.julongchain.gossip.api;

/**
 * PeerSignature defines a signature of a peer
 * on a given message
 *
 * @author wanliangbing
 * @date 2018/08/20
 * @company Dingxuan
 */
public class PeerSignature {

    private byte[] signature;
    private byte[] message;
    private byte[] peerIdentityType;

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public byte[] getPeerIdentityType() {
        return peerIdentityType;
    }

    public void setPeerIdentityType(byte[] peerIdentityType) {
        this.peerIdentityType = peerIdentityType;
    }
}
