// A distinct accent color per category, so the directory reads like a vibrant,
// organized portal instead of a wall of identical red cards.
const CATEGORY_COLORS: Record<string, string> = {
  vesti: '#e0233d',        // news — red
  sport: '#16a34a',        // sport — green
  vlada: '#2563eb',        // government — blue
  banki: '#0891b2',        // banking — cyan
  obrazovanie: '#7c3aed',  // education — violet
  biznis: '#d97706',       // business — amber
  tehnologija: '#0ea5e9',  // tech — sky
  zabava: '#db2777',       // entertainment — pink
  mapi: '#059669',         // maps/transport — emerald
  'pošta': '#ea580c',      // post/utilities — orange
  turizam: '#0d9488',      // travel — teal
};

const FALLBACK = '#6366f1';

export function categoryColor(id: string): string {
  return CATEGORY_COLORS[id] ?? FALLBACK;
}
