package pe.edu.upeu.PharmaBackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CATEGORIA")
public class Categoria {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 100, unique = true)
	private String nombre;
	@Column(length = 250)
	private String descripcion;
	@Column(nullable = false)
	private Boolean estado = true;
	@OneToMany(mappedBy = "categoria")
	private List<Producto> productos = new ArrayList<>();
}
