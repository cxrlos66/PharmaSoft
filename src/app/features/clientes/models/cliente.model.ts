export interface Cliente {
  id: number; dni: string; nombres: string; apellidos: string;
  email: string | null; telefono: string | null; direccion: string | null; estado: boolean;
}
export type ClienteDatos = Omit<Cliente, 'id'>;
