package hu.vetesii.gfs;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import hu.vetesii.gfs.data.githubv3.Error;
import hu.vetesii.gfs.data.githubv3.Errors;
import hu.vetesii.gfs.data.githubv3.PullRequest;
import hu.vetesii.gfs.data.githubv3.Repository;
import hu.vetesii.gfs.data.githubv3.User;
import hu.vetesii.gfs.data.githubv3.jsondataobject.MergePullRequestData;
import hu.vetesii.gfs.data.githubv3.jsondataobject.PullRequestData;

/**
 * 
 * @author vetesii
 * 
 */
public class Main {	
	static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		LOG.info("* --------------- *");
		LOG.info("* Program started *");
		LOG.info("* --------------- *");
		
		String token = "";
		if(args.length > 0) {
			token = args[0];
			LOG.info("Token length:{}", token.length());
		}
		LOG.debug("Token parameter:<{}>", token);
		
		User user = getUserData(token);
		
		List<Repository> rList = getUserRepositoryList(token);		
		for (Repository rep : rList) {
			if(!rep.fork) 
				continue;	// if not fork: jump next 
			LOG.info("Check <{}> repository.", rep.full_name);
			Repository fRep = getRepositoryData(rep, token);
			
			createPullRequest(fRep, token);
			
//			if(pReq != null && pReq.user != null && user != null && pReq.user.id == user.id)
//				mergePullRequest(pReq, TOKEN);
			
			List<PullRequest> pList = getPullRequestList(fRep, token);
			for (PullRequest pr : pList) {
				// null and pull request owner checking
				if(pr != null && pr.user != null && user != null && pr.user.id.equals(user.id))
					mergePullRequest(pr, token);
			}			
		}
		        
