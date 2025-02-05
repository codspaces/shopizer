package com.salesmanager.test.shop.integration.tax;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.salesmanager.shop.application.ShopApplication;
import com.salesmanager.shop.model.entity.Entity;
import com.salesmanager.shop.model.entity.EntityExists;
import com.salesmanager.shop.model.tax.PersistableTaxClass;
import com.salesmanager.shop.model.tax.PersistableTaxRate;
import com.salesmanager.shop.model.tax.TaxRateDescription;
import com.salesmanager.test.shop.common.ServicesTestSupport;

@SpringBootTest(classes = ShopApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class TaxRateIntegrationTest extends ServicesTestSupport {
	
	
    @Test
    public void manageTaxClass() throws Exception {
    	
    	//create tax class
    	PersistableTaxClass taxClass = new PersistableTaxClass();
    	taxClass.setCode("TESTTX");
    	taxClass.setName("Test tax class");
    	
        final HttpEntity<PersistableTaxClass> taxClassEntity = new HttpEntity<>(taxClass, getHeader());
        final ResponseEntity<Entity> response = testRestTemplate.postForEntity("/api/v1/private/tax/class/".formatted(), taxClassEntity, Entity.class);
        
        Entity e = response.getBody();
        
        assertNotNull(e.getId());
        assertTrue(e.getId() > 0);
        
        
        final HttpEntity<String> httpEntity = new HttpEntity<>(getHeader());
        
        //tax class exists
        final ResponseEntity<EntityExists> exists = testRestTemplate.exchange(("/api/v1/private/tax/class/unique?code=" + taxClass.getCode()).formatted(), HttpMethod.GET,
                httpEntity, EntityExists.class);

        assertTrue(exists.getBody().isExists());


        /**
        //list 1 taxClass
        @SuppressWarnings("rawtypes")
		final ResponseEntity<ReadableEntityList> listOfTaxClasses = testRestTemplate.exchange(String.format("/private/tax/class"), HttpMethod.GET,
                httpEntity, ReadableEntityList.class);
        
        assertTrue(listOfTaxClasses.getBody().getRecordsTotal() == 1);
        **/
	
    }
    
    @Test
    public void manageTaxRates() throws Exception {
    	
    	//create tax class
    	PersistableTaxRate taxRate = new PersistableTaxRate();
    	taxRate.setCode("taxcode1");
    	taxRate.setCountry("US");
    	taxRate.setPriority(0);
    	taxRate.setRate(new BigDecimal(5));
    	taxRate.setStore("DEFAULT");
    	taxRate.setTaxClass("DEFAULT");
    	taxRate.setZone("NY");
    	
    	//descriptions
    	TaxRateDescription en = new TaxRateDescription();
    	en.setLanguage("en");
    	en.setName("TaxCode1EN");
    	en.setDescription("TaxCode1EN description");
    	
    	TaxRateDescription fr = new TaxRateDescription();
    	fr.setLanguage("fr");
    	fr.setName("TaxCode1FR");
    	fr.setDescription("TaxCode1fr description");
    	
    	taxRate.getDescriptions().add(en);
    	taxRate.getDescriptions().add(fr);

    	
        final HttpEntity<PersistableTaxRate> taxClassEntity = new HttpEntity<>(taxRate, getHeader());
        final ResponseEntity<Entity> response = testRestTemplate.postForEntity("/api/v1/private/tax/rate/".formatted(), taxClassEntity, Entity.class);
        
        Entity e = response.getBody();
        
        assertNotNull(e.getId());
        assertTrue(e.getId() > 0);
        
        
        final HttpEntity<String> httpEntity = new HttpEntity<>(getHeader());
        
        //tax class exists
        final ResponseEntity<EntityExists> exists = testRestTemplate.exchange(("/api/v1/private/tax/rate/unique?code=" + taxRate.getCode()).formatted(), HttpMethod.GET,
                httpEntity, EntityExists.class);

        assertTrue(exists.getBody().isExists());


	
    }

}
