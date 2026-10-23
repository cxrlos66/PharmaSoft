package pe.edu.upeu.PharmaBackend.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "CLIENTE")
public class Cliente {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true, length = 8) private String dni;
 @Column(nullable = false, length = 100) private String nombres;
 @Column(nullable = false, length = 100) private String apellidos;
 @Column(length = 150) private String email;
 @Column(length = 20) private String telefono;
 @Column(length = 250) private String direccion;
 @Column(nullable = false) private Boolean estado = true;
 @OneToMany(mappedBy = "cliente") private List<Venta> ventas = new ArrayList<>();
}
