package pe.edu.upeu.PharmaBackend.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "DETALLE_VENTA")
public class DetalleVenta {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
 @JoinColumn(name = "venta_id", nullable = false) private Venta venta;
 @ManyToOne(fetch = FetchType.LAZY, optional = false)
 @JoinColumn(name = "producto_id", nullable = false) private Producto producto;
 @Column(nullable = false) private Integer cantidad;
 @Column(nullable = false, precision = 10, scale = 2) private BigDecimal precio;
 @Column(nullable = false, precision = 12, scale = 2) private BigDecimal subtotal;
}
