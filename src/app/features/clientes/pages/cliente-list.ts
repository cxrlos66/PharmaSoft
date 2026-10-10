import { Component,inject,OnInit,signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ReactiveFormsModule,NonNullableFormBuilder,Validators } from '@angular/forms';
import { Cliente,ClienteDatos } from '../models/cliente.model';
import { ClienteService } from '../services/cliente-service';
import { mensajeError } from '../../../core/utils/http-error';
@Component({selector:'app-cliente-list',imports:[ReactiveFormsModule],template:`
<section class="page"><header class="page-header"><h1>Clientes</h1></header>
 @if(mensaje()){<div class="exito">{{ mensaje() }}</div>}
 @if(error()){<div class="alerta" role="alert">{{ error() }}</div>}
 <div class="dos-columnas">
  <form class="card" [formGroup]="formulario" (ngSubmit)="guardar()"><h2>{{ editando()===null?'Nuevo cliente':'Modificar cliente' }}</h2>
   <label>DNI <input class="input" formControlName="dni" maxlength="8" required></label>
   <label>Nombres <input class="input" formControlName="nombres" required></label>
   <label>Apellidos <input class="input" formControlName="apellidos" required></label>
   <label>Correo <input class="input" type="email" formControlName="email"></label>
   <label>Teléfono <input class="input" formControlName="telefono"></label>
   <label>Dirección <input class="input" formControlName="direccion"></label>
   <label class="check"><input type="checkbox" formControlName="estado"> Activo</label>
   <div class="acciones"><button class="btn" type="button" (click)="limpiar()">Limpiar</button><button class="btn btn-primary" [disabled]="guardando()">Guardar</button></div>
  </form>
  <div class="card"><h2>Listado de clientes</h2>
   <div class="tabla-contenedor"><table class="tabla"><thead><tr><th>DNI</th><th>Cliente</th><th>Estado</th><th>Acciones</th></tr></thead>
    <tbody>@for(cliente of clientes();track cliente.id){<tr><td>{{ cliente.dni }}</td><td>{{ cliente.apellidos }}, {{ cliente.nombres }}</td>
     <td>{{ cliente.estado?'Activo':'Inactivo' }}</td><td class="acciones-fila"><button class="btn" (click)="editar(cliente)">Editar</button>
      <button class="btn btn-peligro" (click)="eliminar(cliente)">Eliminar</button></td></tr>}
     @empty{<tr><td colspan="4">Sin clientes.</td></tr>}</tbody></table></div>
  </div>
 </div></section>`,styles:[`.card h2{margin-top:0}.card label{display:flex;flex-direction:column;gap:6px;margin:11px 0}.card .check{flex-direction:row;align-items:center}.acciones{display:flex;gap:9px;justify-content:flex-end;margin-top:15px}.acciones-fila{display:flex;gap:6px;flex-wrap:wrap}`]})
export class ClienteList implements OnInit {
 private readonly fb=inject(NonNullableFormBuilder);
 private readonly servicio=inject(ClienteService);
 protected readonly clientes=signal<Cliente[]>([]);
 protected readonly error=signal<string|null>(null);
 protected readonly mensaje=signal<string|null>(null);
 protected readonly guardando=signal(false);
 protected readonly editando=signal<number|null>(null);
 protected readonly formulario=this.fb.group({
   dni:['',[Validators.required,Validators.pattern(/^\d{8}$/)]],nombres:['',Validators.required],apellidos:['',Validators.required],
   email:['',Validators.email],telefono:[''],direccion:[''],estado:[true]
 });
 ngOnInit():void {this.cargar();}
 private cargar():void {this.servicio.listar().subscribe({next:pagina=>this.clientes.set(pagina.contenido),error:(error:HttpErrorResponse)=>this.error.set(mensajeError(error))});}
 editar(cliente:Cliente):void {this.editando.set(cliente.id);this.formulario.setValue({dni:cliente.dni,nombres:cliente.nombres,apellidos:cliente.apellidos,email:cliente.email??'',telefono:cliente.telefono??'',direccion:cliente.direccion??'',estado:cliente.estado});this.mensaje.set(null);}
 limpiar():void {this.editando.set(null);this.formulario.reset({dni:'',nombres:'',apellidos:'',email:'',telefono:'',direccion:'',estado:true});}
 guardar():void {
  if(this.formulario.invalid){this.formulario.markAllAsTouched();this.error.set('Revisa el DNI, nombres, apellidos y correo.');return;}
  const datos:ClienteDatos=this.formulario.getRawValue();const id=this.editando();
  this.guardando.set(true);this.error.set(null);this.mensaje.set(null);
  const solicitud=id===null?this.servicio.registrar(datos):this.servicio.actualizar(id,datos);
  solicitud.subscribe({next:()=>{this.guardando.set(false);this.mensaje.set('Cliente guardado.');this.limpiar();this.cargar();},error:(error:HttpErrorResponse)=>{this.error.set(mensajeError(error));this.guardando.set(false);}});
 }
 eliminar(cliente:Cliente):void {
  if(!window.confirm(`¿Eliminar a ${cliente.apellidos}, ${cliente.nombres}?`))return;
  this.servicio.eliminar(cliente.id).subscribe({next:()=>{this.mensaje.set('Cliente eliminado.');this.cargar();},error:(error:HttpErrorResponse)=>this.error.set(mensajeError(error))});
 }
}
