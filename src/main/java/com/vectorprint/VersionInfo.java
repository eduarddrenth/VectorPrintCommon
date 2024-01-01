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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionInfo {

   private VersionInfo() {
   }

   private static final Logger LOG = LoggerFactory.getLogger(VersionInfo.class.getName());

   /**
    * print versioninfo for jar files on the classpath, or, alternatively, from jar files provided in space seperated
    * arguments
    *
    * @param args
    * @throws IOException
    */
   public static void main(String[] args) throws IOException {
      if (args != null && args.length > 0) {
         for (VersionInformation mi : getVersionInfo(args).values()) {
            System.out.println(mi);
         }
      } else {
         for (VersionInformation mi : getVersionInfo().values()) {
            System.out.println(mi);
         }
      }
   }

   /**
    * Print the version of all the libraries found in the class path.
    */
   public static void printVersionInfo() throws IOException {

      for (VersionInformation mi : getVersionInfo().values()) {
         String name = mi.artifactId.equals(mi.groupId) ? mi.artifactId : (mi.groupId + '/' + mi.artifactId);
         LOG.info("Maven library {0} v{1} on {2}, size={3}", name, mi.version, mi.buildDate, formatNumber("#,##0", mi.size));
      }
   }

   private static String formatNumber(String format, Number number) {
      if (null == number) {
         return "";
      }
      return new DecimalFormat(format).format(number);
   }

   /**
    * @param parts array containing paths of jar files
    * @return a Map of the version information for libraries found in the class path.
    */
   public static Map<String, VersionInformation> getVersionInfo(String[] parts) throws IOException {
      Map<String, VersionInformation> ret = new HashMap<>();
      for (String part : parts) {
         addFromEntry(part, ret);
      }
      return ret;
   }

   /**
    * @return a Map containing version information of libraries found on the class path.
    */
   public static Map<String, VersionInformation> getVersionInfo() throws IOException {
      String[] parts = System.getProperty("java.class.path").split(File.pathSeparator);
      Map<String, VersionInformation> ret = getVersionInfo(parts);
      if (ret.isEmpty()) {
         LOG.warn("Unable to find version info in class path= {0}", System.getProperty("java.class.path"));
      } else if (parts.length == 1) {
         // perhaps started with -jar and classpath in manifest
         JarFile zipFile = new JarFile(parts[0]);
         String base = new File(parts[0]).getParent() + File.separator;
         if (zipFile.getManifest().getMainAttributes().containsKey(Attributes.Name.CLASS_PATH)) {
            return getVersionInfo(zipFile.getManifest().getMainAttributes().getValue(Attributes.Name.CLASS_PATH).split(" +"));
         }
      }
      return ret;
   }

   private static void addFromEntry(String entry, Map<String, VersionInformation> ret) throws IOException {
      File file = new File(entry);
      if (!file.isFile()) {
         return;
      }
      JarFile zipFile = new JarFile(file);
      zipFile.stream().filter(zi -> zi.getName().endsWith("pom.properties"))
              .forEach(zipEntry -> {
                 try {
                    final InputStream in = zipFile.getInputStream(zipEntry);
                    ret.put(entry, parsePomProperties(file.length(), in));
                 } catch (IOException e) {
                    throw new VectorPrintRuntimeException(e);
                 }
              });
      if (ret.isEmpty()) {
         // try manifest
         if (zipFile.getManifest().getMainAttributes().containsKey(Attributes.Name.IMPLEMENTATION_VERSION)) {
            ret.put(entry, new VersionInformation(entry, "unknown",
                zipFile.getManifest().getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION), "unknown", file.length()));
         }
      }
   }

   /**
    * Class to hold the version info for a library.
    */
   public static class VersionInformation {

      /**
       * artifactId of build
       */
      public final String artifactId;
      /**
       * groupId of build.
       */
      public final String groupId;
      /**
       * version of build.
       */
      public final String version;
      /**
       * date built.
       */
      public final String buildDate;
      /**
       * size of the jar.
       */
      public final long size;

      VersionInformation(String artifactId, String groupId, String version, String buildDate, long size) {
         this.artifactId = artifactId;
         this.groupId = groupId;
         this.version = version;
         this.buildDate = buildDate;
         this.size = size;
      }

      @Override
      public String toString() {
         String name = artifactId.equals(groupId) ? artifactId : (groupId + '/' + artifactId);
         return new StringBuilder("name: ").append(name).append(", version: ").append(version).append(", buildDate: ")
             .append(buildDate).append(", size: ").append(formatNumber("#,##0", size)).toString();
      }
   }

   private static VersionInformation parsePomProperties(long length, InputStream in) throws IOException {
      final BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
      String version = "unknown";
      String buildDate = "unknown";
      String groupId = "unknown";
      String artifactId = "unknown";
      for (String line; (line = br.readLine()) != null;) {
         // #.*:.*:.*
         if (line.startsWith("#") && line.indexOf(':') != line.lastIndexOf(':')) {
            buildDate = line.substring(1);
            continue;
         }
         if (line.startsWith("version=")) {
            version = line.substring(8);
         }
         if (line.startsWith("groupId=")) {
            groupId = line.substring(8);
         }
         if (line.startsWith("artifactId=")) {
            artifactId = line.substring(11);
         }
      }
      return new VersionInformation(artifactId, groupId, version, buildDate, length);
   }
}
