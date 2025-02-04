package com.salesmanager.shop.store.api.v1.shipping;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.shipping.ShippingService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shipping.PackageDetails;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.references.PersistableAddress;
import com.salesmanager.shop.model.references.ReadableAddress;
import com.salesmanager.shop.model.system.IntegrationModuleConfiguration;
import com.salesmanager.shop.model.system.IntegrationModuleSummaryEntity;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.shipping.facade.ShippingFacade;
import com.salesmanager.shop.utils.AuthorizationUtils;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/v1")
@io.swagger.annotations.Tag(name = "Shipping configuration resource (Shipping Management Api)")
@SwaggerDefinition(tags = {
        @io.swagger.annotations.Tag(name = "Shipping management resource", description = "Manage shipping configuration") })
public class ShippingConfigurationApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShippingConfigurationApi.class);

	@Autowired
	private AuthorizationUtils authorizationUtils;

	@Autowired
	private ShippingFacade shippingFacade;

	@Autowired
	private ShippingService shippingService;

@Operation(method = "GET", summary = "Get shipping origin for a specific merchant store", description = "")
	@GetMapping({ "/private/shipping/origin" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ReadableAddress shippingOrigin(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {

		String user = authorizationUtils.authenticatedUser();
		authorizationUtils.authorizeUser(user, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_SHIPPING, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()), merchantStore);

		return shippingFacade.getShippingOrigin(merchantStore);

	}

	@PostMapping({ "/private/shipping/origin" })
	@ResponseStatus(HttpStatus.OK)
	public void saveShippingOrigin(@RequestBody PersistableAddress address, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		String user = authorizationUtils.authenticatedUser();
		authorizationUtils.authorizeUser(user, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_SHIPPING, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()), merchantStore);

		shippingFacade.saveShippingOrigin(address, merchantStore);

	}

	// list packaging
@Operation(method = "GET", summary = "Get list of configured packages types for a specific merchant store", description = "")
	@GetMapping({ "/private/shipping/packages" })
	@ResponseStatus(HttpStatus.OK)
	public List<PackageDetails> listPackages(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {

		String user = authorizationUtils.authenticatedUser();
		authorizationUtils.authorizeUser(user, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_SHIPPING, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()), merchantStore);

		return shippingFacade.listPackages(merchantStore);

	}

	// get packaging
@Operation(method = "GET", summary = "Get package details", description = "")
	@GetMapping({ "/private/shipping/package/{code}" })
	@ResponseStatus(HttpStatus.OK)
	public PackageDetails getPackage(@PathVariable String code, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		String user = authorizationUtils.authenticatedUser();
		authorizationUtils.authorizeUser(user, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_SHIPPING, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()), merchantStore);

		return shippingFacade.getPackage(code, merchantStore);

	}

	// create packaging
@Operation(method = "POST", summary = "Create new package specification", description = "")
	@PostMapping({ "/private/shipping/package" })
	@ResponseStatus(HttpStatus.OK)
	public void createPackage(@RequestBody PackageDetails details, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		String user = authorizationUtils.authenticatedUser();
		authorizationUtils.authorizeUser(user, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_SHIPPING, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()), merchantStore);

		shippingFacade.createPackage(details, merchantStore);

	}

	// edit packaging
@Operation(method = "PUT", summary = "Edit package specification", description = "")
	@PutMapping({ "/private/shipping/package/{code}" })
	@ResponseStatus(HttpStatus.OK)
	public void updatePackage(@PathVariable String code, @RequestBody PackageDetails details,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {

		String user = authorizationUtils.authenticatedUser();
		authorizationUtils.authorizeUser(user, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_SHIPPING, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()), merchantStore);

		shippingFacade.updatePackage(code, details, merchantStore);

	}

	// delete packaging
@Operation(method = "DELETE", summary = "Delete a package specification", description = "")
	@DeleteMapping({ "/private/shipping/package/{code}" })
	@ResponseStatus(HttpStatus.OK)
	public void deletePackage(@PathVariable String code, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		String user = authorizationUtils.authenticatedUser();
		authorizationUtils.authorizeUser(user, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_SHIPPING, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()), merchantStore);

		shippingFacade.deletePackage(code, merchantStore);

	}

	/**
	 * Get available shipping modules
	 * 
	 * @param merchantStore
	 * @param language
	 * @return
	 */
	@GetMapping("/private/modules/shipping")
