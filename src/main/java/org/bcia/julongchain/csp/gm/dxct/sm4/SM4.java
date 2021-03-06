package org.bcia.julongchain.csp.gm.dxct.sm4; /**
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

import org.bcia.julongchain.common.exception.CspException;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.params.KeyParameter;

import java.io.*;
import java.security.*;

/**
 * 国密sm4实现
 *
 * @author zhangmingyang
 * @Date: 2018/4/26
 * @company Dingxuan
 */
public class SM4 {
    /**
     * 加密类型
     */
    public static final int SM4_ENCRYPT = 1;
    /**
     * 解密类型
     */
    public static final int SM4_DECRYPT = 2;

    /**
     * sm4密钥生成
     *
     * @return
     */
    public static byte[] generateKey() {
        KeyGenerationParameters param = new KeyGenerationParameters(new SecureRandom(), 128);
        CipherKeyGenerator cipherKeyGenerator = new CipherKeyGenerator();
        cipherKeyGenerator.init(param);
        byte[] sm4key = cipherKeyGenerator.generateKey();
        return sm4key;
    }

    /**
     * 数据填充,去填充
     *
     * @param input
     * @param mode
     * @return
     */
    private static byte[] padding(byte[] input, int mode) {
        if (input == null) {
            return null;
        }
        byte[] ret = (byte[]) null;
        if (mode == SM4_ENCRYPT) {
            int p = 16 - input.length % 16;
            ret = new byte[input.length + p];
            System.arraycopy(input, 0, ret, 0, input.length);
            for (int i = 0; i < p; i++) {
                ret[input.length + i] = (byte) p;
            }
        } else {
            int p = input[input.length - 1];
            ret = new byte[input.length - p];
            System.arraycopy(input, 0, ret, 0, input.length - p);
        }
        return ret;
    }


    /**
     * ECB模式对数据进行加密
     *
     * @param plainText
     * @return
     */

    public byte[] encryptECB(byte[] plainText, byte[] sm4Key) throws CspException {
        if (plainText == null) {
            throw new CspException("plainText is null");
        }
        if (null == sm4Key) {
            throw new CspException("sm4key is null");
        }
        if (plainText.length == 0) {
            throw new CspException("plainText's length is 0");
        }
        if (sm4Key.length < 16 || sm4Key.length > 16) {
            throw new CspException("sm4key's pattern is wrong!");
        }
        byte[] paddingData = padding(plainText, SM4_ENCRYPT);
        byte[] output = ecbProcessData(paddingData, sm4Key, SM4_ENCRYPT);
        return output;
    }

    /**
     * 对加密的数据进行解密
     *
     * @param encryptData
     * @return
     */
    public byte[] decryptECB(byte[] encryptData, byte[] sm4Key) throws CspException {
        if (null == encryptData) {
            throw new CspException("plainText is null");
        }
        if (encryptData.length == 0) {
            throw new CspException("plainText's length is 0");
        }

        if (null == sm4Key) {
            throw new CspException("sm4key is null");
        }
        if (sm4Key.length == 0) {
            throw new CspException("sm4key's length is 0");
        }

        byte[] output = ecbProcessData(encryptData, sm4Key, SM4_DECRYPT);
        byte[] decrypt = padding(output, SM4_DECRYPT);
        return decrypt;

    }

    /**
     * ecb模式处理消息原文或加密消息
     *
     * @param data
     * @return
     */
    public static byte[] ecbProcessData(byte[] data, byte[] sm4Key, int mode) {
        int length = data.length;
        ByteArrayInputStream bins = new ByteArrayInputStream(data);
        ByteArrayOutputStream bous = new ByteArrayOutputStream();
        SM4Engine engine = new SM4Engine();
        if (mode == SM4_ENCRYPT) {
            engine.init(true, new KeyParameter(sm4Key));
        } else {
            engine.init(false, new KeyParameter(sm4Key));
        }
        for (; length > 0; length -= 16) {

            byte[] buf = new byte[16];
            System.arraycopy(data, 0, buf, 0, buf.length);
            try {
                bins.read(buf);
                engine.processBlock(buf, 0, buf, 0);
                bous.write(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] output = bous.toByteArray();
        return output;
    }

    /**
     * cbc 模式加解密
     *
     * @param data
     * @param sm4Key
     * @param iv
     * @param mode
     * @return
     * @throws CspException
     */
    public static byte[] cbcProcessData(byte[] data, byte[] sm4Key, byte[] iv, int mode) throws CspException {
        int length = data.length;
        ByteArrayInputStream bins = new ByteArrayInputStream(data);
        ByteArrayOutputStream bous = new ByteArrayOutputStream();
        SM4Engine engine = new SM4Engine();
        if (mode == SM4_ENCRYPT) {
            engine.init(true, new KeyParameter(sm4Key));
            for (; length > 0; length -= 16) {
                //buf存储padding后的data
                byte[] buf = new byte[16];
                byte[] out = new byte[16];
                try {
                    bins.read(buf);
                    for (int i = 0; i < 16; i++) {
                        //out为异或运算后的明文块
                        out[i] = ((byte) (buf[i] ^ iv[i]));
                    }
                    engine.processBlock(out, 0, out, 0);
                    //将加密运算后的数据作为iv值
                    System.arraycopy(out, 0, iv, 0, 16);
                    bous.write(out);
                } catch (IOException e) {
                    throw new CspException(e);
                }
            }
        } else {
            engine.init(false, new KeyParameter(sm4Key));
            engine.init(false, new KeyParameter(sm4Key));
            byte[] temp = new byte[16];
            for (; length > 0; length -= 16) {
                byte[] buf = new byte[16];
                byte[] out = new byte[16];
                try {
                    bins.read(buf);
                    System.arraycopy(buf, 0, temp, 0, 16);
                    engine.processBlock(buf, 0, out, 0);
                    for (int i = 0; i < 16; i++) {
                        out[i] = (byte) (out[i] ^ iv[i]);
                    }

                    System.arraycopy(temp, 0, iv, 0, 16);
                    bous.write(out);
                } catch (IOException e) {
                    throw new CspException(e);
                }
            }
        }
        try {
            bins.close();
            bous.close();
        } catch (IOException e) {
            throw new CspException(e);
        }
        byte[] output = bous.toByteArray();
        return output;
    }

}
