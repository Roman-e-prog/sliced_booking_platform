import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class HtmlStripService {
  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

  stripHtml(html: string): string {
    if (isPlatformBrowser(this.platformId)) {
      const div = document.createElement('div');
      div.innerHTML = html;
      return div.textContent || div.innerText || '';
    }
    return html;
  }
}