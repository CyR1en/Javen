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

import com.cyr1en.javen.util.JavenUtil;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenRemoteRepositories;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenRemoteRepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import static com.cyr1en.javen.Javen.LOGGER;

public class Repository {

    private final String id;
    private final String repositoryURL;
    private final String layout;

    public Repository(String id, String repositoryURL) {
        this(id, repositoryURL, null);
    }

    public Repository(String id, String repositoryURL, String layout) {
        this.id = id;
        this.repositoryURL = prepareURL(repositoryURL);
        this.layout = Objects.isNull(layout) ? "default" : layout;
    }

    public String getId() {
        return id;
    }

    public String getRepositoryURL() {
        return repositoryURL;
    }

    public String getLayout() {
        return layout;
    }

    private String prepareURL(String s) {
        String trimmed = s.trim();
        return trimmed.endsWith("/") ?
                trimmed : trimmed + "/";
    }

    public boolean contains(Dependency dependency) {
        String fullURL = getRepositoryURL() + dependency.asURL();
        try {
            URL url = new URL(fullURL);
            if (JavenUtil.validURL(url))
                return true;
        } catch (MalformedURLException e) {
            LOGGER.error(fullURL + " is malformed!");
        }
        return false;
    }

    public URL getURLOf(Dependency dependency) {
        String fullURL = getRepositoryURL() + dependency.asURL();
        if (!contains(dependency)) return null;
        try {
            return new URL(fullURL);
        } catch (MalformedURLException e) {
            LOGGER.error(fullURL + " is malformed!");
        }
        return null;
    }

    public MavenRemoteRepository asMavenRemoteRepo() {
        return MavenRemoteRepositories.createRemoteRepository(getId(), getRepositoryURL(), getLayout());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Repository)) return false;
        Repository that = (Repository) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(repositoryURL, that.repositoryURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, repositoryURL);
    }

    @Override
    public String toString() {
        return "Repository{" +
                "id='" + id + '\'' +
                ", repositoryURL='" + repositoryURL + '\'' +
                '}';
    }
}
