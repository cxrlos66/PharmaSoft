
import { Routes } from '@angular/router';
import { MainLayout } from './layout/main-layout';
import { authGuard, invitadoGuard } from './core/guards/auth-guard';
import { permisoGuard } from './core/guards/permiso-guard';

export const rutas: Routes = [
  {
    path: 'login',
    title: 'Iniciar sesión',
    canActivate: [invitadoGuard],
    loadComponent: () =>
      import('./features/auth/pages/login/login').then(m => m.Login)
  },
  {
    path: '',
    component: MainLayout,
    canActivate: [authGuard],
    canActivateChild: [authGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'inicio'
      },
      {
        path: 'inicio',
        title: 'Inicio',
        loadComponent: () =>
          import('./features/inicio/inicio').then(m => m.Inicio)
      },
      {
        path: 'ventas',
        canActivate: [permisoGuard],
        data: { permiso: 'VENTAS_LEER' },
        loadChildren: () =>
          import('./features/ventas/ventas.routes').then(m => m.rutasVentas)
      },
      {
        path: 'categorias',
        title: 'Categorías',
        canActivate: [permisoGuard],
        data: { permiso: 'CATALOGO_LEER' },
        loadComponent: () =>
          import('./features/categorias/pages/categoria-list').then(m => m.CategoriaList)
      },
      {
        path: 'productos',
        title: 'Productos',
        canActivate: [permisoGuard],
        data: { permiso: 'CATALOGO_LEER' },
        loadComponent: () =>
          import('./features/productos/pages/producto-list').then(m => m.ProductoList)
      },
      {
        path: 'clientes',
        title: 'Clientes',
        canActivate: [permisoGuard],
        data: { permiso: 'CLIENTES_LEER' },
        loadComponent: () =>
          import('./features/clientes/pages/cliente-list').then(m => m.ClienteList)
      },
      {
        path: 'reportes',
        title: 'Reportes',
        canActivate: [permisoGuard],
        data: { permiso: 'REPORTES_VER' },
        loadComponent: () =>
          import('./features/reportes/pages/reporte-list').then(m => m.ReporteList)
      },
      {
        path: 'acceso-denegado',
        title: 'Acceso denegado',
        loadComponent: () =>
          import('./shared/pages/acceso-denegado/acceso-denegado')
            .then(m => m.AccesoDenegado)
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'inicio'
  }
];
