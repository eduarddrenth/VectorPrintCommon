/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vectorprint;

/*-
 * #%L
 * VectorPrintCommon
 * %%
 * Copyright (C) 2011 - 2018 VectorPrint
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.vectorprint.certificates.CertificateHelper;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class CertificateTest {
   
   static {
      Security.addProvider(new BouncyCastleProvider());
   }
   
   @Test
   public void testLoadKeystore() throws Exception {
      KeyStore ks = CertificateHelper.loadKeyStore(new URL("file:src/test/resources/eduarddrenth-TECRA-S11.pfx").openStream(),
          "pkcs12", "password".toCharArray());
      
   }

   @Test
   public void testLoadCertificate() throws Exception {
      Certificate cert = CertificateHelper.loadCertificate(new URL("file:src/test/resources/eduarddrenth-TECRA-S11.crt").openStream());
   }

   @Test
   public void testLoadKey() throws Exception {
      KeyStore ks = CertificateHelper.loadKeyStore(new URL("file:src/test/resources/eduarddrenth-TECRA-S11.pfx").openStream(),
          "pkcs12", "password".toCharArray());
      PrivateKey key = CertificateHelper.getKey(ks, "1", "password".toCharArray());
   }
}
