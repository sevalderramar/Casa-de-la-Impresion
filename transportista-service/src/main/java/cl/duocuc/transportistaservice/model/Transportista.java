package cl.duocuc.transportistaservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transportistas")
@Getter
@Setter
@Schema(description = "Entidad persistente de transportista")
public class Transportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del transportista", example = "1")
    private Long id;

    @NotBlank
    @Schema(description = "Nombre comercial del transportista", example = "Blue Express")
    private String nombre;

    @NotBlank
    @Column(unique = true)
    @Schema(description = "Código interno único del transportista", example = "TR-001")
    private String codigoInterno;

    @Schema(description = "Datos de contacto", example = "contacto@blue.cl")
    private String contacto;

    @Schema(description = "Regiones cubiertas", example = "RM,V")
    private String regionesCobertura;

    @Schema(description = "Indica si el transportista está activo", example = "true")
    private boolean activo = true;
}