@Operation(method = "GET", summary = "List list of shipping modules", description = "Requires administration access")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	public List<IntegrationModuleSummaryEntity> shippingModules(@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		try {
			List<IntegrationModule> modules = shippingService.getShippingMethods(merchantStore);

			// configured modules
			Map<String, IntegrationConfiguration> configuredModules = shippingService
					.getShippingModulesConfigured(merchantStore);
			return modules.stream().map(m -> integrationModule(m, configuredModules)).collect(Collectors.toList());

		} catch (ServiceException e) {
			LOGGER.error("Error getting shipping modules", e);
			throw new ServiceRuntimeException("Error getting shipping modules", e);
		}

	}

	/**
	 * Get merchant shipping module details
	 * 
	 * @param code
	 * @param merchantStore
	 * @param language
	 * @return
	 */
	@GetMapping("/private/modules/shipping/{code}")
@Operation(method = "GET", summary = "Shipping module by code")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	public IntegrationConfiguration shippingModule(@PathVariable String code,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {

		try {

			// configured modules
			List<IntegrationModule> modules = 
					shippingService
					.getShippingMethods(merchantStore);
			
			//check if exist
			Optional<IntegrationModule> checkIfExist = modules.stream().filter(m -> m.getCode().equals(code)).findAny();
			
			if(checkIfExist.isEmpty()) {
				throw new ResourceNotFoundException("Shipping module [" + code + "] not found");
			}
			
			IntegrationConfiguration config = shippingService.getShippingConfiguration(code, merchantStore);
			if (config == null) {
				config = new IntegrationConfiguration();
			}

			/**
			 * Build return object for now this is a read copy
			 */

			config.setActive(config.isActive());
			config.setDefaultSelected(config.isDefaultSelected());
			config.setIntegrationKeys(config.getIntegrationKeys());
			config.setIntegrationOptions(config.getIntegrationOptions());

			return config;

		} catch (ServiceException e) {
			LOGGER.error("Error getting shipping module [" + code + "]", e);
			throw new ServiceRuntimeException("Error getting shipping module [" + code + "]", e);
		}

	}

	@PostMapping(value = "/private/modules/shipping")
	public void configure(@RequestBody IntegrationModuleConfiguration configuration,
			@ApiIgnore MerchantStore merchantStore) {

		try {

			List<IntegrationModule> modules = shippingService.getShippingMethods(merchantStore);

			Map<String, IntegrationModule> map = modules.stream()
					.collect(Collectors.toMap(IntegrationModule::getCode, module -> module));

			IntegrationModule config = map.get(configuration.getCode());

			if (config == null) {
				throw new ResourceNotFoundException("Shipping module [" + configuration.getCode() + "] not found");
			}

			Map<String, IntegrationConfiguration> configuredModules = shippingService
					.getShippingModulesConfigured(merchantStore);

			IntegrationConfiguration integrationConfiguration = configuredModules.get(configuration.getCode());

			if (integrationConfiguration == null) {
				integrationConfiguration = new IntegrationConfiguration();
			}

			/**
			 * Build return object for now this is a read copy
			 */

			integrationConfiguration.setActive(configuration.isActive());
			integrationConfiguration.setDefaultSelected(configuration.isDefaultSelected());
			integrationConfiguration.setIntegrationKeys(configuration.getIntegrationKeys());
			integrationConfiguration.setIntegrationOptions(configuration.getIntegrationOptions());

			shippingService.saveShippingQuoteModuleConfiguration(integrationConfiguration, merchantStore);

		} catch (ServiceException e) {
			LOGGER.error("Error saving shipping modules", e);
			throw new ServiceRuntimeException("Error saving shipping module", e);
		}

	}

	private IntegrationModuleSummaryEntity integrationModule(IntegrationModule module,
			Map<String, IntegrationConfiguration> configuredModules) {

		IntegrationModuleSummaryEntity readable = null;
		readable = new IntegrationModuleSummaryEntity();

		readable.setCode(module.getCode());
		readable.setImage(module.getImage());
		if (configuredModules.containsKey(module.getCode())) {
			IntegrationConfiguration conf = configuredModules.get(module.getCode());
			readable.setConfigured(true);
			if(conf.isActive()) {
				readable.setActive(true);
			}
		}
		return readable;

	}

	// Module configuration
	/**
	 * private String moduleCode; private boolean active; private boolean
	 * defaultSelected; private Map<String, String> integrationKeys = new
	 * HashMap<String, String>(); private Map<String, List<String>>
	 * integrationOptions = new HashMap<String, List<String>>(); private String
	 * environment;
	 * 
	 * moduleCode:CODE, active:true, defaultSelected:false, environment: "TEST",
	 * integrationKeys { "key":"value", "anotherkey":"anothervalue"... }
	 */

}
