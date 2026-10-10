export interface VentaPorCategoria {
  categoriaId: number;
  categoriaNombre: string;
  cantidadVendida: number;
  unidadesVendidas: number; // Compatibilidad con el reporte anterior.
  montoTotal: number;
}

export interface ProductoMasVendido {
  productoId: number;
  productoNombre: string;
  unidadesVendidas: number;
  montoTotal: number;
}
