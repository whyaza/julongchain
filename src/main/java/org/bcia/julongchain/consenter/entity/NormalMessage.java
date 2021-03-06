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
 * 普通消息
 *
 * @author zhangmingyang
 * @date 2018/08/30
 * @company Dingxuan
 */
public class NormalMessage extends Message {
    public NormalMessage(long configSeq, Common.Envelope message) {
        super(configSeq, message);
    }

}