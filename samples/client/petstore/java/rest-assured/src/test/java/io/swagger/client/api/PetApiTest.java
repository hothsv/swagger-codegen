/*
 * Swagger Petstore
 * This spec is mainly for testing Petstore server and contains fake endpoints, models. Please do not use this for any other purpose. Special characters: \" \\
 *
 * OpenAPI spec version: 1.0.0
 * Contact: apiteam@swagger.io
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.client.api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.swagger.client.ApiClient;
import io.swagger.client.model.Category;
import io.swagger.client.model.Pet;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.config;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.swagger.client.GsonObjectMapper.gson;
import static io.swagger.client.ResponseSpecBuilders.shouldBeCode;
import static io.swagger.client.ResponseSpecBuilders.validatedWith;
import static io.swagger.client.model.Pet.StatusEnum.AVAILABLE;
import static io.swagger.client.model.Pet.StatusEnum.PENDING;
import static io.swagger.client.model.Pet.StatusEnum.SOLD;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * API tests for PetApi
 */
public class PetApiTest {

    private PetApi api;

    @Before
    public void createApi() {
        api = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> new RequestSpecBuilder().setConfig(config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(gson())))
                        .addFilter(new ErrorLoggingFilter())
                        .setBaseUri("http://petstore.swagger.io:80/v2"))).pet();
    }

    @Test
    public void statusListTest() {
        Pet pet = getPet();
        api.addPet().body(pet.status(PENDING)).execute(validatedWith(shouldBeCode(SC_OK)));
        List<String> statusList = Arrays.asList(PENDING.getValue());
        List<Long> petsId = api.findPetsByStatus()
                .statusQuery(statusList.toArray()).executeAs(validatedWith(shouldBeCode(SC_OK)))
                .stream().map(Pet::getId).collect(Collectors.toList());
        assertThat(petsId, hasItem(pet.getId()));
        api.deletePet().petIdPath(pet.getId()).execute(validatedWith(shouldBeCode(SC_OK)));
    }

    @Test
    public void getPetByIdTest() {
        Pet pet = getPet();
        api.addPet().body(pet.status(PENDING)).execute(validatedWith(shouldBeCode(SC_OK)));
        Pet fetchedPet = api.getPetById()
                .petIdPath(pet.getId()).executeAs(validatedWith(shouldBeCode(SC_OK)));
        assertThat(fetchedPet.getId(), equalTo(pet.getId()));
        api.deletePet().petIdPath(pet.getId()).execute(validatedWith(shouldBeCode(SC_OK)));
    }

    @Test
    public void deletePet() {
        Pet pet = getPet();
        api.addPet().body(pet.status(PENDING)).execute(validatedWith(shouldBeCode(SC_OK)));
        api.deletePet().petIdPath(pet.getId()).execute(validatedWith(shouldBeCode(SC_OK)));
        api.getPetById()
                .petIdPath(pet.getId()).execute(validatedWith(shouldBeCode(SC_NOT_FOUND)));
    }

    @Test
    public void uploadFileTest() throws IOException {
        Pet pet = getPet();
        api.addPet().body(pet).execute(validatedWith(shouldBeCode(SC_OK)));
        File file = new File("hello.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("Hello world!");
        writer.close();
        api.uploadFile().fileMultiPart(file)
                .petIdPath(pet.getId()).execute(validatedWith(shouldBeCode(SC_OK)));
        api.deletePet().petIdPath(pet.getId()).execute(validatedWith(shouldBeCode(SC_OK)));
    }

    @Test
    public void updatePetTest() {
        Pet pet = getPet();
        api.addPet().body(pet).execute(validatedWith(shouldBeCode(SC_OK)));
        api.updatePet().body(pet.status(SOLD)).execute(validatedWith(shouldBeCode(SC_OK)));
        Pet.StatusEnum statusEnum = api.getPetById().petIdPath(pet.getId()).executeAs(validatedWith(shouldBeCode(SC_OK))).getStatus();
        assertThat(statusEnum, equalTo(SOLD));
        api.deletePet().petIdPath(pet.getId()).execute(validatedWith(shouldBeCode(SC_OK)));
    }


    private Pet getPet() {
        return new Pet().id(TestUtils.nextId()).name("alex").status(AVAILABLE)
                .category(new Category().id(TestUtils.nextId()).name("dog"))
                .photoUrls(Arrays.asList("http://foo.bar.com/1"));
    }

}