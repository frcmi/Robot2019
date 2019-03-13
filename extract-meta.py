#!/usr/bin/env python3

import os
import sys
import re
import zipfile
import subprocess

scriptDir = os.path.abspath(os.path.dirname(__file__))

m = re.match(r'(/mnt/c/Users/[^/]+)/.*', scriptDir)
if m:
    winHomeDir = m.group(1)
else:
    raise RuntimeError("Script directory %s is not in Windows subsystem mapped home directory" % scriptDir)

gradleDir = os.path.join(winHomeDir, '.gradle')
gradleCacheDir = os.path.join(gradleDir, 'caches')

jarfiles = [
    gradleCacheDir + '/modules-2/files-2.1/com.kauailabs.navx.frc/navx-java/3.1.366/61a5d7a55c6f96fa1b15c52eaab3e1ecd27331e5/navx-java-3.1.366.jar',
    gradleCacheDir + '/modules-2/files-2.1/edu.wpi.first.wpilibj/wpilibj-java/2019.4.1/181d600aa0815fca956ff6903748e99be9025949/wpilibj-java-2019.4.1.jar',
    gradleCacheDir + '/modules-2/files-2.1/edu.wpi.first.ntcore/ntcore-java/2019.4.1/9dd5e2cf0d5080b10e19fb88d9372a170f7a034/ntcore-java-2019.4.1.jar',
    gradleCacheDir + '/modules-2/files-2.1/edu.wpi.first.wpiutil/wpiutil-java/2019.4.1/5baa3eec5cac7654236b16afd3f0198bbe5dc946/wpiutil-java-2019.4.1.jar',
    '/mnt/c/Users/Public/frc2019/maven/edu/wpi/first/thirdparty/frc2019/opencv/opencv-java/3.4.4-4/opencv-java-3.4.4-4.jar',
    gradleCacheDir + '/modules-2/files-2.1/com.ctre.phoenix/api-java/5.13.0/f9b2c1e7b6f2cabc3204e791386c47a856654fc5/api-java-5.13.0.jar',
    gradleCacheDir + '/modules-2/files-2.1/com.ctre.phoenix/wpiapi-java/5.13.0/d37ff722315cf93181d9f28b10a6bbcb023306ff/wpiapi-java-5.13.0.jar',
    gradleCacheDir + '/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-annotations/2.9.8/ba7f0e6f8f1b28d251eeff2a5604bed34c53ff35/jackson-annotations-2.9.8.jar',
    gradleCacheDir + '/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-core/2.8.3/5e1dc37c96308851c3ff609c250dc849c4b12022/jackson-core-2.8.3.jar',
    gradleCacheDir + '/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-databind/2.8.3/cea3788c72271d45676ce32c0665991674b24cc5/jackson-databind-2.8.3.jar',
    gradleCacheDir + '/modules-2/files-2.1/com.fasterxml.jackson.jaxrs/jackson-jaxrs-base/2.8.3/2cf8ff461ada0865833c626e6db480da86c8f7dc/jackson-jaxrs-base-2.8.3.jar',
    gradleCacheDir + '/modules-2/files-2.1/com.fasterxml.jackson.jaxrs/jackson-jaxrs-json-provider/2.8.3/e4515b3860beb8a3af171af91a6c3610aaa54521/jackson-jaxrs-json-provider-2.8.3.jar',
    gradleCacheDir + '/modules-2/files-2.1/com.fasterxml.jackson.module/jackson-module-jaxb-annotations/2.8.3/8a9385cb518e67ff7997239f82da4f9dde9f2584/jackson-module-jaxb-annotations-2.8.3.jar',
    gradleCacheDir + '/modules-2/files-2.1/com.googlecode.guava-osgi/guava-osgi/11.0.1/ad6b95c7b266baab6866ec8275c03cf2927713eb/guava-osgi-11.0.1.jar',
    gradleCacheDir + '/modules-2/files-2.1/commons-codec/commons-codec/1.9/9ce04e34240f674bc72680f8b843b1457383161a/commons-codec-1.9.jar',
    gradleCacheDir + '/modules-2/files-2.1/commons-io/commons-io/2.5/2852e6e05fbb95076fc091f6d1780f1f8fe35e0f/commons-io-2.5.jar',
    gradleCacheDir + '/modules-2/files-2.1/commons-logging/commons-logging/1.2/4bfc12adfe4842bf07b657f0369c4cb522955686/commons-logging-1.2.jar',
    gradleCacheDir + '/modules-2/files-2.1/edu.wpi.first.cameraserver/cameraserver-java/2019.4.1/d0d4a27f87a6b87eeed4ddc21964a0bc6e9e372/cameraserver-java-2019.4.1.jar',
    gradleCacheDir + '/modules-2/files-2.1/edu.wpi.first.cscore/cscore-java/2019.4.1/c7eee2f82bf88c65f62a042828bf0944a2a475bb/cscore-java-2019.4.1.jar',
    gradleCacheDir + '/modules-2/files-2.1/edu.wpi.first.hal/hal-java/2019.4.1/fe4aec01e745838fa6997fba26b0917e35af9480/hal-java-2019.4.1.jar',
    gradleCacheDir + '/modules-2/files-2.1/javax.activation/activation/1.1.1/485de3a253e23f645037828c07f1d7f1af40763a/activation-1.1.1.jar',
    gradleCacheDir + '/modules-2/files-2.1/javax.inject/javax.inject/1/6975da39a7040257bd51d21a231b76c915872d38/javax.inject-1.jar',
    gradleCacheDir + '/modules-2/files-2.1/javax.json.bind/javax.json.bind-api/1.0/10332203cb05f4ab2e8bf058bfd7d99648c5ca68/javax.json.bind-api-1.0.jar',
    gradleCacheDir + '/modules-2/files-2.1/javax.json/javax.json-api/1.0/a74939ecbf7294b40accb4048929577f5ddcee2/javax.json-api-1.0.jar',
    gradleCacheDir + '/modules-2/files-2.1/javax.mail/mail/1.4/1aa1579ae5ecd41920c4f355b0a9ef40b68315dd/mail-1.4.jar',
    gradleCacheDir + '/modules-2/files-2.1/javax.ws.rs/javax.ws.rs-api/2.0/61f0983eb190954ccdede31e786a9e0bd9767c4a/javax.ws.rs-api-2.0.jar',
    gradleCacheDir + '/modules-2/files-2.1/net.jcip/jcip-annotations/1.0/afba4942caaeaf46aab0b976afd57cc7c181467e/jcip-annotations-1.0.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.apache.httpcomponents/httpclient/4.5.2/733db77aa8d9b2d68015189df76ab06304406e50/httpclient-4.5.2.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.apache.httpcomponents/httpcore/4.4.4/b31526a230871fbe285fbcbe2813f9c0839ae9b0/httpcore-4.4.4.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.glassfish.hk2.external/asm-all-repackaged/2.0.3/a8eabdb61c1d97889ee7a5a6756a148c45237c3/asm-all-repackaged-2.0.3.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.glassfish.hk2.external/javax.inject/2.0.3/c35544339e92b44e3bc3b0b483ffbf454d1fd7d0/javax.inject-2.0.3.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.glassfish.hk2/auto-depends/2.0.3/4cb83f0d85ce5919a973e7a6f9eaae1f808b372c/auto-depends-2.0.3.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.glassfish.hk2/hk2-api/2.0.3/66480d7cc0610157538d353aadf0a81fc8829d4c/hk2-api-2.0.3.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.glassfish.hk2/osgi-resource-locator/1.0.1/4ed2b2d4738aed5786cfa64cba5a332779c4c708/osgi-resource-locator-1.0.1.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.glassfish.jersey.core/jersey-client/2.0-m04/338ec5389383c99e5e87838b89791a4433e440c2/jersey-client-2.0-m04.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.glassfish.jersey.core/jersey-common/2.0-m04/22b07a3597f347a6266a2b93537695a2af208bf9/jersey-common-2.0-m04.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.jboss.logging/jboss-logging/3.3.0.Final/3616bb87707910296e2c195dc016287080bba5af/jboss-logging-3.3.0.Final.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.jboss.resteasy/resteasy-client/3.1.0.Final/d6bc8ed3588b9ad3bbb8b2326c6122f4ded19dcd/resteasy-client-3.1.0.Final.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.jboss.resteasy/resteasy-jackson2-provider/3.1.0.Final/32b6f0ba5d8145b20ffba7d9c7cf0cdf28e6a262/resteasy-jackson2-provider-3.1.0.Final.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.jboss.resteasy/resteasy-jaxrs-services/3.1.0.Final/23dcbc765f0375d70b6fcfcd22228d338536db7f/resteasy-jaxrs-services-3.1.0.Final.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.jboss.resteasy/resteasy-jaxrs/3.1.0.Final/6427a9a622bff4dbe99d6f08dabd0dd89af85235/resteasy-jaxrs-3.1.0.Final.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.jboss.spec.javax.annotation/jboss-annotations-api_1.2_spec/1.0.0.Final/6d7ff02a645227876ed550900d32d618b8f0d556/jboss-annotations-api_1.2_spec-1.0.0.Final.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.jboss.spec.javax.ws.rs/jboss-jaxrs-api_2.0_spec/1.0.1.Beta1/66c0832acaba167c2fd7ee4cbaf212347854d57c/jboss-jaxrs-api_2.0_spec-1.0.1.Beta1.jar',
    gradleCacheDir + '/modules-2/files-2.1/org.jvnet/tiger-types/1.4/9f75db7dea926f497e76eae2cea36eca74ea508/tiger-types-1.4.jar',
]

