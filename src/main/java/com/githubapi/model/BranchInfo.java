package com.githubapi.model;

public class BranchInfo {
    private String name;
    private CommitInfo commit;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommitInfo getCommit() {
        return commit;
    }

    public void setCommit(CommitInfo commit) {
        this.commit = commit;
    }
}
