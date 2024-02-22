package com.githubapi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RepositoryInfo {
    private final String name;
    private Owner owner;
    private List<BranchInfo> branches;
    private final boolean isFork;

    @JsonCreator
    public RepositoryInfo(@JsonProperty("name") String name,
                          @JsonProperty("owner") Owner owner,
                          @JsonProperty("branches") List<BranchInfo> branches,
                          @JsonProperty("isFork") boolean isFork) {
        this.name = name;
        this.owner = owner;
        this.branches = branches;
        this.isFork = isFork;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwnerLogin(Owner owner) {
        this.owner = owner;
    }


    public String getName() {
        return name;
    }


    public List<BranchInfo> getBranches() {
        return branches;
    }


    public boolean isFork() {
        return isFork;
    }

    public void setBranches(List<BranchInfo> branches) {
        this.branches = branches;
    }


}
