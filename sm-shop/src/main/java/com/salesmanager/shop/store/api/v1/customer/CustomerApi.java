package com.salesmanager.shop.store.api.v1.customer;

import java.security.Principal;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.CustomerCriteria;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.ReadableCustomer;
import com.salesmanager.shop.populator.customer.ReadableCustomerList;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.api.exception.UnauthorizedException;
import com.salesmanager.shop.store.controller.customer.facade.CustomerFacade;
import com.salesmanager.shop.store.controller.user.facade.UserFacade;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/api/v1")
@Tag(tags = { "Customer management resource (Customer Management Api)" })
@SwaggerDefinition(tags = { @Tag(name = "Customer management resource", description = "Manage customers") })
public class CustomerApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerApi.class);

	@Inject
	private CustomerFacade customerFacade;
	
    @Autowired
    private UserFacade userFacade;

	/** Create new customer for a given MerchantStore */
	@PostMapping("/private/customer")
	@Operation(httpMethod = "POST", summary = "Creates a customer", description = "Requires administration access")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	public ReadableCustomer create(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language,
			@Valid @RequestBody PersistableCustomer customer) {
		return customerFacade.create(customer, merchantStore, language);

	}

	@PutMapping("/private/customer/{id}")
	@Operation(httpMethod = "PUT", summary = "Updates a customer", description = "Requires administration access")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	public PersistableCustomer update(@PathVariable Long id, @ApiIgnore MerchantStore merchantStore,
			@Valid @RequestBody PersistableCustomer customer) {

		customer.setId(id);
		return customerFacade.update(customer, merchantStore);
	}

	@PatchMapping("/private/customer/{id}/address")
	@Operation(httpMethod = "PATCH", summary = "Updates a customer", description = "Requires administration access")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	public void updateAddress(@PathVariable Long id, @ApiIgnore MerchantStore merchantStore,
			@RequestBody PersistableCustomer customer) {

		customer.setId(id);
		customerFacade.updateAddress(customer, merchantStore);
	}

	@DeleteMapping("/private/customer/{id}")
	@Operation(httpMethod = "DELETE", summary = "Deletes a customer", description = "Requires administration access")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	public void delete(@PathVariable Long id, @ApiIgnore MerchantStore merchantStore) {
		
		String authenticatedUser = userFacade.authenticatedUser();
		if (authenticatedUser == null) {
			throw new UnauthorizedException();
		}

		userFacade.authorizedGroup(authenticatedUser, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()));
    	
		
		customerFacade.deleteById(id);
	}

	/**
	 * Get all customers
	 *
	 * @param start
	 * @param count
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/private/customers")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public ReadableCustomerList list(@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer count, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		CustomerCriteria customerCriteria = createCustomerCriteria(page, count);
		return customerFacade.getListByStore(merchantStore, customerCriteria, language);
	}

	private CustomerCriteria createCustomerCriteria(Integer start, Integer count) {
		CustomerCriteria customerCriteria = new CustomerCriteria();
		Optional.ofNullable(start).ifPresent(customerCriteria::setStartIndex);
		Optional.ofNullable(count).ifPresent(customerCriteria::setMaxCount);
		return customerCriteria;
	}

	@GetMapping("/private/customer/{id}")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public ReadableCustomer get(@PathVariable Long id, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		return customerFacade.getCustomerById(id, merchantStore, language);
	}

	/**
	 * Get logged in customer profile
	 * 
	 * @param merchantStore
	 * @param language
	 * @param request
	 * @return
	 */
	@GetMapping({ "/private/customer/profile", "/auth/customer/profile" })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public ReadableCustomer getAuthUser(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language,
			HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		String userName = principal.getName();
		return customerFacade.getCustomerByNick(userName, merchantStore, language);
	}

	@PatchMapping("/auth/customer/address")
	@Operation(httpMethod = "PATCH", summary = "Updates a loged in customer address", description = "Requires authentication")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	public void updateAuthUserAddress(@ApiIgnore MerchantStore merchantStore, @RequestBody PersistableCustomer customer,
			HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		String userName = principal.getName();

		customerFacade.updateAddress(userName, customer, merchantStore);

	}

	@PatchMapping("/auth/customer/")
	@Operation(httpMethod = "PATCH", summary = "Updates a loged in customer profile", description = "Requires authentication")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	public PersistableCustomer update(@ApiIgnore MerchantStore merchantStore,
			@Valid @RequestBody PersistableCustomer customer, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();
		String userName = principal.getName();

		return customerFacade.update(userName, customer, merchantStore);
	}
	
	@DeleteMapping("/auth/customer/")
	@Operation(httpMethod = "DELETE", summary = "Deletes a loged in customer profile", description = "Requires authentication")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	public void delete(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language,
			HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();
		String userName = principal.getName();
		
		Customer customer;
		try {
			customer = customerFacade.getCustomerByUserName(userName, merchantStore);
			if(customer == null) {
				throw new ResourceNotFoundException("Customer [" + userName + "] not found");
			}
			customerFacade.delete(customer);
		} catch (Exception e) {
			throw new ServiceRuntimeException("An error occured while deleting customer ["+userName+"]");
		}
		

	}

}
