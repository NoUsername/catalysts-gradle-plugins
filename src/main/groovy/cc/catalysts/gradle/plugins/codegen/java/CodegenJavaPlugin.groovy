package cc.catalysts.gradle.plugins.codegen.java

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.SourceSet
import cc.catalysts.gradle.plugins.codegen.CodegenTask

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CodegenJavaPlugin implements Plugin<Project> {
	void apply(Project project) {
        project.apply plugin: 'java'
		
		if(project.tasks.findByPath('codegen') == null) {
			project.task('codegen', type: CodegenTask)
		}
		
		project.extensions.codegenjava = new CodegenJavaExtension()
		project.task('codegenJavaBuild', type: CodegenJavaTask)
		
		project.convention.plugins.java.sourceSets.all { SourceSet sourceSet ->
			if(!sourceSet.name.toLowerCase().contains("test")) {
				def path = "${project.projectDir}/target/generated-sources/${sourceSet.name}/cp"
				project.extensions.codegenjava.destDirs.add path
                def tplPath = new File("${project.projectDir}/src/tpl/")
                if (tplPath.exists()) {
                    String base = tplPath.getPath()
                    String tplExtension = project.extensions.codegenjava.templateExtension
                    tplPath.eachFileRecurse(groovy.io.FileType.FILES) {
                        String absolute = it.getPath()
                        String relativePath = getRelativePath(absolute, base)
                        println "FOUND: $it, RELATIVE: $relativePath"
                        if (it.getPath().endsWith(tplExtension)) {
                            project.extensions.codegenjava.tplFiles.add(relativePath)
                        }
                    }
                }
				sourceSet.java { srcDir path }
			}
		}
	}

    String getRelativePath(String fullPath, String makeRelativeTo) {
        return new File(makeRelativeTo).toURI().relativize(new File(fullPath).toURI()).getPath();
    }
}