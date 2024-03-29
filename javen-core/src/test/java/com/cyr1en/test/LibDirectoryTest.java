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
import com.cyr1en.javen.LibDirectory;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LibDirectoryTest {

    private LibDirectory defaultLib;
    private Dependency validDependency;
    private Dependency invalidDependency;

    @Before
    public void before() {
        defaultLib = new LibDirectory();
        validDependency = new Dependency("com.github.cyr1en", "flatdb", "1.0.5");
        invalidDependency = new Dependency("com.github.cyr1en", "test-lib", "0.0.1");
    }

    @Test
    public void testAEmpty() {
        Assertions.assertThat(defaultLib.exists()).isTrue();
        Assertions.assertThat(defaultLib.isDirectory()).isTrue();
        Assertions.assertThat(defaultLib.listDepsToLoad()).isEqualTo(Collections.emptyMap());
    }

    @Test
    public void testBFalseConstruct() {
        Assertions.assertThatCode(() -> new LibDirectory("src/test/resources/DummyFile"))
                .isInstanceOf(IllegalStateException.class)
                .hasNoCause();
    }

    @Test
    public void testCExistingDir() {
        AtomicReference<LibDirectory> testLib = new AtomicReference<>();
        Assertions.assertThatCode(() -> testLib.set(new LibDirectory("src/test/resources/testLibDir")))
                .doesNotThrowAnyException();
        LibDirectory nonAtomic = testLib.get();
        Assertions.assertThat(nonAtomic).isNotNull();
        Assertions.assertThat(nonAtomic.containsDependency(validDependency)).isTrue();

        Assertions.assertThat(nonAtomic.containsDiffVersionOf(validDependency)).isTrue();
        Assertions.assertThat(nonAtomic.listJarFilesMatching(validDependency).length > 1).isTrue();

        Dependency oldDependency = new Dependency("com.github.cyr1en", "flatdb", "1.0.4");
        Assertions.assertThatCode(() -> nonAtomic.deleteDifferentVersion(validDependency)).doesNotThrowAnyException();
        Assertions.assertThat(nonAtomic.containsDependency(oldDependency)).isFalse();
    }

    @Test
    public void testDContainsDep() {
        Assertions.assertThat(defaultLib.containsDependency(invalidDependency)).isFalse();
    }

    @After
    public void after() throws IOException {
        defaultLib.deleteOnExit();
        Path backup = Paths.get("src/test/resources/backup/flatdb-1.0.4.jar");
        Path libsDir = Paths.get("src/test/resources/testLibDir/flatdb-1.0.4.jar");
        if (!Files.exists(libsDir))
            Files.copy(backup, libsDir);
    }
}
