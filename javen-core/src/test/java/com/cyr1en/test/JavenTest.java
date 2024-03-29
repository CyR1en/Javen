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

package com.cyr1en.test;


import com.cyr1en.javen.Dependency;
import com.cyr1en.javen.Javen;
import com.cyr1en.javen.Repository;
import com.cyr1en.javen.annotation.Lib;
import com.cyr1en.javen.util.JavenUtil;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JavenTest {

    private Javen javen;

    private Repository jCenterRepo;

    private Dependency mavenCentralTarget;
    private Dependency jCenterTarget;

    @Before
    public void before() {
        Path libsDir = Paths.get("src/test/resources/testLibDir");

        javen = new Javen(libsDir);

        jCenterRepo = new Repository("jCenter", "https://jcenter.bintray.com/");
        javen.addRepository(jCenterRepo);
        mavenCentralTarget = new Dependency("com.google.guava", "guava", "27.1-jre");

        if (javen.getLibsDir().containsDependency(mavenCentralTarget))
            javen.getLibsDir().deleteDependency(mavenCentralTarget);
    }

    @Test
    public void testAContainsJCenter() {
        Assertions.assertThat(javen.getRepositories().contains(jCenterRepo)).isTrue();
    }

    @Test
    public void testBRequestedIsDistinct() {
        List<Dependency> requested = JavenUtil.findAllRequestedDeps();
        Assertions.assertThat(requested.size()).isEqualTo(2);
    }

    @Test
    public void testCRequestedDeps() {
        List<Dependency> requested = JavenUtil.findAllRequestedDeps();
        Assertions.assertThat(requested.contains(mavenCentralTarget)).isTrue();
    }

    @Test
    public void testDDownloadNeededDeps() {
        Assertions.assertThatCode(() -> javen.resolveDependencies()).doesNotThrowAnyException();
        Assertions.assertThat(javen.getLibsDir().containsDependency(mavenCentralTarget)).isTrue();
    }

    @Test
    public void testELoadDepsAfterAllTestsAreDone() {
        Assertions.assertThatCode(() -> javen.loadDependencies()).doesNotThrowAnyException();
        Assertions.assertThatCode(() -> {
            String[] classesToCheck = new String[]{"com.cyr1en.flatdb.annotations.Table"};
            for (String cS : classesToCheck) {
                Class<?> c = Class.forName(cS);
                Assertions.assertThat(c).isNotNull();
            }
        }).doesNotThrowAnyException();
    }

    @After
    public void after() throws IOException {
        javen.getLibsDir().deleteDependency(mavenCentralTarget);

        Path backup = Paths.get("src/test/resources/backup/flatdb-1.0.4.jar");
        Path libsDir = Paths.get("src/test/resources/testLibDir/flatdb-1.0.4.jar");
        if (!Files.exists(libsDir))
            Files.copy(backup, libsDir);
    }

    @Lib(group = "com.google.guava", name = "guava", version = "27.1-jre")
    @Lib(group = "com.google.guava", name = "guava", version = "27.1-jre")
    @Lib(group = "com.github.cyr1en", name = "FlatDB", version = "1.0.5")
    private class TestClass {
    }
}
