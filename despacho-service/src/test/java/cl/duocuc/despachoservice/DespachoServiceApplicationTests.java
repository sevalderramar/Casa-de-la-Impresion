package cl.duocuc.despachoservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DespachoServiceApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void flywayCreaTablasEsperadas() {
        Integer flywaySchemaHistory = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME = 'flyway_schema_history'",
                Integer.class
        );

        Integer despachos = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'DESPACHOS'",
                Integer.class
        );

        assertTrue(flywaySchemaHistory != null && flywaySchemaHistory > 0);
        assertTrue(despachos != null && despachos > 0);
    }

}
