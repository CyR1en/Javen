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

import com.google.common.collect.ImmutableList;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.cyr1en.javen.Javen.LOGGER;

public class URLResolver {

  private Repositories repositories;

  public URLResolver(Repositories repositories) {
    this.repositories = repositories;
  }

  /**
   * Resolves {@link URL} for a {@link Collection} of {@link Dependency}.
   *
   * <p>This method will resolve every {@link Dependency} in a {@link Collection}
   * by calling {@link URLResolver#resolve(Dependency)}--which returns a null if
   * the {@link URL} of a {@link Dependency} was not resolved. Because of that,
   * this method will filter out those un-resolved {@link Dependency} and return
   * an {@link ImmutableList} of resolved {@link URL}</p>
   *
   * @param dependencies {@link Collection} of {@link Dependency} that you want
   *                     resolve the {@link URL} for.
   * @return {@link ImmutableList} of resolved {@link URL}.
   */
  public List<URL> resolve(Collection<? extends Dependency> dependencies) {
    List<URL> resolved = new ArrayList<>();
    for (Dependency deps : dependencies)
      resolved.add(resolve(deps));
    return ImmutableList.copyOf(resolved.stream()
            .filter(Objects::nonNull).collect(Collectors.toList()));
  }

  /**
   * Resolves a {@link URL} for a {@link Dependency}.
   *
   * <p>This method will walk through all of the {@link Repository} in
   * the {@link Repositories} and checks if that {@link Repository} contains
   * the {@link URL} for the {@link Dependency}.
   * If non of the {@link Repository} contains the {@link URL} for the
   * {@link Dependency}, this method returns a null.</p>
   *
   * @param dependency {@link Dependency} that this method will resolve.
   * @return {@link URL} if successfully resolved, null if not.
   */
  public URL resolve(Dependency dependency) {
    for (Repository repo : repositories)
      if (repo.contains(dependency))
        return repo.getURLOf(dependency);
    LOGGER.warn("Cannot resolve URL for " + dependency.toString() + "!");
    return null;
  }

  public Repositories getRepositories() {
    return repositories;
  }

  public void setRepositories(Repositories repositories) {
    this.repositories = repositories;
  }
}
