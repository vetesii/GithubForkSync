package hu.vetesii.gfs.data.githubv3;

import java.util.ArrayList;
import java.util.List;

/**
 * Errors list is null-proof. Errors setter make an empty list if it get a null value.
 * 
 * @author vetesii
 *
 */
public class Error {
	public String message;
	private List<Errors> errors;
	public String documentation_url;
	
	public List<Errors> getErrors() {
		return errors;
	}
	public void setErrors(List<Errors> errors) {
		this.errors = errors;
		if(errors==null)
			this.errors = new ArrayList<Errors>();
	}
	
	
}
