package com.cyr1en.javen;

import java.util.LinkedList;

public class Repositories extends LinkedList<Repository> {

  public Repositories() {
    add(new Repository("maven-central", "https://repo.maven.apache.org/maven2/"));
  }

  public void addRepo(Repository repository) {
    add(repository);
  }

  public void add(String id, String url) {
    Repository repository = new Repository(id, url);
    addRepo(repository);
  }

}
