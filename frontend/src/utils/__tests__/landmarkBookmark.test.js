import { findLandmarkBookmarkId } from '../landmarkBookmark';
import { formatLandmarkProgress } from '../landmarkProgress';

describe('findLandmarkBookmarkId', () => {
  const page = (bookmarks, totalPages) => ({ bookmarks, totalPages });

  it('찜 목록에서 랜드마크의 bookmarkId 를 찾는다', async () => {
    const fetchPage = jest.fn().mockResolvedValue(page([
      { bookmarkId: 7, landmarkId: 10 },
      { bookmarkId: 8, landmarkId: 11 },
    ], 1));
    await expect(findLandmarkBookmarkId(fetchPage, 11)).resolves.toBe(8);
  });

  it('다음 페이지까지 넘겨 가며 찾는다', async () => {
    const fetchPage = jest.fn()
      .mockResolvedValueOnce(page([{ bookmarkId: 1, landmarkId: 1 }], 2))
      .mockResolvedValueOnce(page([{ bookmarkId: 2, landmarkId: 99 }], 2));
    await expect(findLandmarkBookmarkId(fetchPage, 99)).resolves.toBe(2);
    expect(fetchPage).toHaveBeenNthCalledWith(2, 1);
  });

  it('없으면 null 을 돌려주고 마지막 페이지에서 멈춘다', async () => {
    const fetchPage = jest.fn().mockResolvedValue(page([{ bookmarkId: 1, landmarkId: 1 }], 1));
    await expect(findLandmarkBookmarkId(fetchPage, 5)).resolves.toBeNull();
    expect(fetchPage).toHaveBeenCalledTimes(1);
  });

  it('id 타입이 문자열이어도 같은 랜드마크로 본다', async () => {
    const fetchPage = jest.fn().mockResolvedValue(page([{ bookmarkId: 3, landmarkId: '42' }], 1));
    await expect(findLandmarkBookmarkId(fetchPage, 42)).resolves.toBe(3);
  });
});

describe('formatLandmarkProgress', () => {
  it('획득 수와 전체 수가 있으면 "1/3장" 으로 표시한다', () => {
    expect(formatLandmarkProgress({ acquiredLandmarkCount: 1, totalLandmarkCount: 3 })).toBe('1/3장');
  });

  it('0장도 그대로 표시한다', () => {
    expect(formatLandmarkProgress({ acquiredLandmarkCount: 0, totalLandmarkCount: 3 })).toBe('0/3장');
  });

  it('서버가 개수를 주지 않으면 null', () => {
    expect(formatLandmarkProgress({ completionRate: 0.3 })).toBeNull();
    expect(formatLandmarkProgress(null)).toBeNull();
  });
});
