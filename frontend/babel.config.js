module.exports = function (api) {
  api.cache(true);
  return {
    // babel-preset-expo(SDK 54)가 react-native-worklets/plugin을 자동 구성함
    presets: ['babel-preset-expo'],
  };
};
