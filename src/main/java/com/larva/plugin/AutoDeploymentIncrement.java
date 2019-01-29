package com.larva.plugin;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.jgit.api.Git;

import com.larva.plugin.util.AutoDeployUtils;
import com.larva.plugin.util.AutoPackConstants;
import com.larva.plugin.util.FileUtils;
import com.larva.plugin.util.JGitUtils;

@Mojo(name = "increment")
public class AutoDeploymentIncrement extends AbstractMojo{
	
	@Parameter (defaultValue = "${basedir}")
	private String gitpath;
	
	@Parameter (defaultValue = "${basedir}")
	private String projectpath;
	
	@Parameter (defaultValue = "${project.build.directory}/${project.build.finalName}")
	private String compilepath;
	
	@Parameter (defaultValue = "${project.build.sourceDirectory}")
	private String sourcedirectory;
	
	@Parameter (defaultValue = "${project.build.outputDirectory}")
	private String outputdirectory;
	
	@Parameter
	private String builderpath;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			// 初始化 GIT
			Git git = JGitUtils.getInstance().loadGitPath(gitpath).build();
			// 由于打包的文件夹编译的文件不完整性存在缓存所以1.5版本后进行清空
			File securityFile = new File(compilepath + AutoPackConstants.DELIMITER + AutoPackConstants.SECURITY_JAVA_PATH);
			FileUtils.getInstance().deleteFiles(securityFile,true);
			// copy文件夹
			FileUtils.getInstance().copyFolder(outputdirectory,compilepath + AutoPackConstants.DELIMITER + AutoPackConstants.CLASSES_JAVA_PATH);
			// 比较分支代码
			List<String> increments = AutoDeployUtils.getInstance().loadGit(git).loadProperties(builderpath,projectpath).proIncrement().increment(sourcedirectory,projectpath);
			// 删除不必要的代码
			AutoDeployUtils.getInstance().loadCompilePath(compilepath).deleteFile(increments);
		} catch (Exception e) {
			getLog().error("获取部署增量包报错：" + e.getLocalizedMessage());
		}
	}
	
}
