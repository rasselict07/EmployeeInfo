package employeeinfo

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }

//        "/"(view: "/index")
        "/"(controller: "employee")
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
