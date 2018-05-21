/**
 * Copyright BCIA. All Rights Reserved.
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

package org.bcia.javachain.common.tools.cryptogen;

import org.apache.commons.io.FileUtils;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.tools.cryptogen.bean.Configuration;
import org.bcia.javachain.msp.mgmt.Msp;
import org.bcia.javachain.msp.util.MspConfigBuilder;
import org.bcia.javachain.protos.msp.MspConfigPackage;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MspHelperTest {

    private static final String testCAOrg = "example.com";
    private static final String testCAName = "ca" + "." + testCAOrg;
    private static final String testName = "peer0";
    private static final String testCountry = "China";
    private static final String testProvince = "Guangdong";
    private static final String testLocality = "ShenZhen";
    private static final String testOrganizationUnit = "BCIA";
    private static final String testStreetAddress = "testStreetAddress";
    private static final String testPostalCode = "123456";
    private String testDir;

    {
        try {
            Path tempDirPath = Files.createTempDirectory(null);
            testDir = Paths.get(tempDirPath.toString(), "msp-test").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Rule
    public ExpectedException expectedRule = ExpectedException.none();

    @Test
    public void generateLocalMSP() throws JavaChainException, IOException {
        FileUtil.removeAll(testDir);

        String caDir = Paths.get(testDir, "ca").toString();
        String tlsCADir = Paths.get(testDir, "tlsca").toString();
        String mspDir = Paths.get(testDir, "msp").toString();
        String tlsDir = Paths.get(testDir, "tls").toString();
        //generate  CaHelper
        CaHelper signCA = CaHelper.newCA(caDir, testCAOrg, testCAName, testCountry, testProvince, testLocality, testOrganizationUnit, testStreetAddress, testPostalCode);
        CaHelper tlsCA = CaHelper.newCA(tlsCADir, testCAOrg, testCAName, testCountry, testProvince, testLocality, testOrganizationUnit, testStreetAddress, testPostalCode);

        String subjectDN = signCA.getSignCert().getSubject().toString();
        Assert.assertEquals(testCountry, X509CertificateUtil.getSubject(subjectDN).getCountry().get(0));
        Assert.assertEquals(testProvince, X509CertificateUtil.getSubject(subjectDN).getStateOrProvince().get(0));
        Assert.assertEquals(testLocality, X509CertificateUtil.getSubject(subjectDN).getLocality().get(0));
        Assert.assertEquals(testOrganizationUnit, X509CertificateUtil.getSubject(subjectDN).getOrganizationalUnit().get(0));
        Assert.assertEquals(testStreetAddress, X509CertificateUtil.getSubject(subjectDN).getStreetAddress().get(0));
        //generate local MSP for nodeType=PEER
        MspHelper.generateLocalMSP(testDir, testName, null, signCA, tlsCA, MspHelper.PEER, true);
        //check to see that the right files were generated/saved
        List<String> mspFiles = new ArrayList<>();
        mspFiles.add(Paths.get(mspDir, "admincerts", testName + "-cert.pem").toString());
        mspFiles.add(Paths.get(mspDir, "cacerts", testCAName + "-cert.pem").toString());
        mspFiles.add(Paths.get(mspDir, "tlscacerts", testCAName + "-cert.pem").toString());
        mspFiles.add(Paths.get(mspDir, "keystore").toString());
        mspFiles.add(Paths.get(mspDir, "signcerts", testName + "-cert.pem").toString());
        mspFiles.add(Paths.get(mspDir, "config.yaml").toString());

        List<String> tlsFiles = new ArrayList<>();
        tlsFiles.add(Paths.get(tlsDir, "ca.crt").toString());
        tlsFiles.add(Paths.get(tlsDir, "server.key").toString());
        tlsFiles.add(Paths.get(tlsDir, "server.crt").toString());

        for (String mspFile : mspFiles) {
            Assert.assertTrue(new File(mspFile).exists());
        }
        for (String tlsFile : tlsFiles) {
            Assert.assertTrue(new File(tlsFile).exists());
        }
        //generate local MSP for nodeType=CLENT
        MspHelper.generateLocalMSP(testDir, testName, null, signCA, tlsCA, MspHelper.CLIENT, true);
        tlsFiles = new ArrayList<>();
        tlsFiles.add(Paths.get(tlsDir, "ca.crt").toString());
        tlsFiles.add(Paths.get(tlsDir, "client.key").toString());
        tlsFiles.add(Paths.get(tlsDir, "client.crt").toString());

        for (String tlsFile : tlsFiles) {
            Assert.assertTrue(new File(tlsFile).exists());
        }
        //finally check to see if we can load this as a local MSP config
        setupMspConfig(mspDir);


    }

    @Test
    public void generateVerifyingMSP() throws JavaChainException, IOException {

        System.out.println("testDir=" + testDir);

        String caDir = Paths.get(testDir, "ca").toString();
        String tlsCADir = Paths.get(testDir, "tlsca").toString();
        String mspDir = Paths.get(testDir, "msp").toString();
        //generate  CaHelper
        CaHelper signCA = CaHelper.newCA(caDir,
                testCAOrg,
                testCAName,
                testCountry,
                testProvince,
                testLocality,
                testOrganizationUnit,
                testStreetAddress,
                testPostalCode);
        CaHelper tlsCA = CaHelper.newCA(tlsCADir,
                testCAOrg,
                testCAName,
                testCountry,
                testProvince,
                testLocality,
                testOrganizationUnit,
                testStreetAddress,
                testPostalCode);

        MspHelper.generateVerifyingMSP(mspDir, signCA, tlsCA, true);

        List<String> files = new ArrayList<>();
        files.add(Paths.get(mspDir, "admincerts", testCAName + "-cert.pem").toString());
        files.add(Paths.get(mspDir, "cacerts", testCAName + "-cert.pem").toString());
        files.add(Paths.get(mspDir, "tlscacerts", testCAName + "-cert.pem").toString());
        files.add(Paths.get(mspDir, "config.yaml").toString());




        for (String file : files) {
            Assert.assertTrue(new File(file).exists());
        }
        //finally check to see if we can load this as a verifying MSP config
        expectedRule.expect(JavaChainException.class);
        expectedRule.expectMessage("the name is illegal");
        tlsCA.setName("test/fail");
        MspHelper.generateVerifyingMSP(mspDir, signCA, tlsCA, true);
        signCA.setName("test/fail");
        MspHelper.generateVerifyingMSP(mspDir, signCA, tlsCA, true);

        FileUtil.removeAll(testDir);
    }


    @Test
    public void exportConfig() throws JavaChainException {

        String path = Paths.get(testDir, "export-test").toString();
        String configFile = Paths.get(path, "config.yaml").toString();
        String caFile = "ca.pem";
        System.out.print(path);

        FileUtil.mkdirAll(Paths.get(path));

        try {
            MspHelper.exportConfig(path, caFile, true);
        } catch (JavaChainException e) {
            throw new JavaChainException("failed to read config file: {}", e);
        }
        Yaml yaml = new Yaml();
        Configuration configuration;
        try {
            configuration = yaml.loadAs(new FileInputStream(configFile), Configuration.class);
        } catch (FileNotFoundException e) {
            throw new JavaChainException(configFile + " is not found");
        }
        Assert.assertTrue(configuration.getNodeOUs().getEnable());
        Assert.assertEquals(caFile, configuration.getNodeOUs().getClientOUIdentifier().getCertificate());
        Assert.assertEquals(MspHelper.CLIENT_OU, configuration.getNodeOUs().getClientOUIdentifier().getOrganizationalUnitIdentifier());
        Assert.assertEquals(caFile, configuration.getNodeOUs().getPeerOUIdentifier().getCertificate());
        Assert.assertEquals(MspHelper.PEER_OU, configuration.getNodeOUs().getPeerOUIdentifier().getOrganizationalUnitIdentifier());
    }

    private void setupMspConfig(String mspDir) throws IOException {
        List<String> caCert = new ArrayList<>();
        File caCertFile = new File(Paths.get(mspDir, "cacerts").toString());
        for (File file : Objects.requireNonNull(caCertFile.listFiles())) {
            caCert.add(FileUtils.readFileToString(file, Charset.forName("UTF-8")));
        }

        List<String> adminCert = new ArrayList<>();
        File adminCertFile = new File(Paths.get(mspDir, "admincerts").toString());
        for (File file : Objects.requireNonNull(adminCertFile.listFiles())) {
            adminCert.add(FileUtils.readFileToString(file, Charset.forName("UTF-8")));
        }

        List<String> keyStore = new ArrayList<>();
        File keyStoreFile = new File(Paths.get(mspDir, "keystore").toString());
        for (File file : Objects.requireNonNull(keyStoreFile.listFiles())) {
            keyStore.add(FileUtils.readFileToString(file, Charset.forName("UTF-8")));
        }

        List<String> tlsCaCert = new ArrayList<>();
        File tlsCertFile = new File(Paths.get(mspDir, "tlscacerts").toString());
        for (File file : Objects.requireNonNull(tlsCertFile.listFiles())) {
            tlsCaCert.add(FileUtils.readFileToString(file, Charset.forName("UTF-8")));
        }

        List<String> intermediateCerts = new ArrayList<>();
        File intermediateCertFile = new File(Paths.get(mspDir, "intermediatecerts").toString());
        for (File file : Objects.requireNonNull(intermediateCertFile.listFiles())) {
            intermediateCerts.add(FileUtils.readFileToString(file, Charset.forName("UTF-8")));
        }

        List<String> crls = new ArrayList<>();
        File crlsFile = new File(Paths.get(mspDir, "crls").toString());
        for (File file : Objects.requireNonNull(crlsFile.listFiles())) {
            crls.add(FileUtils.readFileToString(file, Charset.forName("UTF-8")));
        }

        List<String> configContent = new ArrayList<>();
        File configFile = new File(Paths.get(mspDir, "config.yaml").toString());
        configContent.add(FileUtils.readFileToString(configFile, Charset.forName("UTF-8")));

        List<String> tlsIntermediateCerts = new ArrayList<>();
        File tlsIntermediateCertsFile = new File(Paths.get(mspDir, "tlsintermediatecerts").toString());
        for (File file : Objects.requireNonNull(tlsIntermediateCertsFile.listFiles())) {
            tlsIntermediateCerts.add(FileUtils.readFileToString(file, Charset.forName("UTF-8")));
        }

        List<String> signCert = new ArrayList<>();
        File signCertFile = new File(Paths.get(mspDir, "signcerts").toString());
        for (File file : Objects.requireNonNull(signCertFile.listFiles())) {
            signCert.add(FileUtils.readFileToString(file, Charset.forName("UTF-8")));
        }

        MspConfigPackage.MSPConfig mspConfig = MspConfigBuilder.buildMspConfig("testMsp", caCert, keyStore, signCert, adminCert, intermediateCerts, crls, configContent, tlsCaCert, tlsIntermediateCerts);
        Msp msp = new Msp();
        // TODO: 2018/4/20 setup未完善
//        msp.setup(mspConfig);
    }
}