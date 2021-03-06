package com.brewinapps.ios;

import java.io.File;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;


/**
 * 
 * @author Brewin' Apps AS
 * @goal package
 * @phase package
 */
public class IOSPackageMojo extends IOSAbstractMojo {
	
	/**
	 * iOS app name
	 * @parameter
	 * 		expression="${ios.appName}"
	 * @required
	 */
	private String appName;
	
	/**
	 * iOS build parameters
	 * @parameter
	 * 		expression="${ios.buildParams}"
	 */
	private Map<String, String> buildParams;
	
	/**
	* The maven project.
	* 
	* @parameter expression="${project}"
	* @required
	* @readonly
	*/
	protected MavenProject project;
	
	private String appDir;
	private String targetDir;
	
	
	/**
	 * 
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			intialize();
			validateParameters();
			
			String finalName = project.getBuild().getFinalName();
			if (null == finalName) {
				finalName = appName;
			}
			
			final String packageName = finalName + ".zip";
			packageApp(packageName);
			
			project.getArtifact().setFile(new File(appDir + "/" + packageName));
		} catch (IOSException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
	
	protected void intialize() {
		targetDir = project.getBuild().getDirectory();
		appDir = targetDir + "/" + buildParams.get("buildConfiguration") + "-" + DEFAULT_SDK + "/";
	}
	
	protected void validateParameters() throws IOSException {
		if (null == buildParams.get("buildConfiguration")) {
			buildParams.put("buildConfiguration", DEFAULT_BUILD_CONFIGURATION);
		}
	}
	
	protected void packageApp(String packageName) throws IOSException {
		ProcessBuilder pb = new ProcessBuilder(
				"zip",
				"-r", 
				packageName, 
				appName + ".app.dSYM",
				appName + ".ipa");
		pb.directory(new File(appDir));
		executeCommand(pb);
	}
}
