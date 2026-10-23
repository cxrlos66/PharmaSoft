
import { Component, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { AuthService } from '../../core/auth/auth-service';

@Component({
  selector: 'app-mi-cuenta',
  imports: [DatePipe],
  template: `
    <section class="page">
      <header class="page-header">
        <h1>Mi cuenta</h1>
      </header>

      @if (auth.sesion(); as sesion) {
        <div class="cuenta-contenedor">

          <div class="card perfil">
            <div class="avatar">👤</div>

            <div class="perfil-datos">
              <h2>{{ sesion.nombreCompleto }}</h2>
              <p>{{ sesion.username }}</p>
              <span class="estado">Sesión activa</span>
            </div>
          </div>

          <div class="cuenta-grid">

            <div class="card">
              <h2>Información personal</h2>

              <div class="dato">
                <span class="etiqueta">Nombre completo</span>
                <strong>{{ sesion.nombreCompleto }}</strong>
              </div>

              <div class="dato">
                <span class="etiqueta">Nombre de usuario</span>
                <strong>{{ sesion.username }}</strong>
              </div>
            </div>

            <div class="card">
              <h2>Información de sesión</h2>

              <div class="dato">
                <span class="etiqueta">Estado</span>
                <strong>Autenticado</strong>
              </div>

              <div class="dato">
                <span class="etiqueta">Fecha y hora de expiración</span>
                <strong>
                  {{ sesion.expiraEn | date:'dd/MM/yyyy HH:mm:ss' }}
                </strong>
              </div>
            </div>

            <div class="card">
              <h2>Roles asignados</h2>

              <div class="etiquetas">
                @for (rol of sesion.roles; track rol) {
                  <span class="etiqueta-rol">
                    {{ rol }}
                  </span>
                } @empty {
                  <p>No tiene roles asignados.</p>
                }
              </div>
            </div>

            <div class="card">
              <h2>Permisos asignados</h2>

              <div class="lista-permisos">
                @for (permiso of sesion.permisos; track permiso) {
                  <div class="permiso">
                    <span class="marca">✓</span>
                    <span>{{ permiso }}</span>
                  </div>
                } @empty {
                  <p>No tiene permisos asignados.</p>
                }
              </div>
            </div>

          </div>

          <p class="nota">
            Esta información corresponde a la sesión autenticada.
            Los roles y permisos son administrados por PharmaBackend.
          </p>

        </div>
      }
    </section>
  `,
  styles: [`
    .cuenta-contenedor {
      max-width: 1050px;
      display: flex;
      flex-direction: column;
      gap: 18px;
    }

    .perfil {
      display: flex;
      align-items: center;
      gap: 20px;
      padding: 24px;
    }

    .avatar {
      width: 72px;
      height: 72px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      background: #e8f0fd;
      font-size: 35px;
    }

    .perfil-datos h2 {
      margin: 0 0 6px;
      color: #173e73;
      font-size: 22px;
    }

    .perfil-datos p {
      margin: 0 0 12px;
      color: #64748b;
    }

    .estado {
      display: inline-block;
      padding: 5px 12px;
      border-radius: 20px;
      background: #dcfce7;
      color: #166534;
      font-size: 12px;
      font-weight: 600;
    }

    .cuenta-grid {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 18px;
    }

    .cuenta-grid .card {
      padding: 24px;
    }

    .cuenta-grid h2 {
      margin: 0 0 20px;
      color: #173e73;
      font-size: 19px;
    }

    .dato {
      display: flex;
      flex-direction: column;
      gap: 6px;
      padding: 12px 0;
      border-bottom: 1px solid #e2e8f0;
    }

    .dato:last-child {
      border-bottom: 0;
    }

    .etiqueta {
      color: #64748b;
      font-size: 13px;
    }

    .dato strong {
      color: #1e293b;
      font-size: 15px;
      overflow-wrap: anywhere;
    }

    .etiquetas {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
    }

    .etiqueta-rol {
      padding: 8px 14px;
      border-radius: 8px;
      background: #e8f0fd;
      color: #173e73;
      font-size: 13px;
      font-weight: 600;
    }

    .lista-permisos {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }

    .permiso {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px 12px;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      color: #334155;
      font-size: 13px;
      overflow-wrap: anywhere;
    }

    .marca {
      color: #15803d;
      font-weight: 700;
    }

    .nota {
      margin: 0;
      color: #64748b;
      font-size: 12px;
      line-height: 1.6;
    }

    @media (max-width: 700px) {
      .cuenta-grid {
        grid-template-columns: 1fr;
      }

      .perfil {
        flex-direction: column;
        text-align: center;
      }
    }

    @media print {
      .nota {
        display: none;
      }
    }
  `]
})
export class MiCuenta {
  protected readonly auth = inject(AuthService);
}
