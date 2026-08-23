const CATEGORY_ICON_MAP: Record<string, string> = {
  newspaper: 'newspaper',
  trophy: 'trophy',
  'building-columns': 'landmark',
  landmark: 'coins',
  'graduation-cap': 'graduation-cap',
  briefcase: 'briefcase',
  microchip: 'cpu',
  tv: 'tv',
  'map-location-dot': 'map-pin',
  envelope: 'mail',
  plane: 'plane',
};

export function categoryIcon(key: string): string {
  return CATEGORY_ICON_MAP[key] ?? 'external';
}
