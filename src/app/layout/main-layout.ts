
import { Component, computed, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { menu } from '../core/config/menu';
import { AuthService } from '../core/auth/auth-service';

@Component({
  selector: 'app-main-layout',
  imports: [RouterLink, RouterLinkActive, RouterOutlet, DatePipe],
  template: `
    <div class="shell">
      <header class="area-header">

        <div class="cabecera-marca">
          <strong>PharmaSoft</strong>
          <span class="descripcion">Botica · Inventario y ventas</span>
        </div>

        @if (auth.sesion(); as sesion) {
          <div class="cabecera-sesion">
            <div class="usuario">
              <span class="nombre">{{ sesion.nombreCompleto }}</span>
              <small>
                {{ sesion.roles.join(' · ') }}
                · Expira {{ sesion.expiraEn | date:'HH:mm' }}
              </small>
            </div>

            <button type="button" class="btn-salir" (click)="salir()">
              Cerrar sesión
            </button>
          </div>
        }

      </header>

      <aside class="area-sidebar">
        <nav aria-label="Navegación principal">
          @for (opcion of opciones(); track opcion.ruta) {
            <a
              [routerLink]="opcion.ruta"
              routerLinkActive="activo"
            >
              {{ opcion.icono }} {{ opcion.etiqueta }}
            </a>
          }
        </nav>
      </aside>

      <main class="area-main">
        <router-outlet />
      </main>
    </div>
  `,
  styles: [`
    .shell {
      grid-template-rows: minmax(68px, auto) 1fr;
    }

    .area-header {
      gap: 20px;
      padding-top: 10px;
      padding-bottom: 10px;
    }

    .cabecera-marca {
      display: flex;
      align-items: center;
      gap: 22px;
      min-width: 0;
    }

    .descripcion {
      font-size: 13px;
      opacity: 0.85;
    }

    .cabecera-sesion {
      margin-left: auto;
      display: flex;
      align-items: center;
      gap: 22px;
    }

    .usuario {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      gap: 4px;
      white-space: nowrap;
    }

    .nombre {
      font-weight: 600;
      font-size: 14px;
    }

    .usuario small {
      font-size: 12px;
      color: #cadcfc;
    }

    .btn-salir {
      padding: 8px 14px;
      border: 1px solid #cadcfc;
      border-radius: 8px;
      background: transparent;
      color: white;
      white-space: nowrap;
      cursor: pointer;
    }

    .btn-salir:hover {
      background: rgba(255,255,255,0.12);
    }

    @media (max-width: 700px) {
      .shell {
        grid-template-rows: auto auto 1fr;
      }

      .area-header {
        flex-wrap: wrap;
        justify-content: center;
      }

      .cabecera-marca {
        width: 100%;
        justify-content: center;
        flex-wrap: wrap;
        gap: 6px 12px;
      }

      .cabecera-sesion {
        margin-left: 0;
        width: 100%;
        justify-content: center;
        gap: 12px;
      }
    }

    @media print {
      .shell {
        display: block;
        min-height: 0;
      }

      .area-header,
      .area-sidebar {
        display: none !important;
      }

      .area-main {
        padding: 0;
        background: #fff;
      }
    }
  `]
})
export class MainLayout {
  protected readonly auth = inject(AuthService);

  protected readonly opciones = computed(() =>
    menu.filter(opcion =>
      !opcion.permiso || this.auth.tienePermiso(opcion.permiso)
    )
  );

  salir(): void {
    this.auth.cerrarSesion();
  }
}
