package com.group3.learningcommonsproject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Context smoke tests must not depend on a developer's configured MySQL server.
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:context-smoke;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class LearningCommonsProjectApplicationTests {

    @Test
    void contextLoads() {
    }

}
