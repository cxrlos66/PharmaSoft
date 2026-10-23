
import { Routes } from '@angular/router';
import { permisoGuard } from '../../core/guards/permiso-guard';

export const rutasVentas: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/venta-list/venta-list').then(m => m.VentaList),
    title: 'Consulta de ventas'
  },
  {
    path: 'nueva',
    loadComponent: () =>
      import('./pages/venta-form/venta-form').then(m => m.VentaForm),
    title: 'Nueva venta',
    canActivate: [permisoGuard],
    data: {
      permiso: 'VENTAS_REGISTRAR'
    }
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./pages/venta-detalle/venta-detalle').then(m => m.VentaDetalle),
    title: 'Comprobante de venta'
  }
];
