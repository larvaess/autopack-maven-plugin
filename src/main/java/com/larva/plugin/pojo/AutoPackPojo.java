package com.larva.plugin.pojo;

public class AutoPackPojo {

	private String warSourceDirectory;
	
	private String sourceDirectory;
	
	private String parentBranch;
	
	private String parentCommitid;
	
	private String childCommitid;

	public String getWarSourceDirectory() {
		return warSourceDirectory;
	}

	public void setWarSourceDirectory(String warSourceDirectory) {
		this.warSourceDirectory = warSourceDirectory;
	}

	public String getSourceDirectory() {
		return sourceDirectory;
	}

	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public String getParentBranch() {
		return parentBranch;
	}

	public void setParentBranch(String parentBranch) {
		this.parentBranch = parentBranch;
	}

	public String getParentCommitid() {
		return parentCommitid;
	}

	public void setParentCommitid(String parentCommitid) {
		this.parentCommitid = parentCommitid;
	}

	public String getChildCommitid() {
		return childCommitid;
	}

	public void setChildCommitid(String childCommitid) {
		this.childCommitid = childCommitid;
	}
	
}
