package pe.edu.upeu.PharmaBackend.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "VENTA")
public class Venta {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "fecha_registro", nullable = false, updatable = false) private LocalDateTime fechaRegistro;
 @Column(nullable = false, precision = 12, scale = 2) private BigDecimal total = BigDecimal.ZERO;
 @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
 private pe.edu.upeu.PharmaBackend.enums.EstadoVenta estado;
 @ManyToOne(fetch = FetchType.LAZY, optional = false)
 @JoinColumn(name = "cliente_id", nullable = false) private Cliente cliente;
 @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
 @org.hibernate.annotations.BatchSize(size = 32)
 private List<DetalleVenta> detalles = new ArrayList<>();
 @PrePersist public void alGuardar() {
  if (fechaRegistro == null) fechaRegistro = LocalDateTime.now();
  if (estado == null) estado = pe.edu.upeu.PharmaBackend.enums.EstadoVenta.REGISTRADA;
 }
 public void agregarDetalle(DetalleVenta detalle) {
  detalles.add(detalle);
  detalle.setVenta(this);
 }
}
