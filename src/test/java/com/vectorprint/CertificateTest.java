/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vectorprint;

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
