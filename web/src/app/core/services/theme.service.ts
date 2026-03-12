import { Injectable, signal, computed } from '@angular/core';

type Theme = 'light' | 'dark';

const STORAGE_KEY = 'fastdel-theme';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly _theme = signal<Theme>('light');

  readonly isDark = computed(() => this._theme() === 'dark');

  init(): void {
    const stored = localStorage.getItem(STORAGE_KEY) as Theme | null;
    const preferred = stored
      ?? (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
    this.apply(preferred);
  }

  toggle(): void {
    this.apply(this._theme() === 'light' ? 'dark' : 'light');
  }

  private apply(theme: Theme): void {
    this._theme.set(theme);
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem(STORAGE_KEY, theme);
  }
}
