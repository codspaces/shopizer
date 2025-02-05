package com.salesmanager.shop.store.api.v1.product;

import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.salesmanager.core.business.services.catalog.product.manufacturer.ManufacturerService;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.manufacturer.PersistableManufacturer;
import com.salesmanager.shop.model.catalog.manufacturer.ReadableManufacturer;
import com.salesmanager.shop.model.catalog.manufacturer.ReadableManufacturerList;
import com.salesmanager.shop.model.entity.EntityExists;
import com.salesmanager.shop.model.entity.ListCriteria;
import com.salesmanager.shop.store.controller.manufacturer.facade.ManufacturerFacade;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import springfox.documentation.annotations.ApiIgnore;

/**
 * Manufacturer management Collection, Manufacturer ...
 *
 * @author c.samson
 */
@Controller
@RequestMapping("/api/v1")
@Tag(tags = { "Manufacturer / Brand management resource (Manufacturer / Brand Management Api)" })
@SwaggerDefinition(tags = {
		@Tag(name = "Manufacturer / Brand Management Api", description = "Edit Manufacturer / Brand") })
public class ProductManufacturerApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductManufacturerApi.class);

	@Inject
	private ManufacturerService manufacturerService;

	@Inject
	private ManufacturerFacade manufacturerFacade;

	/**
	 * Method for creating a manufacturer
	 *
	 * @param manufacturer
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/private/manufacturer")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public PersistableManufacturer create(@Valid @RequestBody PersistableManufacturer manufacturer,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language, HttpServletResponse response) {

		try {
			manufacturerFacade.saveOrUpdateManufacturer(manufacturer, merchantStore, language);

			return manufacturer;

		} catch (Exception e) {
			LOGGER.error("Error while creating manufacturer", e);
			try {
				response.sendError(503, "Error while creating manufacturer " + e.getMessage());
			} catch (Exception ignore) {
			}

			return null;
		}
	}

	@GetMapping("/manufacturer/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public ReadableManufacturer get(@PathVariable Long id, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language, HttpServletResponse response) {

		try {
			ReadableManufacturer manufacturer = manufacturerFacade.getManufacturer(id, merchantStore, language);

			if (manufacturer == null) {
				response.sendError(404, "No Manufacturer found for ID : " + id);
			}

			return manufacturer;

		} catch (Exception e) {
			LOGGER.error("Error while getting manufacturer", e);
			try {
				response.sendError(503, "Error while getting manufacturer " + e.getMessage());
			} catch (Exception ignore) {
			}
		}

		return null;
	}

	
	@GetMapping("/private/manufacturers")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	@Operation(httpMethod = "GET", summary = "List manufacturers by store", description = "This request supports paging or not. Paging supports page number and request count")
	public ReadableManufacturerList listByStore(
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language,
			@RequestParam(required = false) String name,
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer count) {

		ListCriteria listCriteria = new ListCriteria();
		listCriteria.setName(name);
		return manufacturerFacade.listByStore(merchantStore, language, listCriteria, page, count);
	}
	
	
	@GetMapping("/manufacturers")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	@Operation(httpMethod = "GET", summary = "List manufacturers by store", description = "This request supports paging or not. Paging supports page number and request count")
	public ReadableManufacturerList list(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language,
			@RequestParam(required = false) String name,
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer count) {

		ListCriteria listCriteria = new ListCriteria();
		listCriteria.setName(name);
		return manufacturerFacade.getAllManufacturers(merchantStore, language, listCriteria, page, count);
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = { "/private/manufacturer/unique" }, produces = MediaType.APPLICATION_JSON_VALUE)
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	@Operation(httpMethod = "GET", summary = "Check if manufacturer code already exists", description = "")
	public ResponseEntity<EntityExists> exists(@RequestParam String code,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {

		boolean exists = manufacturerFacade.manufacturerExist(merchantStore, code);
		return new ResponseEntity<EntityExists>(new EntityExists(exists), HttpStatus.OK);

	}

	@PutMapping("/private/manufacturer/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public void update(@PathVariable Long id,
			@Valid @RequestBody PersistableManufacturer manufacturer, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language, HttpServletRequest request, HttpServletResponse response) {

		try {
			manufacturer.setId(id);
			manufacturerFacade.saveOrUpdateManufacturer(manufacturer, merchantStore, language);
		} catch (Exception e) {
			LOGGER.error("Error while creating manufacturer", e);
			try {
				response.sendError(503, "Error while creating manufacturer " + e.getMessage());
			} catch (Exception ignore) {
			}
		}
	}

	@DeleteMapping("/private/manufacturer/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public void delete(@PathVariable Long id, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language,
			HttpServletResponse response) {

		try {
			Manufacturer manufacturer = manufacturerService.getById(id);

			if (manufacturer != null) {
				manufacturerFacade.deleteManufacturer(manufacturer, merchantStore, language);
			} else {
				response.sendError(404, "No Manufacturer found for ID : " + id);
			}

		} catch (Exception e) {
			LOGGER.error("Error while deleting manufacturer id " + id, e);
			try {
				response.sendError(503, "Error while deleting manufacturer id " + id + " - " + e.getMessage());
			} catch (Exception ignore) {
			}
		}
	}

	@GetMapping("/category/{id}/manufacturer")
	@ResponseStatus(HttpStatus.OK)
	@Operation(httpMethod = "GET", summary = "Get all manufacturers for all items in a given category", description = "")
	@ResponseBody
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public List<ReadableManufacturer> list(@PathVariable final Long id, // category
																					// id
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language, HttpServletResponse response)
			throws Exception {

		return manufacturerFacade.getByProductInCategory(merchantStore, language, id);

	}

}