		LOG.info("Program stopped");
	}
	
	/**
	 * 
	 * @param token Github Authorization token, place in HTTP GET header (for authorisation)
	 * @return User POJO with user data
	 * @throws IOException Re-throw HttpComponents exceptions after logging
	 */
	public static User getUserData(String token) throws IOException {
		String url = "https://api.github.com/user";
		
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Authorization", "token " + token);
		
		LOG.info("Executing request: {}", httpGet.getRequestLine());		
		Util.logHeaderToDebug(httpGet);		
		
		try(	CloseableHttpClient client = HttpClients.createDefault();
				CloseableHttpResponse httpResponse = client.execute(httpGet);){
			
			LOG.info("Response status line:{}", httpResponse.getStatusLine());
			Util.logHeaderToDebug(httpResponse);
	        String response = EntityUtils.toString(httpResponse.getEntity());
	        LOG.debug("Response:{}", response);
	        User user = new ObjectMapper().readValue(response, User.class);
	        
	        return user;
		} catch (IOException e) {
			LOG.error("User data request error:", e);
			throw e;
		}
	}
	
	/**
	 * 
	 * @param token Github Authorization token, place in HTTP GET header (for authorisation)
	 * @return User's repository list
	 * @throws IOException Re-throw HttpComponents exceptions after logging
	 */
	public static List<Repository> getUserRepositoryList(String token) throws IOException {
		String url = "https://api.github.com/user/repos";
		
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Authorization", "token " + token);
		
		LOG.info("Executing request: {}", httpGet.getRequestLine());		
		Util.logHeaderToDebug(httpGet);		
		
		try(	CloseableHttpClient client = HttpClients.createDefault();
				CloseableHttpResponse httpResponse = client.execute(httpGet);){
			
			LOG.info("Response status line:{}", httpResponse.getStatusLine());
			Util.logHeaderToDebug(httpResponse);
	        String response = EntityUtils.toString(httpResponse.getEntity());
	        LOG.debug("Response:{}", response);
	        Repository[] repositories = new ObjectMapper().readValue(response, Repository[].class);
	        LOG.info("Repository count:{}", repositories.length);
	        
	        return Arrays.asList(repositories);
		} catch (IOException e) {
			LOG.error("User repository list request error:", e);
			throw e;
		}
	}
	
	/**
	 * Get all data for repository. You can get base repository data for example from 
	 * {@link #getUserRepositoryList(String) getUserRepositoryList} method.
	 * 
	 * @param repo Base repository data with URL field
	 * @param token Github Authorization token, place in HTTP GET header (for authorisation)
	 * @return Repository with all data
	 * @throws IOException Re-throw HttpComponents exceptions after logging
	 */
	public static Repository getRepositoryData(Repository repo, String token) throws IOException{
		String url = repo.url;
		
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Authorization", "token " + token);
		
		LOG.info("Executing request: {}", httpGet.getRequestLine());		
		Util.logHeaderToDebug(httpGet);		
		
		try(	CloseableHttpClient client = HttpClients.createDefault();
				CloseableHttpResponse httpResponse = client.execute(httpGet);){
			
			LOG.info("Response status line:{}", httpResponse.getStatusLine());
			Util.logHeaderToDebug(httpResponse);
	        String response = EntityUtils.toString(httpResponse.getEntity());
	        LOG.debug("Response:{}", response);
	        Repository repository = new ObjectMapper().readValue(response, Repository.class);
	        
	        return repository;
		} catch (IOException e) {
			LOG.error("Repository data request error:", e);
			throw e;
		}
	}
	
	/**
	 * Call {@link #createPullRequest(Repository, String, String, String) createPullRequest} with default title and body text
	 * @param repo
	 * @param token
	 * @throws IOException
	 */
	public static PullRequest createPullRequest(Repository repo, String token) throws IOException {
		return createPullRequest(repo, token, "Automatic fork sync", "Automatic merge body");
	}
	
	/**
	 * Create a pull request for a fork. This method use parent repository of the fork, not the source repository.
	 * 
	 * @param repo Repository data with URL, parent-owner-login, parent-default_branch and default_branch field
	 * @param token Github Authorization token, place in HTTP GET header (for authorisation)
	 * @return A pull request data object or null (when no commit between the fork and the parent)
	 * @throws IOException Re-throw HttpComponents and Jackson objectmapper exceptions after logging
	 */
	public static PullRequest createPullRequest(Repository repo, String token, String title, String body) throws IOException {
		String url = repo.url + "/pulls"; // https://api.github.com/repos/:user/:repo/pulls
		String head = repo.parent.owner.login + ":" + repo.parent.default_branch; // username + : + branch name
		String defaultBranch = repo.default_branch;
		
		PullRequestData pReqD = new PullRequestData("Automatic fork sync", "Automatic merge body", head, defaultBranch);
		
		try{
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Authorization", "token " + token);
			httpPost.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(pReqD)));
			
			LOG.info("Executing request: {}", httpPost.getRequestLine());		
			Util.logHeaderToDebug(httpPost);		
			
			try(	CloseableHttpClient client = HttpClients.createDefault();
					CloseableHttpResponse httpResponse = client.execute(httpPost);){
				
				LOG.info("Response status line:{}", httpResponse.getStatusLine());
				Util.logHeaderToDebug(httpResponse);
		        String response = EntityUtils.toString(httpResponse.getEntity());
		        		        
		        try{
		        	// Get an error
		        	Error err = new ObjectMapper().readValue(response, Error.class);
		        	for (Errors er : err.getErrors()) 
		        		if(er.message.startsWith("No commits between")) {
		        			LOG.info("{}. Response:{}", er.message, response);
		        			return null;
		        		}
		        	
					LOG.error("Create pull request error. Response:{}", response);
		        	return null;
		        } catch (UnrecognizedPropertyException e){
		        	LOG.debug("Response:{}", response);
		        	try {
		        		PullRequest pReq = new ObjectMapper().readValue(response, PullRequest.class);
		        		return (pReq.url == null || pReq.id == null) ? null : pReq;
		        		// TODO Kicsit még átgondolni
					} catch (Exception e2) {
						LOG.error("Pull request mapping error",e2);
						return null;
					}		        	
		        }
			} 
		} catch (IOException e) {
			LOG.error("Repository data request error:", e);
			throw e;
		}
	}
	
	/**
	 * Get a repository pull request list.
	 * 
	 * @param repo Repository data with URL
	 * @param token Github Authorization token, place in HTTP GET header (for authorisation)
	 * @return
	 * @throws IOException Re-throw HttpComponents and Jackson objectmapper exceptions after logging
	 */
	public static List<PullRequest> getPullRequestList(Repository repo, String token) throws IOException{
		String url = repo.url + "/pulls"; // https://api.github.com/repos/:user/:repo/pulls
		
		try{
			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("Authorization", "token " + token);
			
			LOG.info("Executing request: {}", httpGet.getRequestLine());		
			Util.logHeaderToDebug(httpGet);		
			
			try(	CloseableHttpClient client = HttpClients.createDefault();
					CloseableHttpResponse httpResponse = client.execute(httpGet);){
				
				LOG.info("Response status line:{}", httpResponse.getStatusLine());
				Util.logHeaderToDebug(httpResponse);
		        String response = EntityUtils.toString(httpResponse.getEntity());
		        LOG.debug("Response:{}", response);
		        
		        List<PullRequest> pullRequestList =  new ObjectMapper().readValue(response, new TypeReference<List<PullRequest>>() {});
			
		        return pullRequestList;
			} 
		} catch (IOException e) {
			LOG.error("Repository data request error:", e);
			throw e;
		}
	}
	
	/**
	 * Call {@link #mergePullRequest(Repository, String, String) mergePullRequest} with default commit message
	 * @param pReq
	 * @param token
	 * @throws IOException
	 */
	public static void mergePullRequest(PullRequest pReq, String token) throws IOException{
		mergePullRequest(pReq, token, "Automatic fork sync");
	}
	
	/**
	 * Merge a pull request.
	 * @param pReq Pull request data
	 * @param token Github Authorization token, place in HTTP GET header (for authorisation)
	 * @param commitMessage
	 * @throws IOException Re-throw HttpComponents and Jackson objectmapper exceptions after logging
	 */
	public static void mergePullRequest(PullRequest pReq, String token, String commitMessage) throws IOException{
		MergePullRequestData mpr = new MergePullRequestData(commitMessage, pReq.head.sha);
		String url = pReq._links.self.href + "/merge";
		
		try{
			HttpPut httpPut = new HttpPut(url);
			httpPut.addHeader("Authorization", "token " + token);
			httpPut.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(mpr)));
			
			LOG.info("Executing request: {}", httpPut.getRequestLine());		
			Util.logHeaderToDebug(httpPut);		
			
			try(	CloseableHttpClient client = HttpClients.createDefault();
					CloseableHttpResponse httpResponse = client.execute(httpPut);){
				
				LOG.info("Response status line:{}", httpResponse.getStatusLine());
				Util.logHeaderToDebug(httpResponse);
		        String response = EntityUtils.toString(httpResponse.getEntity());
		        if(httpResponse.getStatusLine().toString().contains("200 OK"))
		        	LOG.info("Response:{}", response);
		        else 
		        	LOG.error("Pull request merge error:{}", response);
			} 
		} catch (IOException e) {
			LOG.error("Repository data request error:", e);
			throw e;
		}
	}
	
}
