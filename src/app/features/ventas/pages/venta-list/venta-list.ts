
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SiPermiso } from '../../../../shared/directives/si-permiso';
import { PaginaResponse } from '../../../../core/models/pagina-response';
import { mensajeError } from '../../../../core/utils/http-error';
import { Cliente } from '../../../clientes/models/cliente.model';
import { ClienteService } from '../../../clientes/services/cliente-service';
import { EstadoVenta, FiltroVentas, Venta } from '../../models/venta.model';
import { VentaService } from '../../services/venta-service';

@Component({
  selector: 'app-venta-list',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    CurrencyPipe,
    DatePipe,
    SiPermiso
  ],
  templateUrl: './venta-list.html',
  styleUrl: './venta-list.css'
})
export class VentaList implements OnInit {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly ventaService = inject(VentaService);
  private readonly clienteService = inject(ClienteService);

  protected readonly clientes = signal<Cliente[]>([]);
  protected readonly resultado = signal<PaginaResponse<Venta> | null>(null);
  protected readonly pagina = signal(0);
  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly ahora = signal(new Date());

  protected readonly filtros = this.fb.group({
    clienteId: this.fb.control<number | null>(null),
    estado: this.fb.control<EstadoVenta | null>(null),
    desde: this.fb.control<string | null>(null),
    hasta: this.fb.control<string | null>(null)
  });

  protected readonly totalPagina = computed(() =>
    (this.resultado()?.contenido ?? [])
      .filter(venta => venta.estado === 'REGISTRADA')
      .reduce((suma, venta) => suma + venta.total, 0)
  );

  protected resumenFiltros(): string {
    const datos = this.filtros.getRawValue();
    const cliente = this.clientes().find(c => c.id === datos.clienteId);

    const partes = [
      cliente ? `cliente ${cliente.apellidos}, ${cliente.nombres}` : '',
      datos.estado ? `estado ${datos.estado}` : '',
      datos.desde ? `desde ${datos.desde}` : '',
      datos.hasta ? `hasta ${datos.hasta}` : ''
    ].filter(Boolean);

    return partes.length
      ? partes.join(' · ')
      : 'ninguno (todas las ventas)';
  }

  imprimirConsulta(): void {
    this.ahora.set(new Date());
    setTimeout(() => window.print(), 50);
  }

  ngOnInit(): void {
    this.clienteService.listar(0, 100).subscribe({
      next: pagina => this.clientes.set(pagina.contenido),
      error: (error: HttpErrorResponse) =>
        this.error.set(mensajeError(error))
    });

    this.cargar();
  }

  buscar(): void {
    const { desde, hasta } = this.filtros.getRawValue();

    if (desde && hasta && desde > hasta) {
      this.error.set(
        'La fecha «desde» no puede ser posterior a «hasta».'
      );
      return;
    }

    this.pagina.set(0);
    this.cargar();
  }

  limpiar(): void {
    this.filtros.reset();
    this.buscar();
  }

  irA(pagina: number): void {
    const total = this.resultado()?.totalPaginas ?? 0;

    if (pagina < 0 || (total > 0 && pagina >= total)) {
      return;
    }

    this.pagina.set(pagina);
    this.cargar();
  }

  private cargar(): void {
    const filtro: FiltroVentas = this.filtros.getRawValue();

    this.cargando.set(true);
    this.error.set(null);

    this.ventaService.buscar(filtro, this.pagina(), 10).subscribe({
      next: resultado => {
        this.resultado.set(resultado);
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.error.set(mensajeError(error));
        this.cargando.set(false);
      }
    });
  }
}
