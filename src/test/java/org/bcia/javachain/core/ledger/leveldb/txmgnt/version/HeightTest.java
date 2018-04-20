/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.javachain.core.ledger.leveldb.txmgnt.version;

import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/08
 * @company Dingxuan
 */
public class HeightTest {

    Height height = new Height();

    @Test
    public void toBytes(){
        height.setBlockNum((long)100000000);
        height.setTxNum((long)200000000);
        byte[] bytes = height.toBytes();
        for(int i = 0; i < bytes.length; i++){
            System.out.print(bytes[i] + " ");
        }
    }

    @Test
    public void newHeightFromBytes(){
        height.setBlockNum((long)100000000);
        height.setTxNum((long)200000000);
        byte[] bytes = height.toBytes();
        for(int i = 0; i < bytes.length; i++){
            System.out.print(bytes[i] + " ");
        }
        System.out.println();
        Height height1 = Height.newHeightFromBytes(bytes);
        System.out.println(height1.getTxNum());
        System.out.println(height1.getBlockNum());
    }
}