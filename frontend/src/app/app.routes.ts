import { Routes } from '@angular/router';
import { LinkDirectory } from './components/link-directory/link-directory';
import { CategoryPage } from './components/category-page/category-page';

export const routes: Routes = [
  { path: '', component: LinkDirectory },
  { path: ':id', component: CategoryPage },
];
