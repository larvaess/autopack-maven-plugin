package com.larva.plugin.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import com.larva.plugin.pojo.AutoPackPojo;
import com.larva.plugin.util.exception.AutoPackException;

public class AutoDeployUtils extends SystemStreamLog{

	private static class SingletonClassInstance {
		private static final AutoDeployUtils instance = new AutoDeployUtils();
	}

	private AutoDeployUtils(){}

	public static AutoDeployUtils getInstance() {
		return SingletonClassInstance.instance;
	}

	private Git git;
    
	private AutoPackPojo o;
	
	private Properties p;
	
	private String compliePath;
	
	/***
     * 获取增量文件路径
     * @param child
     * @param parent
     * @param sourcedirectory 项目的主源码目录
     * @param warsourcedirectory 
     * @return
     * @throws RevisionSyntaxException
     * @throws AmbiguousObjectException
     * @throws IncorrectObjectTypeException
     * @throws IOException
     * @throws GitAPIException
     */
	public List<String> differential(String sourceName,String projectpath) throws RevisionSyntaxException,AmbiguousObjectException, IncorrectObjectTypeException, IOException, GitAPIException{
    	// 获取仓库
		Repository repository = git.getRepository();
		ObjectReader reader = repository.newObjectReader();
		// 获取当前分支的名称
		String currentBranch = repository.getBranch();
		// 分支判断
		info("当前分支名称：" + currentBranch + "===========,与之比对的分支名称：" + o.getParentBranch());
		if (currentBranch.equals(o.getParentBranch())) {
			throw new AutoPackException("请选择不同分支进行比较,分支间的比较必须保证在不同分支上");
		}
		// 获取父子分支
		ObjectId child = repository.resolve(currentBranch +"^{tree}");
		ObjectId head = repository.resolve(o.getParentBranch()+"^{tree}");
		// 要与谁比对的分支
		CanonicalTreeParser childTree = new CanonicalTreeParser();
		childTree.reset(reader,child);
		// 需要比对的分支
		CanonicalTreeParser headTree = new CanonicalTreeParser();
		headTree.reset(reader,head);
		// 获取差异
		List<DiffEntry> diffs = git.diff().setNewTree(childTree).setOldTree(headTree).setShowNameAndStatusOnly(true).call();
		return executeAddFiles(sourceName,projectpath,diffs);
	}

	 /***
     * 获取增量文件路径
	 * @param projectpath 
	 * @param warsourcedirectory 
     * @return
     * @throws RevisionSyntaxException
     * @throws AmbiguousObjectException
     * @throws IncorrectObjectTypeException
     * @throws IOException
     * @throws GitAPIException
     */
	public List<String> increment(String sourceName, String projectpath) throws RevisionSyntaxException,AmbiguousObjectException, IncorrectObjectTypeException, IOException, GitAPIException{
		// 获取仓库
		Repository repository = git.getRepository();
		ObjectReader reader = repository.newObjectReader();
		RevWalk walk = new RevWalk(repository);
		// 获取当前分支的名称
		String currentbranch = repository.getBranch();
		info("当前分支名称：" + currentbranch);
		ObjectId curroid = null;
		if (null == o.getParentCommitid() || "".equals(o.getParentCommitid())) {
			curroid = repository.resolve(currentbranch);
		} else {
			curroid = ObjectId.fromString(o.getParentCommitid());
		}
		ObjectId formoid = ObjectId.fromString(o.getChildCommitid());
		info("父节点commitid:" + curroid + "===========,子节点commitid:" + formoid);
		// commitId 比较
		if (curroid.equals(formoid)) {
			throw new AutoPackException("同分支比较请选择不同的提交点进行比较!!!");
		}
		List<RevCommit> commitList = new ArrayList<RevCommit>();
    	// 获取两次ID之间的记录
    	Iterable<RevCommit> commits = git.log().addRange(formoid,curroid).call();
    	// 打印提交的log
    	for(RevCommit commit:commits){
    		commitList.add(commit);
    		info(commit.getFullMessage());
    	}
		// 获取差异
		AbstractTreeIterator newTree = JGitUtils.getInstance().prepareTreeParser(curroid,reader,walk);
    	AbstractTreeIterator oldTree = JGitUtils.getInstance().prepareTreeParser(formoid,reader,walk);
		List<DiffEntry> diffs = git.diff().setNewTree(newTree).setOldTree(oldTree).setShowNameAndStatusOnly(true).call();
		return executeAddFiles(sourceName,projectpath,diffs);
	}
	
