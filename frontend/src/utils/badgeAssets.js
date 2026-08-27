import { BadgeAssets, AppellationAssets } from '../assets';

/**
 * Returns a fallback local badge icon based on badgeName keywords.
 * @param {string} badgeName
 * @returns {any} require image source
 */
export function getBadgeFallback(badgeName = '') {
  const name = (badgeName || '').toLowerCase();

  if (name.includes('첫') || name.includes('방문') || name.includes('first')) {
    return BadgeAssets.firstVisit;
  }
  if (name.includes('서울') || name.includes('seoul') || name.includes('정복')) {
    return BadgeAssets.seoulConqueror;
  }
  if (name.includes('10') || name.includes('수집') || name.includes('collector')) {
    return BadgeAssets.cardCollector10;
  }
  if (name.includes('기록') || name.includes('리뷰') || name.includes('review')) {
    return BadgeAssets.reviewer;
  }
  if (name.includes('주말') || name.includes('weekend')) {
    return BadgeAssets.weekendTraveler;
  }
  if (name.includes('탐험') || name.includes('지역') || name.includes('explorer')) {
    return BadgeAssets.regionExplorer;
  }

  return BadgeAssets.firstVisit;
}

/**
 * Returns a fallback local appellation icon based on appellationName keywords.
 * @param {string} appellationName
 * @returns {any} require image source
 */
export function getAppellationFallback(appellationName = '') {
  const name = (appellationName || '').toLowerCase();

  if (name.includes('초보') || name.includes('novice')) {
    return AppellationAssets.novice;
  }
  if (name.includes('탐험') || name.includes('explorer')) {
    return AppellationAssets.explorer;
  }
  if (name.includes('수집') || name.includes('collector')) {
    return AppellationAssets.collector;
  }
  if (name.includes('마스터') || name.includes('master')) {
    return AppellationAssets.master;
  }

  return AppellationAssets.novice;
}
