package hu.vetesii.gfs.data.githubv3;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {

    public String actor;
    public ActorAttributes actor_attributes;
    public String created_at;
    public Payload payload;

    @JsonProperty("public")
    public Boolean public_;

    public RepositoryBrief repository;
    public String type;
    public String url;
}
