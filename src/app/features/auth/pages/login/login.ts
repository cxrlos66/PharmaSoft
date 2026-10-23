
import { Component, inject, input, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/auth/auth-service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly returnUrl = input<string>();
  readonly expirada = input<string>();

  protected readonly enviando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly verClave = signal(false);

  protected readonly form = this.fb.group({
    username: ['', [Validators.required, Validators.maxLength(50)]],
    password: ['', [Validators.required, Validators.maxLength(72)]]
  });

  ingresar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (this.enviando()) {
      return;
    }

    this.enviando.set(true);
    this.error.set(null);

    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => {
        this.router.navigateByUrl(this.destino());
      },
      error: (err: HttpErrorResponse) => {
        this.enviando.set(false);
        this.form.controls.password.reset();

        if (err.status === 401) {
          this.error.set('Usuario o contraseña incorrectos.');
        } else if (err.status === 0) {
          this.error.set('No se pudo conectar con el servidor.');
        } else if (err.status === 403) {
          this.error.set('Acceso denegado.');
        } else {
          this.error.set('No se pudo iniciar sesión. Intente nuevamente.');
        }
      }
    });
  }

  private destino(): string {
    const url = this.returnUrl();

    return url && url.startsWith('/') && !url.startsWith('//')
      ? url
      : '/inicio';
  }
}
