package pe.edu.upeu.PharmaBackend.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "PRODUCTO")
public class Producto {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true, length = 25) private String codigo;
 @Column(nullable = false, length = 150) private String nombre;
 @Column(length = 250) private String descripcion;
 @Column(nullable = false, precision = 10, scale = 2) private BigDecimal precio;
 @Column(nullable = false) private Integer stock;
 @Column(nullable = false) private Boolean estado = true;
 @ManyToOne(fetch = FetchType.LAZY, optional = false)
 @JoinColumn(name = "categoria_id", nullable = false) private Categoria categoria;
 @OneToMany(mappedBy = "producto") private List<DetalleVenta> detalles = new ArrayList<>();
}
