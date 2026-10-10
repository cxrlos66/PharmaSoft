import { Routes } from '@angular/router';

export const rutasReportes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'ventas-por-categoria' },
  {
    path: 'ventas-por-categoria',
    loadComponent: () => import('./pages/ventas-categoria/ventas-categoria').then(m => m.VentasCategoria),
    title: 'Reporte de ventas por categoría',
  },
  {
    path: 'resumen',
    loadComponent: () => import('./pages/reporte-list').then(m => m.ReporteList),
    title: 'Reportes',
  },
];
