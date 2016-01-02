package hu.vetesii.gfs.data.githubv3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Permissions {
	
	public boolean admin;
	public boolean push;
	public boolean pull;
}
