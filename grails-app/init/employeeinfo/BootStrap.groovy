package employeeinfo

import com.employeeinfo.utility.Tools
import grails.core.GrailsApplication

class BootStrap {
    GrailsApplication grailsApplication

    def init = { servletContext ->
        try {

            // Get the file directory from configuration file
            String fileDir = grailsApplication.config.getProperty('app.filedir')

            // Create a File object representing the folder
            def folder = new File(fileDir)

            // If it doesn't exist
            if (!folder.exists()) {
                // Create all folders up-to
                folder.mkdirs()
            }

            // Get the file name configuration file
            String fileName = grailsApplication.config.getProperty('app.filename')
            def file = new File(fileDir + Tools.SLASH + fileName)

            if (!file.exists()) {
                // Then, write to file inside file directory
                new File(folder, fileName).withWriterAppend { writer ->
                    writer << "[]"
                }
            }
            log.info(fileName + " created successfully")
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex.getMessage())
        }
    }
    def destroy = {
    }
}
