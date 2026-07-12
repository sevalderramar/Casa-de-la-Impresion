package cl.duocuc.fabricacion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class FabricacionServiceApplicationTest {

    @Test
    void mainDelegaArranqueASpringApplication() {
        // Given
        String[] args = {"--spring.profiles.active=test"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            // When
            FabricacionServiceApplication.main(args);

            // Then
            springApplication.verify(() -> SpringApplication.run(FabricacionServiceApplication.class, args));
        }
    }
}
