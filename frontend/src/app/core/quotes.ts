export interface DailyQuote {
  text: string;
  author: string;
}

// Rotated deterministically by day-of-year so everyone sees the same quote each day.
const QUOTES: DailyQuote[] = [
  { text: 'Кој рано рани, две среќи граби.', author: 'Македонска поговорка' },
  { text: 'Со трпение се стигнува најдалеку.', author: 'Народна мудрост' },
  { text: 'Капка по капка — езеро.', author: 'Македонска поговорка' },
  { text: 'Знаењето е сила, а трудот е клуч.', author: 'Народна мудрост' },
  { text: 'Добриот глас далеку се слуша.', author: 'Македонска поговорка' },
  { text: 'Каков труд — таков и плод.', author: 'Народна мудрост' },
  { text: 'Полека се оди подалеку.', author: 'Македонска поговорка' },
  { text: 'Секој почеток е тежок, но вреди.', author: 'Народна мудрост' },
  { text: 'Умен човек од туѓа грешка учи.', author: 'Македонска поговорка' },
  { text: 'Работата го краси човекот.', author: 'Народна мудрост' },
  { text: 'Дрво без корен не стои, човек без цел не оди.', author: 'Народна мудрост' },
  { text: 'Насмевката е најкратко растојание меѓу луѓето.', author: 'Народна мудрост' },
  { text: 'Секој ден е нова можност.', author: 'Народна мудрост' },
  { text: 'Малку, но со срце.', author: 'Македонска поговорка' },
];

export function quoteOfTheDay(date: Date = new Date()): DailyQuote {
  const start = new Date(date.getFullYear(), 0, 0);
  const diff = date.getTime() - start.getTime();
  const dayOfYear = Math.floor(diff / 86_400_000);
  return QUOTES[dayOfYear % QUOTES.length];
}
