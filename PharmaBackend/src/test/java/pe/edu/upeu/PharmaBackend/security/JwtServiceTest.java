package pe.edu.upeu.PharmaBackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import pe.edu.upeu.PharmaBackend.entity.Rol;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String CLAVE =
            "w9Z6dP/dzykNH/uiI3Z/KGjBlP0H4Ji+Tuq4fm2cTNuLD4jWafu1gBL74V2SJEvy";

    private static final String OTRA_CLAVE =
            "q8Bx3mN0vRk7TzYp1LcW5aHs9JdFg2Ue4OiPlKjMnBvCxZaSdFgHjKlQwErTyUiO";

    private final JwtService servicio = new JwtService(CLAVE, 60);

    private UserDetails vendedor() {

        Rol rol = new Rol(2L, "VENDEDOR", null);

        return User.withUsername("vendedor")
                .password("prueba")
                .authorities(PermisosPorRol.authorities(List.of(rol)))
                .build();
    }

    @Test
    void generarYLeerToken() {

        String token = servicio.generarToken(vendedor());

        Claims datos = servicio.leerClaims(token);

        assertThat(token.split("\\.")).hasSize(3);
        assertThat(datos.getSubject()).isEqualTo("vendedor");
        assertThat(datos.getExpiration()).isAfter(datos.getIssuedAt());


		List<String> permisos = datos.get("authorities", List.class).stream().map(String::valueOf).toList();


        assertThat(permisos)
                .contains("ROLE_VENDEDOR", "VENTAS_REGISTRAR")
                .doesNotContain("REPORTES_VER");

        assertThat(servicio.extraerUsername(token))
                .isEqualTo("vendedor");
    }

    @Test
    void tokenAlterado() {

        String token = servicio.generarToken(vendedor());

        String[] partes = token.split("\\.");

        char letra = partes[2].charAt(5);

        char cambio = letra == 'A' ? 'B' : 'A';

        String firma = partes[2].substring(0, 5)
                + cambio
                + partes[2].substring(6);

        String alterado = partes[0] + "."
                + partes[1] + "." + firma;

        assertThatThrownBy(() -> servicio.extraerUsername(alterado))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void tokenDeOtraClave() {

        JwtService otroServicio = new JwtService(OTRA_CLAVE, 60);

        String token = otroServicio.generarToken(vendedor());

        assertThatThrownBy(() -> servicio.extraerUsername(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void tokenExpirado() {

        Instant ahora = Instant.now();

        String token = Jwts.builder()
                .subject("vendedor")
                .issuedAt(Date.from(ahora.minusSeconds(120)))
                .expiration(Date.from(ahora.minusSeconds(60)))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(CLAVE)))
                .compact();

        assertThatThrownBy(() -> servicio.extraerUsername(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void expiracionEnSegundos() {

        assertThat(servicio.getExpiracionSegundos())
                .isEqualTo(3600);
    }
}
