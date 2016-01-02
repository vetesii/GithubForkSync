package hu.vetesii.gfs.data.githubv3.jsondataobject;

public class PullRequestData {
	private String title;
	private String body;
	private String head;
	private String base;
	
	public PullRequestData(String title, String body, String head, String base){
		this.title = title;
		this.body = body;
		this.head = head;
		this.base = base;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	
}
