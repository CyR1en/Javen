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

import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.util.LinkedList;

public class Repositories {

    private final LinkedList<Repository> repoTracker;

    public Repositories() {
        Maven.configureResolver().withMavenCentralRepo(true);
        repoTracker = new LinkedList<>();
    }

    public void addRepo(String id, String url) {
        addRepo(id, url, null);
    }

    public void addRepo(String id, String url, String layout) {
        Repository repository = new Repository(id, url, layout);
        addRepo(repository);
    }

    public void addRepo(Repository repository) {
        repoTracker.add(repository);
        Maven.configureResolver().withRemoteRepo(repository.asMavenRemoteRepo());
    }

    public boolean contains(Repository repository) {
        return repoTracker.contains(repository);
    }

}
