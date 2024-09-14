package com.testing.api.utils;

import com.github.javafaker.Faker;
import com.testing.api.models.Client;
import com.testing.api.models.Resource;

/**
 * The type Randoms.
 */
public class Randoms {
    private static final Faker faker = new Faker();

    /**
     * Create random client.
     *
     * @return the client
     */
    public static Client createRandomClient() {
        return Client.builder()
                .name(faker.name().firstName())
                .lastName(faker.name().lastName())
                .country(faker.address().country())
                .city(faker.address().city())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .id(faker.idNumber().valid())
                .build();
    }

    /**
     * Create random resource.
     *
     * @return the resource
     */
    public static Resource createRandomResource() {
        return Resource.builder()
                .name(faker.commerce().productName())
                .trademark(faker.company().name())
                .stock(faker.number().numberBetween(1, 100000))
                .price((float) faker.number().randomDouble(2, 10, 1000))
                .description(faker.lorem().sentence())
                .tags(faker.lorem().words(3).toString())
                .active(true)
                .id(faker.idNumber().valid())
                .build();
    }
}
