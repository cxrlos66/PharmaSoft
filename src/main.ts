import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { App } from './app/app';
import { rutas } from './app/app.routes';
bootstrapApplication(App, {
  providers: [provideHttpClient(), provideRouter(rutas, withComponentInputBinding())]
}).catch(console.error);
