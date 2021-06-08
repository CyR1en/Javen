/*
 * MIT License
 *
 * Copyright (c) 2019 Ethan Bacurio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cyr1en.javen;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import static com.cyr1en.javen.Javen.LOGGER;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.Objects;
import java.util.jar.JarFile;

public class Loader {

    private static final Dependency TOOLS_DEP = new Dependency("io.earcam.wrapped", "com.sun.tools.attach", "1.8.0_jdk8u172-b11");

    public Loader(Javen instance) {
        if (!instance.getLibsDir().containsDependency(TOOLS_DEP))
            prepare(instance);
        else {
            String path = instance.getLibsDir().listJarFilesMatching(TOOLS_DEP)[0].getAbsolutePath();
            setAttachProp(path);
        }
        ByteBuddyAgent.install(ByteBuddyAgent.AttachmentProvider.ForUserDefinedToolsJar.INSTANCE);
    }

    public void prepare(Javen instance) {
        getAttachTool(instance);
        if (instance.getLibsDir().containsDependency(TOOLS_DEP)) {
            String path = instance.getLibsDir().listJarFilesMatching(TOOLS_DEP)[0].getAbsolutePath();
            setAttachProp(path);
        }
    }

    private void getAttachTool(Javen instance) {
        File file = Maven.resolver().resolve(TOOLS_DEP.getCanonicalName()).withoutTransitivity().asSingleFile();
        instance.getLibsDir().moveHere(file);
    }

    private void setAttachProp(String value) {
        System.setProperty(ByteBuddyAgent.AttachmentProvider.ForUserDefinedToolsJar.PROPERTY, value);
    }

    public void addJarToClassPath(File jarFile) {
        Instrumentation instrumentation = ByteBuddyAgent.getInstrumentation();
        if (instrumentation != null) {
            try {
                instrumentation.appendToSystemClassLoaderSearch(new JarFile(jarFile.getCanonicalPath()));
                LOGGER.info("\u001b[32m" + jarFile.getName() + " loaded. \033[0m");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
