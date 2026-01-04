import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class MonacoService {  // <--- The word 'export' is critical here!
  private loaded = false;
  public loading$ = new Subject<void>();

  load() {
    if (this.loaded) {
      this.loading$.next();
      return;
    }

    // Check if script is already added to avoid duplicates
    if (document.querySelector('script[src="assets/monaco/vs/loader.js"]')) {
        return;
    }

    const script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = 'assets/monaco/vs/loader.js';
    script.onload = () => {
      (window as any).require.config({ paths: { vs: 'assets/monaco/vs' } });
      (window as any).require(['vs/editor/editor.main'], () => {
        this.loaded = true;
        this.loading$.next();
      });
    };
    document.body.appendChild(script);
  }
}