	private List<String> executeAddFiles(String sourceName,String projectpath,List<DiffEntry> diffs)
			throws IOException {
		// 获取src资源包名称
		sourceName = sourceName.replace(projectpath,"").replaceFirst(AutoPackConstants.MATCHER_DELIMITER,"");
		// 格式化
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DiffFormatter df = new DiffFormatter(out);
		df.setRepository(git.getRepository());
		// 存放的路径
		List<String> paths = new ArrayList<String>();
		// 开始处理获取增量文件
		for (DiffEntry diffEntry : diffs) {
			// 格式化
			df.format(diffEntry);
			// 获取文件路径
			String newPath = diffEntry.getNewPath();
			// 打印文件路径及状态
			info("增量文件" + newPath + ",当前状态：" + diffEntry.getChangeType());
			if (diffEntry.getChangeType() == ChangeType.DELETE) {
				// 说明新增的文件被删除了继续查找下一个
				continue;
			}
			// 二进制文件处理
	    	if (newPath.contains(sourceName)) {
	    		newPath = newPath.replace(sourceName,AutoPackConstants.CLASSES_JAVA_PATH);
	    	}
	    	if (newPath.endsWith(".java")) {
	    		newPath = newPath.replace(".java",".class");
	    	}
	    	// 静态资源文件处理
	    	if (newPath.contains(o.getWarSourceDirectory())) {
	    		newPath = newPath.replace(o.getWarSourceDirectory(),"").replaceFirst(AutoPackConstants.LINUX_DELIMITER,"");
	    	}
	    	paths.add(newPath);
		}
		return paths;
	}
	
	/***
	 * 加载Git
	 * @param propertiesPath 配置文件路径
	 * @return
	 */
	public AutoDeployUtils loadGit(Git git) {
		this.git = git;
		return this;
	}
	
	
	/***
	 * 读取配置文件
	 * @param projectpath 
	 * @param propertiesPath 配置文件路径
	 * @return
	 */
	public AutoDeployUtils loadProperties(String pp, String projectpath) {
		if (null == pp || "".equals(pp)) {
			pp = projectpath + AutoPackConstants.DELIMITER + AutoPackConstants.DEFAULT_CONFIG_FILE;
		}
		p = PropertiesUtils.getInstance().loadProps(pp);
		return this;
	}
	
	/***
	 * 设置对象值
	 * @return
	 */
	public AutoDeployUtils proIncrement() {
		o = new AutoPackPojo();
		o.setChildCommitid(p.getProperty("childcommitid"));
		o.setParentCommitid(p.getProperty("parentcommitid"));
		o.setWarSourceDirectory(p.getProperty("warsourcedirectory",AutoPackConstants.WEBROOT_JAVA_PATH));
		return this;
	}
	
	public AutoDeployUtils proDifferential() {
		o = new AutoPackPojo();
		o.setWarSourceDirectory(p.getProperty("warsourcedirectory",AutoPackConstants.WEBROOT_JAVA_PATH));
		o.setParentBranch(p.getProperty("parentbranch",AutoPackConstants.DEFAULT_BRANCH));
		return this;
	}
	
	/***
	 * 读取配置文件
	 * @param propertiesPath 配置文件路径
	 * @return
	 */
	public AutoDeployUtils loadCompilePath(String compliePath) {
		this.compliePath = compliePath.replaceAll(AutoPackConstants.MATCHER_DELIMITER,AutoPackConstants.LINUX_DELIMITER);
		return this;
	}
	
	/***
	 * 排除不必要的文件
	 * @param file
	 * @param time
	 */
	public void deleteFile(List<String> diffMethod) {
		if (null == diffMethod || diffMethod.isEmpty()) {
			throw new AutoPackException("没有增量文件暂无法打包");
		}
		File file = new File(this.compliePath);
		if (file.isFile()) return;
		// 剔除不符合条件的文件
		deleteFile(file,diffToMap(diffMethod));
		// 删除空文件夹
		FileUtils.getInstance().clear(file);
	}

	private Map<String, String> diffToMap(List<String> diffMethod) {
		Map<String,String> map = new HashMap<String,String>();
		for (String str : diffMethod) {
			int lastIndexOf = str.lastIndexOf(AutoPackConstants.LINUX_DELIMITER);
			// 路径 + 文件名
			map.put(str,str.substring(lastIndexOf+1));
		}
		return map;
	}

	/***
	 * 删除不符合的文件
	 * @param file
	 * @param compilepath
	 * @param pathMap
	 */
	public void deleteFile(File file, Map<String,String> pathMap){
		File[] fs = file.listFiles();
		for(File f:fs){
			if(f.isDirectory())	//若是目录，则递归
				deleteFile(f,pathMap);
			if(f.isFile()) {
				String path = f.getPath();
				path = path.replaceAll(AutoPackConstants.MATCHER_DELIMITER,AutoPackConstants.LINUX_DELIMITER).replace(this.compliePath,"").replaceFirst(AutoPackConstants.LINUX_DELIMITER,"");
				// 判断一些class文件的匿名内部类问题
				if (path.endsWith(".class")) {
					int dlast = path.lastIndexOf(".");
					int $last = path.lastIndexOf("$");
					// 获得该文件名
					if (dlast > -1 && $last > -1) {
						// 说明可能有匿名内部类
						String repeat = path.substring($last,dlast);
						path = path.replace(repeat,"");
					}
					if (!pathMap.containsKey(path)) {
						f.delete();
					}
				}else {
					if (!pathMap.containsKey(path)) {
						f.delete();
					}
				}
			}
		}
	}
}
