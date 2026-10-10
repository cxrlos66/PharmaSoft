import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ProductoMasVendido, VentaPorCategoria } from '../models/reporte.model';

interface CategoriaRespuesta {
  categoriaId: number;
  categoriaNombre: string;
  cantidadVendida?: number;
  unidadesVendidas?: number;
  montoTotal: number;
}

@Injectable({ providedIn: 'root' })
export class ReporteService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/reportes`;

  private parametros(desde: string | null, hasta: string | null): HttpParams {
    let parametros = new HttpParams();
    if (desde) parametros = parametros.set('desde', desde);
    if (hasta) parametros = parametros.set('hasta', hasta);
    return parametros;
  }

  ventasPorCategoria(desde: string | null, hasta: string | null): Observable<VentaPorCategoria[]> {
    return this.http.get<CategoriaRespuesta[]>(`${this.url}/ventas-por-categoria`, {
      params: this.parametros(desde, hasta),
    }).pipe(
      map(datos => datos.map(dato => {
        const unidades = dato.unidadesVendidas ?? dato.cantidadVendida ?? 0;
        return {
          categoriaId: dato.categoriaId,
          categoriaNombre: dato.categoriaNombre,
          cantidadVendida: unidades,
          unidadesVendidas: unidades,
          montoTotal: dato.montoTotal,
        };
      }))
    );
  }

  // Se conserva el método usado por el reporte anterior.
  categorias(desde: string, hasta: string): Observable<VentaPorCategoria[]> {
    return this.ventasPorCategoria(desde || null, hasta || null);
  }

  productos(desde: string, hasta: string): Observable<ProductoMasVendido[]> {
    return this.http.get<ProductoMasVendido[]>(`${this.url}/productos-mas-vendidos`, {
      params: this.parametros(desde, hasta),
    });
  }
}
