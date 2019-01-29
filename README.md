# autopack-maven-plugin
自动获取maven构建项目的增量包

使用方法：

1、增加仓库地址

```java
<pluginRepositories>
       	<pluginRepository>
            <id>yanxiaochuan-maven-repo-release</id>
       		<url>https://raw.githubusercontent.com/yanxiaochuan/mvn-repo/release</url>
       	</pluginRepository>
</pluginRepositories>
```

2、插件引入方式

```java 
<plugin>
    <groupId>com.larva.plugin</groupId>
    <artifactId>autopack-maven-plugin</artifactId>
    <version>0.1.0</version>
    <executions>
    	<execution>
    		<phase>package</phase>
    		<goals>
    		     <goal>increment</goal>
   		</goals>
    	</execution>
    </executions>
    <dependencies>
    	<dependency>
    		<groupId>org.eclipse.jgit</groupId>
    		<artifactId>org.eclipse.jgit</artifactId>
    		<version>3.4.2.201412180340-r</version>
    	</dependency>
    </dependencies>
</plugin>
```

goal包含两个：

increment：同分支不同commidId之间的增量,默认为当前分支所处的commitId与所设置的commitId之间的增量

differential：不同分支之间的增量，默认为当前所在的分支与master分支间的比较

3、关于builder.properties

默认加载项目路径下builder.properties文件

```properties
###parentbranch=develop
###parentcommitid=
###warsourcedirectory=WebRoot
###securitycatalog=WEB-INF
childcommitid =xxxxxxxxxx
```
4、使用
mvn clean package
