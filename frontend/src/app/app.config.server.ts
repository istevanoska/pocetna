import { ApplicationConfig, mergeApplicationConfig } from '@angular/core';
import { provideServerRendering, withRoutes } from '@angular/ssr';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { appConfig } from './app.config';
import { serverRoutes } from './app.routes.server';
import { prerenderApiInterceptor } from './core/prerender-api.interceptor';

/**
 * Browser config plus the two things only the prerenderer needs: the route render
 * modes, and an interceptor that answers /api/links from the build-time snapshot
 * because no backend is running during the build.
 */
const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(withRoutes(serverRoutes)),
    provideHttpClient(withInterceptors([prerenderApiInterceptor])),
  ],
};

export const config = mergeApplicationConfig(appConfig, serverConfig);
