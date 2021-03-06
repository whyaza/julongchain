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

import java.io.Serializable;

/**
 * 配置消息
 * @author zhangmingyang
 * @Date: 2018/3/15
 * @company Dingxuan
 */
public class ConfigMsg implements Serializable{
    private Common.Envelope config;
    private long configSeq;

    public ConfigMsg() {
    }

    public ConfigMsg(Common.Envelope config, long configSeq) {
        this.config = config;
        this.configSeq = configSeq;
    }

    public Common.Envelope getConfig() {
        return config;
    }

    public void setConfig(Common.Envelope config) {
        this.config = config;
    }

    public long getConfigSeq() {
        return configSeq;
    }

    public void setConfigSeq(long configSeq) {
        this.configSeq = configSeq;
    }
}
