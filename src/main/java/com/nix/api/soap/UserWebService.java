package com.nix.api.soap;

import com.nix.api.soap.exception.UserValidationException;
import com.nix.model.User;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;

@WebService(name = "userWebService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
public interface UserWebService {

    @WebMethod(operationName = "findAllUsers")
    @WebResult(name = "user")
    List<User> getAllUsers();

    @WebMethod(operationName = "findUserByLogin")
    @WebResult(name = "user")
    User getUserByLogin(@WebParam(name = "login") String login);

    @WebMethod
    void createUser(@WebParam(name = "user") User user) throws UserValidationException;

    @WebMethod
    void updateUser(@WebParam(name = "user") User user) throws UserValidationException;

    @WebMethod
    void deleteUser(@WebParam(name = "login") String login);

}
