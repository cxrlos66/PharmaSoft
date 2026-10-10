import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';
import { mensajeError } from '../../../../core/utils/http-error';
import { VentaPorCategoria } from '../../models/reporte.model';
import { ReporteService } from '../../services/reporte-service';

@Component({
  selector: 'app-ventas-categoria',
  imports: [ReactiveFormsModule, CurrencyPipe],
  templateUrl: './ventas-categoria.html',
  styleUrl: './ventas-categoria.css',
})
export class VentasCategoria implements OnInit {
  private readonly constructorFormulario = inject(NonNullableFormBuilder);
  private readonly servicioReportes = inject(ReporteService);

  protected readonly filtros = this.constructorFormulario.group({
    desde: '',
    hasta: '',
  });
  protected readonly categorias = signal<VentaPorCategoria[]>([]);
  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);

  protected readonly totalUnidades = computed(() =>
    this.categorias().reduce((suma, categoria) => suma + categoria.cantidadVendida, 0));

  protected readonly totalMonto = computed(() =>
    this.categorias().reduce((suma, categoria) => suma + categoria.montoTotal, 0));

  protected readonly mayorMonto = computed(() =>
    Math.max(0, ...this.categorias().map(categoria => categoria.montoTotal)));

  ngOnInit(): void {
    this.generar();
  }

  generar(): void {
    const { desde, hasta } = this.filtros.getRawValue();
    if (desde && hasta && desde > hasta) {
      this.error.set('La fecha «desde» no puede ser posterior a «hasta».');
      return;
    }

    this.error.set(null);
    this.cargando.set(true);
    this.servicioReportes.ventasPorCategoria(desde || null, hasta || null).subscribe({
      next: datos => {
        this.categorias.set(datos);
        this.cargando.set(false);
      },
      error: (fallo: HttpErrorResponse) => {
        this.error.set(mensajeError(fallo));
        this.cargando.set(false);
      },
    });
  }

  porcentaje(monto: number): number {
    const mayor = this.mayorMonto();
    return mayor > 0 ? (monto / mayor) * 100 : 0;
  }
}
