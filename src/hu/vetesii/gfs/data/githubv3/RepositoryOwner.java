package hu.vetesii.gfs.data.githubv3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RepositoryOwner {
	public String login;
	public int id;
	public String url;
	public String repos_url;
	public String type;
}