wdir = os.path.join(scriptDir, 'build', 'tmp', 'jar-merge')

subprocess.check_call(['rm', '-fr', wdir])
subprocess.check_call(['mkdir', '-p', wdir])

# dict of jars by package name
jars = {}

# dict from relative pathname to set of jars that contain that relative pathname (directories excluded) */
relpathJars = {}

def addRelpathJar(relpath, jar):
    entry = relpathJars.get(relpath, None)
    if entry is None:
        entry = set()
        relpathJars[relpath] = entry
    entry.add(jar)

class Jar(object):
    def __init__(self, jarfile):
        self.jarfile = jarfile
        m = re.match(r'^.*/([^/]+)\.jar$', jarfile)
        if not m:
            raise RuntimeError("Jar filename not in proper form: %s" % jarfile)
        self.pname = m.group(1)
        self.jwdir = os.path.join(wdir, self.pname)
        subprocess.check_call(['rm', '-fr', self.jwdir])
        subprocess.check_call(['mkdir', '-p', self.jwdir])

    def extract(self, subpath=None):
        if subpath is None:
            print("Extracting %s (All files)" % self.pname)
            subprocess.check_call(['jar', '-xf', self.jarfile], cwd=self.jwdir)
        else:
            print("Extracting %s %s" % (self.pname, subpath))
            subprocess.check_call(['jar', '-xf', self.jarfile, subpath], cwd=self.jwdir)

    def extractMetaInf(self):
        self.extract('META-INF')

    def addRelpaths(self):
        for dirpath, dnames, fnames in os.walk(self.jwdir):
            rdirpath = os.path.relpath(dirpath, self.jwdir)
            for fname in fnames:
                rfpath = os.path.join(rdirpath, fname)
                addRelpathJar(rfpath, self)

    def fpath(self, rpath):
        return os.path.join(self.jwdir, rpath)

    def __str__(self):
        return "Jar %s" % self.pname

    def __lt__(self, other):
        return self.pname < other.pname

    def __eq__(self, other):
        return self.pname == other.pname

    def __hash__(self):
        return self.pname.__hash__()

