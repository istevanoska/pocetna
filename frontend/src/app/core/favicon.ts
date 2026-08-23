// Builds a Google favicon-service URL for a site, so the directory shows each
// site's real logo instead of a plain bullet list.
export function faviconUrl(url: string, size = 64): string {
  try {
    const host = new URL(url).hostname;
    return `https://www.google.com/s2/favicons?domain=${host}&sz=${size}`;
  } catch {
    return `https://www.google.com/s2/favicons?domain=${encodeURIComponent(url)}&sz=${size}`;
  }
}
