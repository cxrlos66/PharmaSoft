
import { Component, computed, effect, inject, signal } from '@angular/core';
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

        @if (avisoExpiracion()) {
          <div class="aviso-expiracion" role="alert">
            <div class="aviso-icono">⚠</div>

            <div class="aviso-contenido">
              <strong>Tu sesión está por expirar</strong>
              <p>
                Quedan menos de 5 minutos para el cierre automático
                de tu sesión. Guarda tu trabajo para evitar perder
                información no registrada.
              </p>
            </div>
          </div>
        }

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

    .aviso-expiracion {
      display: flex;
      align-items: flex-start;
      gap: 14px;
      padding: 16px 20px;
      margin-bottom: 18px;
      background: #fff4d6;
      border: 1px solid #f0d58a;
      border-left: 5px solid #d18a12;
      border-radius: 10px;
      color: #704b08;
    }

    .aviso-icono {
      font-size: 26px;
      line-height: 1;
    }

    .aviso-contenido {
      flex: 1;
    }

    .aviso-contenido strong {
      display: block;
      font-size: 15px;
      margin-bottom: 5px;
    }

    .aviso-contenido p {
      margin: 0;
      font-size: 13px;
      line-height: 1.6;
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

      .aviso-expiracion {
        padding: 14px;
      }
    }

    @media print {
      .shell {
        display: block;
        min-height: 0;
      }

      .area-header,
      .area-sidebar,
      .aviso-expiracion {
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

  protected readonly avisoExpiracion = signal(false);

  protected readonly opciones = computed(() =>
    menu.filter(opcion =>
      !opcion.permiso || this.auth.tienePermiso(opcion.permiso)
    )
  );

  constructor() {
    effect(onCleanup => {
      const sesion = this.auth.sesion();

      this.avisoExpiracion.set(false);

      if (!sesion) {
        return;
      }

      const anticipacion = 5 * 60 * 1000;
      const tiempoHastaAviso = sesion.expiraEn - Date.now() - anticipacion;

      if (tiempoHastaAviso <= 0) {
        this.avisoExpiracion.set(true);
        return;
      }

      const temporizador = setTimeout(() => {
        this.avisoExpiracion.set(true);
      }, tiempoHastaAviso);

      onCleanup(() => {
        clearTimeout(temporizador);
      });
    });
  }

  salir(): void {
    this.auth.cerrarSesion();
  }
}
