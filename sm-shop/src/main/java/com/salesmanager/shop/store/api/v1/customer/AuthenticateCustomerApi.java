package com.salesmanager.shop.store.api.v1.customer;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.Validate;
import org.apache.http.auth.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.store.api.exception.GenericRuntimeException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.UnauthorizedException;
import com.salesmanager.shop.store.controller.customer.facade.CustomerFacade;
import com.salesmanager.shop.store.controller.store.facade.StoreFacade;
import com.salesmanager.shop.store.controller.user.facade.UserFacade;
import com.salesmanager.shop.store.security.AuthenticationRequest;
import com.salesmanager.shop.store.security.AuthenticationResponse;
import com.salesmanager.shop.store.security.JWTTokenUtil;
import com.salesmanager.shop.store.security.PasswordRequest;
import com.salesmanager.shop.store.security.user.JWTUser;
import com.salesmanager.shop.utils.AuthorizationUtils;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/v1")
@Tag(tags = {"Customer authentication resource (Customer Authentication Api)"})
@SwaggerDefinition(tags = {
    @Tag(name = "Customer authentication resource", description = "Authenticates customer, register customer and reset customer password")
})
public class AuthenticateCustomerApi {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticateCustomerApi.class);

    @Value("${authToken.header}")
    private String tokenHeader;

    @Inject
    private AuthenticationManager jwtCustomerAuthenticationManager;

    @Inject
    private JWTTokenUtil jwtTokenUtil;

    @Inject
    private UserDetailsService jwtCustomerDetailsService;
    
    @Inject
    private CustomerFacade customerFacade;
    
    @Inject
    private StoreFacade storeFacade;

    @Autowired
    AuthorizationUtils authorizationUtils;
    
    @Autowired
    private UserFacade userFacade;

    /**
     * Create new customer for a given MerchantStore, then authenticate that customer
     */
    @PostMapping( value={"/customer/register"}, produces ={ "application/json" })
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(httpMethod = "POST", summary = "Registers a customer to the application", description = "Used as self-served operation")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
		@Parameter(name = "lang", defaultValue = "en") })
    @ResponseBody
    public ResponseEntity<?> register(
    		@Valid @RequestBody PersistableCustomer customer, 
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) throws Exception {


            customer.setUserName(customer.getEmailAddress());
            
			if(customerFacade.checkIfUserExists(customer.getUserName(),  merchantStore)) {
				//409 Conflict
				throw new GenericRuntimeException("409", "Customer with email [" + customer.getEmailAddress() + "] is already registered");
			}
            
            Validate.notNull(customer.getUserName(),"Username cannot be null");
            Validate.notNull(customer.getBilling(),"Requires customer Country code");
            Validate.notNull(customer.getBilling().getCountry(),"Requires customer Country code");
            
            customerFacade.registerCustomer(customer, merchantStore, language);
            
            // Perform the security
            Authentication authentication = null;
            try {
                
                authentication = jwtCustomerAuthenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                customer.getUserName(),
                                customer.getPassword()
                        )
                );
                
            } catch(Exception e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            if(authentication == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Reload password post-security so we can generate token
            final JWTUser userDetails = (JWTUser)jwtCustomerDetailsService.loadUserByUsername(customer.getUserName());
            final String token = jwtTokenUtil.generateToken(userDetails);

            // Return the token
            return ResponseEntity.ok(new AuthenticationResponse(customer.getId(),token));

        
    }

    /**
     * Authenticate a customer using username & password
     * @param authenticationRequest
     * @param device
     * @return
     * @throws AuthenticationException
     */
    @PostMapping(value = "/customer/login", produces ={ "application/json" })
    @Operation(httpMethod = "POST", summary = "Authenticates a customer to the application", description = "Customer can authenticate after registration, request is {\"username\":\"admin\",\"password\":\"password\"}")
    @ResponseBody
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest) throws AuthenticationException {

    	//TODO SET STORE in flow
        // Perform the security
        Authentication authentication = null;
        try {
            
    
                //to be used when username and password are set
                authentication = jwtCustomerAuthenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authenticationRequest.getUsername(),
                                authenticationRequest.getPassword()
                        )
                );

        } catch(BadCredentialsException unn) {
        	return new ResponseEntity<>("{\"message\":\"Bad credentials\"}",HttpStatus.UNAUTHORIZED);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        if(authentication == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-security so we can generate token
        // todo create one for social
        final JWTUser userDetails = (JWTUser)jwtCustomerDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        
        final String token = jwtTokenUtil.generateToken(userDetails);

        // Return the token
        return ResponseEntity.ok(new AuthenticationResponse(userDetails.getId(),token));
    }

    @GetMapping(value = "/auth/customer/refresh", produces ={ "application/json" })
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);

        String username = jwtTokenUtil.getUsernameFromToken(token);
        JWTUser user = (JWTUser) jwtCustomerDetailsService.loadUserByUsername(username);

        if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
            String refreshedToken = jwtTokenUtil.refreshToken(token);
            return ResponseEntity.ok(new AuthenticationResponse(user.getId(),refreshedToken));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    

    @PostMapping(value = "/auth/customer/password", produces ={ "application/json" })
    @Operation(httpMethod = "POST", summary = "Sends a request to reset password", description = "Password reset request is {\"username\":\"test@email.com\"}")
    public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordRequest passwordRequest, HttpServletRequest request) {


        try {
            
            MerchantStore merchantStore = storeFacade.getByCode(request);

            Customer customer = customerFacade.getCustomerByUserName(passwordRequest.getUsername(), merchantStore);
            
            if(customer == null){
                return ResponseEntity.notFound().build();
            }
            
            //need to validate if password matches
            if(!customerFacade.passwordMatch(passwordRequest.getCurrent(), customer)) {
              throw new ResourceNotFoundException("Username or password does not match");
            }
            
            if(!passwordRequest.getPassword().equals(passwordRequest.getRepeatPassword())) {
              throw new ResourceNotFoundException("Both passwords do not match");
            }
            
            customerFacade.changePassword(customer, passwordRequest.getPassword());           
            return ResponseEntity.ok(Void.class);
            
        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Exception when reseting password "+e.getMessage());
        }
    }
}