builtJarFile = os.path.join(scriptDir, 'build', 'libs', 'Robot2019.jar')
builtJar = Jar(builtJarFile)
builtJar.extractMetaInf()

for jarfile in jarfiles:
    jar = Jar(jarfile)
    if jar.pname in jars:
        raise RuntimeError("Multiple jars with package name %s" % jar.pname)
    jars[jar.pname] = jar
    jar.extractMetaInf()
    jar.addRelpaths()

conflicts = {}
serviceConflicts = {}
for rpath, jset in relpathJars.items():
    if len(jset) > 1:
        conflicts[rpath] = jset
        if rpath.startswith('META-INF/services/'):
            serviceConflicts[rpath] = jset

for rpath in sorted(conflicts.keys()):
    jset = conflicts[rpath]
    print("PATH %s:" % rpath)
    for jar in sorted(jset):
        print("   %s" % jar.pname)

for rpath in sorted(serviceConflicts.keys()):
    jset = serviceConflicts[rpath]
    print("Service PATH %s:" % rpath)
    for jar in sorted(jset) + [builtJar]:
        print("   In package %s:" % jar.pname)
        fpath = jar.fpath(rpath)
        with open(fpath, 'r') as fd:
            for line in fd:
                print("    %s" % line, end='')
            print()
    
