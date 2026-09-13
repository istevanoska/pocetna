import { BootstrapContext, bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';
import { config } from './app/app.config.server';

/**
 * The prerenderer creates a platform per render and hands it to this function as the
 * BootstrapContext. It has to be forwarded to bootstrapApplication — without it the
 * build fails with NG0401 "Missing Platform" while extracting routes.
 */
const bootstrap = (context: BootstrapContext) => bootstrapApplication(App, config, context);

export default bootstrap;
