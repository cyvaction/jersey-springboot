package com.sapbas.server.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;

import com.sapbas.server.model.Employee;
import com.sapbas.server.privileges.Privilege;
import com.sapbas.server.repository.EmployeeRepository;
import com.sapbas.server.security.Secured;

@Path("/employees")
public class EmployeeResource {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Secured(Privilege.EMPLOYEE_SEARCH)
    public List<Employee> searchAllEmployees(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName) {
        List<Employee> searchResult = employeeRepository.searchEmployees(firstName, lastName);
        return searchResult;
    }

    @GET
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Secured(Privilege.EMPLOYEE_READ)
    public Employee getEmployee(@PathParam("id") int id) {
        return employeeRepository.getEmployee(id);
    }

    @PUT
    @Path("/{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Secured(Privilege.EMPLOYEE_UPDATE)
    public Response updateEmployee(Employee employee, @PathParam("id") int id) {
        try {
            employeeRepository.updateEmployee(employee, id);
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        }
        return Response.status(Response.Status.OK.getStatusCode()).build();
    }

    @DELETE
    @Path("/{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Secured(Privilege.EMPLOYEE_DELETE)
    public Response deleteEmployee(@PathParam("id") int id) {
        try {
            employeeRepository.deleteEmployee(id);
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        }
        return Response.status(Response.Status.OK.getStatusCode()).build();
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Secured(Privilege.EMPLOYEE_CREATE)
    public Response addEmployee(Employee employee, @Context UriInfo uriInfo) {
        try {
            employeeRepository.addEmployee(new Employee(employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getAge()));
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT.getStatusCode()).build();
        }
        return Response.status(Response.Status.CREATED.getStatusCode()).header("Location", String.format("%s/%s", uriInfo.getAbsolutePath().toString(), employee.getId())).build();
    }
}
