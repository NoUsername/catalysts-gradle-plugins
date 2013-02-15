package cc.catalysts.gradle.plugins.codegen.java

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class CodegenJavaExtension {
	String packageName
    String templateExtension = ".tpl"
    List<String> tplFiles = []
	List<String> destDirs = []

    Closure onGenerate
	
	public packageName(pack) {
		this.packageName = pack
	}
	
	public build(Closure closure) {
		closure(this)
	}
}