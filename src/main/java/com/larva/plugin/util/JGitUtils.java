package com.larva.plugin.util;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

public class JGitUtils {

	private static class SingletonClassInstance {
		private static final JGitUtils instance = new JGitUtils();
	}

	public static JGitUtils getInstance() {
		return SingletonClassInstance.instance;
	}
	
	private String gitpath;
	
	public JGitUtils() {
		super();
	}

	public JGitUtils loadGitPath(String gitpath) {
		this.gitpath = gitpath;
		return this;
		
	}
	
    public Git build() throws IOException{
    	File root = new File(this.gitpath);
    	return Git.open(root);
    }
    
	/***
	 * 根据commitId获取tree
	 * @param commit
	 * @param reader
	 * @param walk 
	 * @return
	 * @throws MissingObjectException
	 * @throws IncorrectObjectTypeException
	 * @throws IOException
	 */
	public AbstractTreeIterator prepareTreeParser(ObjectId id,ObjectReader reader,RevWalk walk) throws MissingObjectException, IncorrectObjectTypeException, IOException{
		 RevTree tree = walk.parseTree(id);
         CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
         oldTreeParser.reset(reader,tree.getId());
         walk.dispose();
         return oldTreeParser;
	}
}
