package com.salesmanager.shop.store.api.v1.category;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.catalog.category.PersistableCategory;
import com.salesmanager.shop.model.catalog.category.ReadableCategory;
import com.salesmanager.shop.model.catalog.category.ReadableCategoryList;
import com.salesmanager.shop.model.entity.EntityExists;
import com.salesmanager.shop.model.entity.ListCriteria;
import com.salesmanager.shop.store.api.exception.UnauthorizedException;
import com.salesmanager.shop.store.controller.category.facade.CategoryFacade;
import com.salesmanager.shop.store.controller.user.facade.UserFacade;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/api/v1")
@Tag(tags = { "Category management resource (Category Management Api)" })
@SwaggerDefinition(tags = {
		@Tag(name = "Category management resource", description = "Manage category and attached products") })
public class CategoryApi {

	private static final int DEFAULT_CATEGORY_DEPTH = 0;

	@Inject
	private CategoryFacade categoryFacade;

	@Inject
	private UserFacade userFacade;

	@GetMapping(value = "/private/category/{id}", produces = { APPLICATION_JSON_VALUE })
	@Operation(httpMethod = "GET", summary = "Get category list for an given Category id", description = "List current Category and child category")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List of category found", response = ReadableCategory.class) })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public ReadableCategory get(
			@PathVariable(name = "id") Long categoryId, 
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		ReadableCategory category = categoryFacade.getById(merchantStore, categoryId, language);
		return category;
	}

	@GetMapping(value = "/category/{friendlyUrl}", produces = { APPLICATION_JSON_VALUE })
	@Operation(httpMethod = "GET", summary = "Get category list for an given Category code", description = "List current Category and child category")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List of category found", response = ReadableCategory.class) })
	@Parameters({
			@Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en")
	})
	public ReadableCategory getByfriendlyUrl(
								@PathVariable String friendlyUrl,
								@ApiIgnore MerchantStore merchantStore,
								@ApiIgnore Language language) throws Exception {
		ReadableCategory category = categoryFacade.getCategoryByFriendlyUrl(merchantStore, friendlyUrl, language);
		return category;
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = { "/private/category/unique" }, produces = MediaType.APPLICATION_JSON_VALUE)
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	@Operation(httpMethod = "GET", summary = "Check if category code already exists", description = "")
	public ResponseEntity<EntityExists> exists(@RequestParam String code,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
		boolean isCategoryExist = categoryFacade.existByCode(merchantStore, code);
		return new ResponseEntity<EntityExists>(new EntityExists(isCategoryExist), HttpStatus.OK);
	}

	/**
	 * Get all category starting from root filter can be used for filtering on
	 * fields only featured is supported
	 *
	 * @return
	 */
	@GetMapping(value = "/category", produces = { APPLICATION_JSON_VALUE })
	@Operation(httpMethod = "GET", summary = "Get category hierarchy from root. Supports filtering FEATURED_CATEGORIES and VISIBLE ONLY by adding ?filter=[featured] or ?filter=[visible] or ? filter=[featured,visible", description = "Does not return any product attached")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public ReadableCategoryList list(
			@RequestParam(required = false) List<String> filter,
			@RequestParam(required = false) String name,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language,
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer count) {


		ListCriteria criteria = new ListCriteria();
		criteria.setName(name);
		return categoryFacade.getCategoryHierarchy(merchantStore, criteria, DEFAULT_CATEGORY_DEPTH, language, filter,
				page, count);
	}
	
	
	@GetMapping(value = "/category/product/{ProductId}", produces = { APPLICATION_JSON_VALUE })
	@Operation(httpMethod = "GET", summary = "Get category by product", description = "")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public ReadableCategoryList list(
			@PathVariable(name = "ProductId") Long id,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language lang) {


		return categoryFacade.listByProduct(merchantStore, id, lang);

	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/private/category", produces = { APPLICATION_JSON_VALUE })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public PersistableCategory create(
			@Valid @RequestBody PersistableCategory category,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		// superadmin, admin and admin_catalogue
		String authenticatedUser = userFacade.authenticatedUser();
		if (authenticatedUser == null) {
			throw new UnauthorizedException();
		}

		userFacade.authorizedGroup(authenticatedUser, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN, Constants.GROUP_ADMIN_CATALOGUE, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()));

		return categoryFacade.saveCategory(merchantStore, category);
	}

	@PutMapping(value = "/private/category/{id}", produces = { APPLICATION_JSON_VALUE })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	public PersistableCategory update(@PathVariable Long id, @Valid @RequestBody PersistableCategory category,
			@ApiIgnore MerchantStore merchantStore) {

		// superadmin, admin and admin_catalogue
		String authenticatedUser = userFacade.authenticatedUser();
		if (authenticatedUser == null) {
			throw new UnauthorizedException();
		}

		userFacade.authorizedGroup(authenticatedUser, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN, Constants.GROUP_ADMIN_CATALOGUE, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()));


		category.setId(id);
		return categoryFacade.saveCategory(merchantStore, category);
	}

	@PatchMapping(value = "/private/category/{id}/visible", produces = { APPLICATION_JSON_VALUE })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	public void updateVisible(@PathVariable Long id, @Valid @RequestBody PersistableCategory category,
			@ApiIgnore MerchantStore merchantStore
			) {

		// superadmin, admin and admin_catalogue
		String authenticatedUser = userFacade.authenticatedUser();
		if (authenticatedUser == null) {
			throw new UnauthorizedException();
		}

		userFacade.authorizedGroup(authenticatedUser, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN, Constants.GROUP_ADMIN_CATALOGUE, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()));

		category.setId(id);
		categoryFacade.setVisible(category, merchantStore);
	}

	@PutMapping(value = "/private/category/{id}/move/{parent}", produces = { APPLICATION_JSON_VALUE })
	@Operation(httpMethod = "PUT", summary = "Move a category under another category", description = "Move category {id} under category {parent}")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	public void move(
			@PathVariable Long id,
			@PathVariable Long parent,
			@ApiIgnore MerchantStore merchantStore) {
		// superadmin, admin and admin_catalogue
		String authenticatedUser = userFacade.authenticatedUser();
		if (authenticatedUser == null) {
			throw new UnauthorizedException();
		}

		userFacade.authorizedGroup(authenticatedUser, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN, Constants.GROUP_ADMIN_CATALOGUE, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()));


		categoryFacade.move(id, parent, merchantStore);
		return;
	}

	@DeleteMapping(value = "/private/category/{id}", produces = { APPLICATION_JSON_VALUE })
	@ResponseStatus(OK)
	public void delete(@PathVariable("id") Long categoryId, @ApiIgnore MerchantStore merchantStore) {

		// superadmin, admin and admin_catalogue
		String authenticatedUser = userFacade.authenticatedUser();
		if (authenticatedUser == null) {
			throw new UnauthorizedException();
		}

		userFacade.authorizedGroup(authenticatedUser, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN, Constants.GROUP_ADMIN_CATALOGUE, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()));


		categoryFacade.deleteCategory(categoryId, merchantStore);
	}

}
