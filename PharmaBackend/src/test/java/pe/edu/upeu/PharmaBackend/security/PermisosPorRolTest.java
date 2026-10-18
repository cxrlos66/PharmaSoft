package pe.edu.upeu.PharmaBackend.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import pe.edu.upeu.PharmaBackend.entity.Rol;
import pe.edu.upeu.PharmaBackend.enums.Permiso;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PermisosPorRolTest {

    private final Rol admin =
            new Rol(1L, "ADMIN", null);

    private final Rol vendedor =
            new Rol(2L, "VENDEDOR", null);

    private final Rol almacenero =
            new Rol(3L, "ALMACENERO", null);

    private List<String> permisos(Rol... roles) {

        return PermisosPorRol.authorities(List.of(roles))
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    @Test
    void administradorTieneTodosLosPermisos() {

        assertThat(PermisosPorRol.de("ADMIN"))
                .containsExactlyInAnyOrder(Permiso.values());

        assertThat(permisos(admin))
                .contains(
                        "ROLE_ADMIN",
                        "REPORTES_VER",
                        "USUARIOS_ADMINISTRAR"
                );
    }

    @Test
    void vendedorTienePermisosLimitados() {

        assertThat(permisos(vendedor))
                .contains(
                        "ROLE_VENDEDOR",
                        "VENTAS_REGISTRAR",
                        "CATALOGO_LEER"
                )
                .doesNotContain(
                        "REPORTES_VER",
                        "CATALOGO_ESCRIBIR",
                        "USUARIOS_ADMINISTRAR"
                );
    }

    @Test
    void supervisorCombinaPermisos() {

        assertThat(permisos(vendedor, almacenero))
                .contains(
                        "ROLE_VENDEDOR",
                        "ROLE_ALMACENERO",
                        "CATALOGO_ESCRIBIR",
                        "VENTAS_REGISTRAR"
                )
                .doesNotContain("REPORTES_VER")
                .doesNotHaveDuplicates();
    }

    @Test
    void rolDesconocidoNoTienePermisos() {

        Rol desconocido = new Rol(9L, "AUDITOR", null);

        assertThat(permisos(desconocido))
                .containsExactly("ROLE_AUDITOR");
    }
}
