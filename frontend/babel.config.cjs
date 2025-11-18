const presets = [
  ['@babel/preset-env', { targets: { node: 'current' } }],
  '@babel/preset-react',
];

const plugins = [
  ['@babel/plugin-syntax-import-meta'],
];

module.exports = { presets, plugins };
