package triplog.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.batch.job.enabled=false",
        "spring.batch.job.name=regionSyncJob",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=none",
        "mission.scheduling.enabled=false"
})
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }
}
