package com.cyr1en.javen;

import com.cyr1en.javen.util.JavenUtil;

import java.net.MalformedURLException;
import java.net.URL;

import static com.cyr1en.javen.Javen.LOGGER;

public class Repository {

  private String id;
  private String repositoryURL;

  public Repository(String id, String repositoryURL) {
    this.id = id;
    this.repositoryURL = prepareURL(repositoryURL);
  }

  public String getId() {
    return id;
  }

  public String getRepositoryURL() {
    return repositoryURL;
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

}
