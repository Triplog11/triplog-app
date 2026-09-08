import { optimizeImageUrl, IMAGE_PRESETS } from '../imageUrl';

const CLOUD_BASE = 'https://res.cloudinary.com/pvswis5a/image/upload';

describe('optimizeImageUrl', () => {
  describe('Cloudinary URL 변환', () => {
    it('버전 세그먼트가 없는 URL의 upload 뒤에 변환을 삽입한다', () => {
      expect(optimizeImageUrl(`${CLOUD_BASE}/triplog/cards/127642.webp`, 'card'))
        .toBe(`${CLOUD_BASE}/w_800,q_auto,f_auto/triplog/cards/127642.webp`);
    });

    it('버전 세그먼트가 있는 URL은 버전 앞에 변환을 삽입한다', () => {
      expect(optimizeImageUrl(`${CLOUD_BASE}/v1787467863/triplog/cards/127642.webp`, 'thumb'))
        .toBe(`${CLOUD_BASE}/w_400,q_auto,f_auto/v1787467863/triplog/cards/127642.webp`);
    });

    it('프리셋마다 서로 다른 폭을 적용한다', () => {
      const url = `${CLOUD_BASE}/triplog/cards/1.webp`;
      expect(optimizeImageUrl(url, 'thumb')).toContain('w_400');
      expect(optimizeImageUrl(url, 'card')).toContain('w_800');
      expect(optimizeImageUrl(url, 'hero')).toContain('w_1080');
    });

    it('모든 프리셋이 q_auto와 f_auto를 포함한다', () => {
      Object.values(IMAGE_PRESETS).forEach((transform) => {
        expect(transform).toMatch(/q_auto/);
        expect(transform).toMatch(/f_auto/);
      });
    });

    it('프리셋을 생략하면 card 프리셋을 쓴다', () => {
      expect(optimizeImageUrl(`${CLOUD_BASE}/triplog/cards/1.webp`))
        .toContain(IMAGE_PRESETS.card);
    });

    it('알 수 없는 프리셋 이름이면 card 프리셋으로 대체한다', () => {
      expect(optimizeImageUrl(`${CLOUD_BASE}/triplog/cards/1.webp`, 'unknown'))
        .toContain(IMAGE_PRESETS.card);
    });

    it('http URL과 대문자가 섞인 호스트도 처리한다', () => {
      expect(optimizeImageUrl(`http://res.Cloudinary.com/pvswis5a/image/upload/a.webp`, 'thumb'))
        .toBe('http://res.Cloudinary.com/pvswis5a/image/upload/w_400,q_auto,f_auto/a.webp');
    });

    it('폴더명에 밑줄이 있어도 변환으로 오인하지 않는다', () => {
      expect(optimizeImageUrl(`${CLOUD_BASE}/my_folder/cards/1.webp`, 'thumb'))
        .toBe(`${CLOUD_BASE}/w_400,q_auto,f_auto/my_folder/cards/1.webp`);
    });
  });

  describe('원본을 그대로 돌려주는 입력', () => {
    it('이미 변환 파라미터가 붙어 있으면 덧붙이지 않는다', () => {
      const url = `${CLOUD_BASE}/w_400,q_auto,f_auto/triplog/cards/1.webp`;
      expect(optimizeImageUrl(url, 'hero')).toBe(url);
    });

    it('변환 파라미터가 하나뿐이어도 덧붙이지 않는다', () => {
      const url = `${CLOUD_BASE}/c_fill/triplog/cards/1.webp`;
      expect(optimizeImageUrl(url, 'card')).toBe(url);
    });

    it('Cloudinary가 아닌 URL은 그대로 둔다', () => {
      const url = 'https://triplog11.store/cards/10.png';
      expect(optimizeImageUrl(url, 'card')).toBe(url);
    });

    it('Cloudinary 호스트라도 upload 경로가 아니면 그대로 둔다', () => {
      const url = 'https://res.cloudinary.com/pvswis5a/image/fetch/a.webp';
      expect(optimizeImageUrl(url, 'card')).toBe(url);
    });

    it('null, undefined, 빈 문자열을 안전하게 통과시킨다', () => {
      expect(optimizeImageUrl(null, 'card')).toBeNull();
      expect(optimizeImageUrl(undefined, 'card')).toBeUndefined();
      expect(optimizeImageUrl('', 'card')).toBe('');
      expect(optimizeImageUrl('   ', 'card')).toBe('   ');
    });

    it('require()로 불러온 로컬 에셋(숫자·객체)은 손대지 않는다', () => {
      const numericAsset = 42;
      const objectAsset = { uri: 'asset:/images/card.png', width: 100 };
      expect(optimizeImageUrl(numericAsset, 'card')).toBe(numericAsset);
      expect(optimizeImageUrl(objectAsset, 'card')).toBe(objectAsset);
    });
  });
});
