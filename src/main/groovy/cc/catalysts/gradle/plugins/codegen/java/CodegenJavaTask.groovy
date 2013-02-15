package cc.catalysts.gradle.plugins.codegen.java

import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.text.SimpleDateFormat

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CodegenJavaTask extends DefaultTask {
	
	CodegenJavaTask() {
		project.tasks.codegen.dependsOn(this)
		project.tasks.compileJava.dependsOn(this)
	}
	
	@TaskAction
	def build() {
		def pName = project.codegenjava.packageName
		if(pName != null) {
			Calendar calendar = Calendar.getInstance()
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm")
			def date = dateFormat.format(calendar.getTime())
			def version = project.version
			project.codegenjava.destDirs.each { destDirPath ->
				File destDir = new File(destDirPath)
				if(destDir.exists()) {
					destDir.deleteDir()
				}
				new File(destDirPath + "/" + pName.replace('.', '/')).mkdirs()
				def f = new File(destDirPath + "/" + pName.replace('.', '/') + '/Build.java')
				def w = f.newWriter()
				
				w << 'package ' << pName << ';\r\n'
				w << '\r\npublic class Build {\r\n'
				w << '\tpublic static final String VERSION = "' + version + '";\r\n'
				w << '\tpublic static final String DATE = "' + date + '";\r\n'
				w << '}'
				
				w.close()

                String tplExtension = project.extensions.codegenjava.templateExtension
                project.codegenjava.tplFiles.each { tplFile ->
                    String targetFile = tplFile
                    if (targetFile.endsWith(tplExtension)) {
                        targetFile = targetFile.substring(0, targetFile.length() - tplExtension.length())
                    }
                    File generatedFile = new File(destDirPath, targetFile)
                    Writer writer = new FileWriter(generatedFile)
                    println "generating... "
                    generateFile(tplFile, writer)
                    println "done"
                    writer.close()
                }
			}
		}
	}

    void generateFile(String file, Writer writer) {
        Velocity.init();
        VelocityContext context = new VelocityContext();

        if (project.extensions.codegenjava.onGenerate) {
            Map entries = project.extensions.codegenjava.onGenerate()
            entries.each { k,v ->
                context.put(k, v)
            }
        }

        Template template = Velocity.getTemplate(new File("src/tpl", file).getPath())
        template.merge( context, writer );
    }
}