import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { catchError, map, of, switchMap } from 'rxjs';
import { mensajeError } from '../../../../core/utils/http-error';
import { Cliente } from '../../../clientes/models/cliente.model';
import { ClienteService } from '../../../clientes/services/cliente-service';
import { Venta } from '../../models/venta.model';
import { VentaService } from '../../services/venta-service';
@Component({selector:'app-venta-detalle',imports:[RouterLink,CurrencyPipe,DatePipe],templateUrl:'./venta-detalle.html',styleUrl:'./venta-detalle.css'})
export class VentaDetalle implements OnInit {
 private readonly ventaService=inject(VentaService);
 private readonly clienteService=inject(ClienteService);
 readonly id=input.required<string>();
 readonly nueva=input<string>();
 readonly imprimir=input<string>();
 protected readonly venta=signal<Venta|null>(null);
 protected readonly cliente=signal<Cliente|null>(null);
 protected readonly error=signal<string|null>(null);
 protected readonly anulando=signal(false);
 protected readonly fechaImpresion=signal(new Date());
 protected readonly numero=computed(()=>String(this.venta()?.id??'').padStart(6,'0'));
 protected readonly unidades=computed(()=>(this.venta()?.detalles??[]).reduce((suma,detalle)=>suma+detalle.cantidad,0));
 ngOnInit():void {
  this.ventaService.obtener(Number(this.id())).pipe(
   switchMap(venta=>this.clienteService.obtener(venta.clienteId).pipe(
    catchError(()=>of(null)),map(cliente=>({venta,cliente}))
   ))
  ).subscribe({
   next:({venta,cliente})=>{
    this.venta.set(venta);this.cliente.set(cliente);
    if(this.imprimir()==='1')setTimeout(()=>window.print(),300);
   },
   error:(error:HttpErrorResponse)=>this.error.set(mensajeError(error))
  });
 }
 imprimirComprobante():void {this.fechaImpresion.set(new Date());setTimeout(()=>window.print(),50);}
 anular():void {
  const venta=this.venta();
  if(!venta||venta.estado==='ANULADA'||this.anulando())return;
  if(!window.confirm(`¿Anular la venta N.º ${venta.id} y devolver sus productos al inventario?`))return;
  this.anulando.set(true);this.error.set(null);
  this.ventaService.anular(venta.id).subscribe({
   next:actualizada=>{this.venta.set(actualizada);this.anulando.set(false);},
   error:(error:HttpErrorResponse)=>{this.error.set(mensajeError(error));this.anulando.set(false);}
  });
 }
}
