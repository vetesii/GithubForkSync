package hu.vetesii.gfs.data.githubv3.jsondataobject;

public class MergePullRequestData {
	
	public String commit_message;
	public String sha;
	
	public MergePullRequestData(){
		
	}
	
	public MergePullRequestData(String commit_message, String sha){
		this.commit_message = commit_message;
		this.sha = sha;
	}
}
