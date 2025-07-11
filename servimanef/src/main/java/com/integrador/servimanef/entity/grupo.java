package com.integrador.servimanef.entity;

import java.util.List;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "informe_id", nullable = false)
    private informe informe;

    @Column(nullable = false)
    private String nombreGrupo;

    @Column(nullable = false)
    private Integer cantidadImagenes;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @OneToMany(mappedBy = "grupo")
    private List<imagen> imagenes;

    public List<imagen> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<imagen> imagenes) {
        this.imagenes = imagenes;
    }
}
