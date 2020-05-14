

package com.vectorprint.certificates;

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


import com.vectorprint.ArrayHelper;
import com.vectorprint.IOHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * Methods to load certificates, keystores and private keys. Password arguments will be cleared after use, see {@link ArrayHelper#clear(char[]) }.
 * You need to configure a {@link Provider security provider} in the jvm or programmatically.
 * 
 * @author Eduard Drenth at VectorPrint.nl
 */
public class CertificateHelper {
   
   private CertificateHelper() {}

   /**
    * calls {@link #loadCertificate(java.io.InputStream, java.lang.String) } with X.509
    * @param in
    * @return
    * @throws IOException
    * @throws CertificateException 
    */
   public static Certificate loadCertificate(InputStream in) throws IOException, CertificateException {
      return loadCertificate(in, "X.509");
   }

   /**
    * loads a certificate from a stream
    * @param in
    * @param type
    * @return
    */
   public static Certificate loadCertificate(InputStream in, String type) throws IOException, CertificateException {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         Certificate cert = CertificateFactory.getInstance(type)
             .generateCertificate(new ByteArrayInputStream(IOHelper.load(in, out).toByteArray()));
         return cert;
   }

   /**
    * loads a keystore from an inputstream
    * @param in
    * @param keystoretype
    * @param password
    * @return
    */
   public static KeyStore loadKeyStore(InputStream in, String keystoretype, char[] password) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
         KeyStore ks = KeyStore.getInstance(keystoretype);
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         ks.load(new ByteArrayInputStream(IOHelper.load(in, out).toByteArray()), password);
         ArrayHelper.clear(password);
         return ks;
   }

   /**
    * returns a private key from a keystore
    * @param ks
    * @param alias
    * @param password
    * @return
    * @throws KeyStoreException
    * @throws NoSuchAlgorithmException
    * @throws UnrecoverableKeyException 
    */
   public static PrivateKey getKey(KeyStore ks, String alias, char[] password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      PrivateKey k = (PrivateKey) ks.getKey(alias, password);
      ArrayHelper.clear(password);
      return k;
   }

   /**
    * calls {@link #getKey(java.security.KeyStore, java.lang.String, char[]) } with "1" for alias.
    * @param ks
    * @param password
    * @return
    * @throws KeyStoreException
    * @throws NoSuchAlgorithmException
    * @throws UnrecoverableKeyException 
    */
   public static PrivateKey getKey(KeyStore ks, char[] password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      return getKey(ks, "1", password);
   }
   /*
Installing the Provider Statically

To install the provider statically you need to add it as an entry to the java.security file which can be found in $JAVA_HOME/jre/lib/security/java.security for the JRE/JDK you are using. Look for a list of lines with security.provider.X where X is some number. At the bottom of the list add the line:

security.provider.N=org.bouncycastle.jce.provider.BouncyCastleProvider

where N is one more than the last number in the list.

It is possible to add the provider higher up in the list. If you do this we recommend you don't add it earlier than position 2 as there are occasionally internal dependencies on the provider at position 1 which may cause some operations by your JVM to result in errors.   */
}
