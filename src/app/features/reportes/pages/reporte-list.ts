import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { mensajeError } from '../../../core/utils/http-error';
import { ProductoMasVendido, VentaPorCategoria } from '../models/reporte.model';
import { ReporteService } from '../services/reporte-service';
@Component({selector:'app-reporte-list',imports:[ReactiveFormsModule,CurrencyPipe],template:`
<section class="page">
 <header class="page-header"><h1>Reportes de ventas</h1><button class="btn no-imprimir" (click)="imprimir()">🖨 Imprimir reportes</button></header>
 <form class="card filtros no-imprimir" [formGroup]="filtros" (ngSubmit)="consultar()">
  <label>Desde <input class="input" type="date" formControlName="desde"></label>
  <label>Hasta <input class="input" type="date" formControlName="hasta"></label>
  <button class="btn btn-primary" type="submit">Consultar</button>
 </form>
 @if(error()){<div class="alerta" role="alert">{{ error() }}</div>}
 @if(cargando()){<p>Consultando reportes…</p>}
 @else {
  <section class="card"><h2>Ventas por categoría</h2>
   <div class="tabla-contenedor"><table class="tabla"><thead><tr><th>Categoría</th><th>Unidades</th><th>Monto</th></tr></thead>
    <tbody>@for(dato of categorias();track dato.categoriaId){<tr><td>{{ dato.categoriaNombre }}</td><td>{{ dato.unidadesVendidas }}</td><td>{{ dato.montoTotal | currency:'PEN':'S/ ' }}</td></tr>}
    @empty {<tr><td colspan="3">Sin ventas registradas en el período.</td></tr>}</tbody>
    <tfoot><tr><td><strong>Total registrado</strong></td><td></td><td><strong>{{ totalCategorias() | currency:'PEN':'S/ ' }}</strong></td></tr></tfoot>
   </table></div>
  </section>
  <section class="card"><h2>Productos más vendidos</h2>
   <div class="tabla-contenedor"><table class="tabla"><thead><tr><th>Producto</th><th>Unidades</th><th>Monto</th></tr></thead>
    <tbody>@for(dato of productos();track dato.productoId){<tr><td>{{ dato.productoNombre }}</td><td>{{ dato.unidadesVendidas }}</td><td>{{ dato.montoTotal | currency:'PEN':'S/ ' }}</td></tr>}
    @empty {<tr><td colspan="3">Sin productos vendidos en el período.</td></tr>}</tbody>
   </table></div>
  </section>
 }
</section>`,styles:[`.filtros{display:flex;gap:15px;align-items:end;flex-wrap:wrap;margin-bottom:16px}.filtros label{display:flex;flex-direction:column;gap:5px}.card+.card{margin-top:17px}.card h2{margin-top:0}@media print{.filtros{display:none}}`]})
export class ReporteList implements OnInit {
 private readonly fb=inject(NonNullableFormBuilder);
 private readonly servicio=inject(ReporteService);
 protected readonly filtros=this.fb.group({desde:'',hasta:''});
 protected readonly categorias=signal<VentaPorCategoria[]>([]);
 protected readonly productos=signal<ProductoMasVendido[]>([]);
 protected readonly error=signal<string|null>(null);
 protected readonly cargando=signal(false);
 protected readonly totalCategorias=computed(()=>this.categorias().reduce((suma,dato)=>suma+dato.montoTotal,0));
 ngOnInit():void {this.consultar();}
 consultar():void {
  const {desde,hasta}=this.filtros.getRawValue();
  if(desde&&hasta&&desde>hasta){this.error.set('La fecha «desde» no puede superar la fecha «hasta».');return;}
  this.error.set(null);this.cargando.set(true);
  forkJoin({categorias:this.servicio.categorias(desde,hasta),productos:this.servicio.productos(desde,hasta)}).subscribe({
   next:datos=>{this.categorias.set(datos.categorias);this.productos.set(datos.productos);this.cargando.set(false);},
   error:(error:HttpErrorResponse)=>{this.error.set(mensajeError(error));this.cargando.set(false);}
  });
 }
 imprimir():void {window.print();}
}
