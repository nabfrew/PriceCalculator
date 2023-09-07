# Price calculator microservice.

To get the price:
GET [base-url]/calculate?customerId=[customerId]&startDate=[yyyy/mm/dd]&endDate=[yyyy/mm/dd]

## WIP:
The actual price calculation seems pretty solid, based on my testing.
I was going to try some new database, cassandra, but decided it wasn't a great choice for this use, and reverted to what I know a bit better, postgres.

### todo
- Make the liquibase migration work. - something amiss with the pom file atm.
- Check that the schema makes sense.
- Make model classes line up with schema.
- Make bindings to model classes work.
- Implement dao.
- Test db framework?
- Implement controller using dao.
- logging, observability
- security

### Future work (That I will probably never do)
- stick in a container, deploy and all that.
- In the spirit of microservices split up db and calculation into separate services?

# Template generated stuff below. Some is probably bloat that I should remove.

## Micronaut 4.1.0 Documentation

- [User Guide](https://docs.micronaut.io/4.1.0/guide/index.html)
- [API Reference](https://docs.micronaut.io/4.1.0/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/4.1.0/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)

---

- [Micronaut Maven Plugin documentation](https://micronaut-projects.github.io/micronaut-maven-plugin/latest/)

## Feature serialization-jackson documentation

- [Micronaut Serialization Jackson Core documentation](https://micronaut-projects.github.io/micronaut-serialization/latest/guide/)

## Feature openapi documentation

- [Micronaut OpenAPI Support documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/index.html)

- [https://www.openapis.org](https://www.openapis.org)

## Feature micronaut-aot documentation

- [Micronaut AOT documentation](https://micronaut-projects.github.io/micronaut-aot/latest/guide/)

## Feature maven-enforcer-plugin documentation

- [https://maven.apache.org/enforcer/maven-enforcer-plugin/](https://maven.apache.org/enforcer/maven-enforcer-plugin/)

## Feature test-resources documentation

- [Micronaut Test Resources documentation](https://micronaut-projects.github.io/micronaut-test-resources/latest/guide/)

## Feature management documentation

- [Micronaut Management documentation](https://docs.micronaut.io/latest/guide/index.html#management)

## Feature security documentation

- [Micronaut Security documentation](https://micronaut-projects.github.io/micronaut-security/latest/guide/index.html)


