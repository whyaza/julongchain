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
package org.bcia.julongchain.consenter.entity;

import org.bcia.julongchain.protos.common.Common;

/**
 * 批次消息实体
 *
 * @author zhangmingyang
 * @Date: 2018/3/15
 * @company Dingxuan
 */
public class BatchesMes {
    public Common.Envelope[][] messageBatches;
    public boolean pending;

    public Common.Envelope[][] getMessageBatches() {
        return messageBatches;
    }

    public void setMessageBatches(Common.Envelope[][] messageBatches) {
        this.messageBatches = messageBatches;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
